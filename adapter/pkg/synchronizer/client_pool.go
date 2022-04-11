/*
 * Package "synchronizer" contains artifacts relate to fetching APIs and
 * API related updates from the control plane event-hub.
 * This file contains functions to retrieve APIs and API updates.
 */

package synchronizer

import (
	"net/http"
	"sync"
	"time"

	"github.com/wso2/product-microgateway/adapter/pkg/loggers"
)

type worker struct {
	id                       int
	internalQueue            <-chan WorkerRequest
	processFunc              processHTTPRequest
	delayAfterFaultInSeconds time.Duration
	// done          *sync.WaitGroup
	// work          chan Work
	// quit          chan bool
}

// WorkerRequest is the task which can be submitted to the pool.
type WorkerRequest struct {
	Req                http.Request
	APIUUID            *string
	labels             []string
	SyncAPIRespChannel chan SyncAPIResponse
}

// Pool is the worker pool which is handling
type Pool struct {
	internalQueue chan WorkerRequest
	workers       []*worker
	quit          chan bool
	// TODO: (VirajSalaka) remove
	processFunc processHTTPRequest
	timeout     time.Duration
}

type processHTTPRequest func(*http.Request, *string, []string, chan SyncAPIResponse) bool

func (w *worker) ProcessFunction() {
	for workerReq := range w.internalQueue {
		responseReceived := w.processFunc(&workerReq.Req, workerReq.APIUUID, workerReq.labels, workerReq.SyncAPIRespChannel)
		if !responseReceived {
			// TODO: (VirajSalaka) make it configurable
			time.Sleep(5 * time.Second)
		}

	}
}

var (
	// WorkerPool is the thread pool responsible for sending the control plane request to fetch APIs
	WorkerPool        *Pool
	oncePoolInitiated sync.Once
)

// InitializeWorkerPool creates the Worker Pool used for the Control Plane Rest API invocations.
// maxWorkers indicate the maximum number of parallel workers sending requests to the control plane.
// jobQueueCapacity indicate the maximum number of requests can kept inside a single worker's queue.
// delayForFaultRequests indicate the delay a worker enforce (in seconds) when a fault response is received.
func InitializeWorkerPool(maxWorkers, jobQueueCapacity int, delayForFaultRequests time.Duration) {
	// TODO: (VirajSalaka) Think on whether this could be moved to global adapter seamlessly.
	oncePoolInitiated.Do(func() {
		WorkerPool = newWorkerPool(maxWorkers, jobQueueCapacity, delayForFaultRequests)
	})
}

func newWorkerPool(maxWorkers, jobQueueCapacity int, delayForFaultRequests time.Duration) *Pool {
	if jobQueueCapacity <= 0 {
		jobQueueCapacity = 100
	}
	requestChannel := make(chan WorkerRequest, jobQueueCapacity)
	workers := make([]*worker, maxWorkers)

	// create workers
	for i := 0; i < maxWorkers; i++ {
		workers[i] = &worker{
			id:                       i,
			internalQueue:            requestChannel,
			processFunc:              SendRequestToControlPlane,
			delayAfterFaultInSeconds: delayForFaultRequests,
		}
		go workers[i].ProcessFunction()
		loggers.LoggerSync.Infof("ControlPlane processing worker %d spawned.", i)
	}

	return &Pool{
		internalQueue: requestChannel,
		workers:       workers,
		quit:          make(chan bool),
	}
}

// Enqueue Tries to enqueue but fails if queue is full
func (q *Pool) Enqueue(req WorkerRequest) bool {
	select {
	case q.internalQueue <- req:
		return true
	default:
		return false
	}
}

// EnqueueWithTimeout Tries to enqueue but fails if queue becomes not vacant within the defined period of time.
func (q *Pool) EnqueueWithTimeout(req WorkerRequest) bool {
	// TODO: Use this
	timeout := q.timeout
	if timeout <= 0 {
		timeout = 1 * time.Second
	}

	ch := make(chan bool, 1)
	t := time.AfterFunc(timeout, func() {
		ch <- false
	})
	defer func() {
		t.Stop()
	}()

	select {
	case q.internalQueue <- req:
		return true
	case <-ch:
		return false
	}
}
