package mgw

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"sync"

	discovery "github.com/envoyproxy/go-control-plane/envoy/service/discovery/v3"
	"github.com/wso2/micro-gw/configs"
)

type Callbacks struct {
	Signal   chan struct{}
	Debug    bool
	Fetches  int
	Requests int
	mu       sync.Mutex
}

func (cb *Callbacks) Report() {
	cb.mu.Lock()
	defer cb.mu.Unlock()
	fmt.Printf("server callbacks fetches=%d requests=%d\n", cb.Fetches, cb.Requests)
}
func (cb *Callbacks) OnStreamOpen(_ context.Context, id int64, typ string) error {
	if cb.Debug {
		fmt.Printf("stream %d open for %s\n", id, typ)
	}
	return nil
}
func (cb *Callbacks) OnStreamClosed(id int64) {
	if cb.Debug {
		fmt.Printf("stream %d closed\n", id)
		log.Printf("stream %d closed\n", id)
	}
}
func (cb *Callbacks) OnStreamRequest(id int64, drq *discovery.DiscoveryRequest) error {
	fmt.Printf("on stream request %d\n", id)
	s, _ := json.MarshalIndent(drq, "", "\t")
	fmt.Println("request : " + string(s))
	fmt.Printf("on stream request  %d ----  \n", id)

	nodeId := drq.Node.GetId()
	conf, _ := configs.ReadConfigs()
	updateEnvoyForSpecificNode(conf.Apis.Location, nodeId)

	cb.mu.Lock()
	defer cb.mu.Unlock()
	cb.Requests++
	if cb.Signal != nil {
		close(cb.Signal)
		cb.Signal = nil
	}
	return nil
}
func (cb *Callbacks) OnStreamResponse(id int64, drq *discovery.DiscoveryRequest, dresp *discovery.DiscoveryResponse) {
	fmt.Printf("on stream response  %d \n", id)
	resp, _ := json.MarshalIndent(dresp, "", "\t")
	fmt.Println("response : " + string(resp))
	fmt.Printf("on stream response %d ---- \n", id)

}
func (cb *Callbacks) OnFetchRequest(_ context.Context, req *discovery.DiscoveryRequest) error {
	fmt.Println("on fetch request")
	s, _ := json.MarshalIndent(req, "", "\t")
	fmt.Println("request : " + string(s))
	fmt.Println("on fetch request ---- ")

	cb.mu.Lock()
	defer cb.mu.Unlock()
	cb.Fetches++
	if cb.Signal != nil {
		close(cb.Signal)
		cb.Signal = nil
	}
	return nil
}
func (cb *Callbacks) OnFetchResponse(*discovery.DiscoveryRequest, *discovery.DiscoveryResponse) {
	fmt.Println("fetch response received")
}
