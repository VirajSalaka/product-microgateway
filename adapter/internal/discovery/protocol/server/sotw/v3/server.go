// Code generated by protoc. DO NOT EDIT.
// Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

// Package sotw provides an implementation of GRPC SoTW (State of The World) part of XDS server
package sotw

import (
	"context"
	"errors"
	"strconv"
	"sync/atomic"

	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"

	core "github.com/envoyproxy/go-control-plane/envoy/config/core/v3"
	discovery "github.com/envoyproxy/go-control-plane/envoy/service/discovery/v3"
	"github.com/envoyproxy/go-control-plane/pkg/cache/v3"
	"github.com/envoyproxy/go-control-plane/pkg/server/sotw/v3"
	"github.com/wso2/micro-gw/internal/discovery/protocol/resource/v3"
)

// NewServer creates handlers from a config watcher and callbacks.
func NewServer(ctx context.Context, config cache.ConfigWatcher, callbacks sotw.Callbacks) sotw.Server {
	return &server{cache: config, callbacks: callbacks, ctx: ctx}
}

type server struct {
	cache     cache.ConfigWatcher
	callbacks sotw.Callbacks
	ctx       context.Context

	// streamCount for counting bi-di streams
	streamCount int64
}

// watches for all xDS resource types
type watches struct {
	configs                   chan cache.Response
	apis                      chan cache.Response
	subscriptionList          chan cache.Response
	applicationList           chan cache.Response
	apiList                   chan cache.Response
	applicationPolicyList     chan cache.Response
	subscriptionPolicyList    chan cache.Response
	applicationKeyMappingList chan cache.Response
	keyManagers               chan cache.Response
	revokedTokens             chan cache.Response
	throttleData              chan cache.Response

	configCancel                    func()
	apiCancel                       func()
	subscriptionListCancel          func()
	applicationListCancel           func()
	apiListCancel                   func()
	applicationPolicyListCancel     func()
	subscriptionPolicyListCancel    func()
	applicationKeyMappingListCancel func()
	keyManagerCancel                func()
	revokedTokenCancel              func()
	throttleDataCancel              func()

	configNonce                    string
	apiNonce                       string
	subscriptionListNonce          string
	applicationListNonce           string
	apiListNonce                   string
	applicationPolicyListNonce     string
	subscriptionPolicyListNonce    string
	applicationKeyMappingListNonce string
	keyManagerNonce                string
	revokedTokenNonce              string
	throttleDataNonce              string

	// Opaque resources share a muxed channel. Nonces and watch cancellations are indexed by type URL.
	responses     chan cache.Response
	cancellations map[string]func()
	nonces        map[string]string
	terminations  map[string]chan struct{}
}

// Initialize all watches
func (values *watches) Init() {
	// muxed channel needs a buffer to release go-routines populating it
	values.responses = make(chan cache.Response, 11)
	values.cancellations = make(map[string]func())
	values.nonces = make(map[string]string)
	values.terminations = make(map[string]chan struct{})
}

// Token response value used to signal a watch failure in muxed watches.
var errorResponse = &cache.RawResponse{}

// Cancel all watches
func (values *watches) Cancel() {
	if values.configCancel != nil {
		values.configCancel()
	}
	if values.apiCancel != nil {
		values.apiCancel()
	}
	if values.subscriptionListCancel != nil {
		values.subscriptionListCancel()
	}
	if values.applicationListCancel != nil {
		values.applicationListCancel()
	}
	if values.apiListCancel != nil {
		values.apiListCancel()
	}
	if values.applicationPolicyListCancel != nil {
		values.applicationPolicyListCancel()
	}
	if values.subscriptionPolicyListCancel != nil {
		values.subscriptionPolicyListCancel()
	}
	if values.applicationKeyMappingListCancel != nil {
		values.applicationKeyMappingListCancel()
	}
	if values.keyManagerCancel != nil {
		values.keyManagerCancel()
	}
	if values.revokedTokenCancel != nil {
		values.revokedTokenCancel()
	}
	if values.throttleDataCancel != nil {
		values.throttleDataCancel()
	}

	for _, cancel := range values.cancellations {
		if cancel != nil {
			cancel()
		}
	}
	for _, terminate := range values.terminations {
		close(terminate)
	}
}

// process handles a bi-di stream request
func (s *server) process(stream sotw.Stream, reqCh <-chan *discovery.DiscoveryRequest, defaultTypeURL string) error {
	// increment stream count
	streamID := atomic.AddInt64(&s.streamCount, 1)

	// unique nonce generator for req-resp pairs per xDS stream; the server
	// ignores stale nonces. nonce is only modified within send() function.
	var streamNonce int64

	// a collection of stack allocated watches per request type
	var values watches
	values.Init()
	defer func() {
		values.Cancel()
		if s.callbacks != nil {
			s.callbacks.OnStreamClosed(streamID)
		}
	}()

	// sends a response by serializing to protobuf Any
	send := func(resp cache.Response, typeURL string) (string, error) {
		if resp == nil {
			return "", errors.New("missing response")
		}

		out, err := resp.GetDiscoveryResponse()
		if err != nil {
			return "", err
		}

		// increment nonce
		streamNonce = streamNonce + 1
		out.Nonce = strconv.FormatInt(streamNonce, 10)
		if s.callbacks != nil {
			s.callbacks.OnStreamResponse(streamID, resp.GetRequest(), out)
		}
		return out.Nonce, stream.Send(out)
	}

	if s.callbacks != nil {
		if err := s.callbacks.OnStreamOpen(stream.Context(), streamID, defaultTypeURL); err != nil {
			return err
		}
	}

	// node may only be set on the first discovery request
	var node = &core.Node{}

	for {
		select {
		case <-s.ctx.Done():
			return nil
			// config watcher can send the requested resources types in any order
		case resp, more := <-values.configs:
			if !more {
				return status.Errorf(codes.Unavailable, "configs watch failed")
			}
			nonce, err := send(resp, resource.ConfigType)
			if err != nil {
				return err
			}
			values.configNonce = nonce

		case resp, more := <-values.apis:
			if !more {
				return status.Errorf(codes.Unavailable, "apis watch failed")
			}
			nonce, err := send(resp, resource.APIType)
			if err != nil {
				return err
			}
			values.apiNonce = nonce

		case resp, more := <-values.subscriptionList:
			if !more {
				return status.Errorf(codes.Unavailable, "subscriptionList watch failed")
			}
			nonce, err := send(resp, resource.SubscriptionListType)
			if err != nil {
				return err
			}
			values.subscriptionListNonce = nonce

		case resp, more := <-values.apiList:
			if !more {
				return status.Errorf(codes.Unavailable, "apiList watch failed")
			}
			nonce, err := send(resp, resource.APIListType)
			if err != nil {
				return err
			}
			values.apiListNonce = nonce

		case resp, more := <-values.applicationList:
			if !more {
				return status.Errorf(codes.Unavailable, "applicationList watch failed")
			}
			nonce, err := send(resp, resource.ApplicationListType)
			if err != nil {
				return err
			}
			values.applicationListNonce = nonce

		case resp, more := <-values.applicationPolicyList:
			if !more {
				return status.Errorf(codes.Unavailable, "applicationPolicyList watch failed")
			}
			nonce, err := send(resp, resource.ApplicationPolicyListType)
			if err != nil {
				return err
			}
			values.applicationPolicyListNonce = nonce

		case resp, more := <-values.subscriptionPolicyList:
			if !more {
				return status.Errorf(codes.Unavailable, "subscriptionPolicyList watch failed")
			}
			nonce, err := send(resp, resource.SubscriptionPolicyListType)
			if err != nil {
				return err
			}
			values.subscriptionPolicyListNonce = nonce

		case resp, more := <-values.applicationKeyMappingList:
			if !more {
				return status.Errorf(codes.Unavailable, "applicationKeyMappingList watch failed")
			}
			nonce, err := send(resp, resource.ApplicationKeyMappingListType)
			if err != nil {
				return err
			}
			values.applicationKeyMappingListNonce = nonce

		case resp, more := <-values.keyManagers:
			if !more {
				return status.Errorf(codes.Unavailable, "keyManagers watch failed")
			}
			nonce, err := send(resp, resource.KeyManagerType)
			if err != nil {
				return err
			}
			values.keyManagerNonce = nonce

		case resp, more := <-values.revokedTokens:
			if !more {
				return status.Errorf(codes.Unavailable, "revoked tokens watch failed")
			}
			nonce, err := send(resp, resource.RevokedTokensType)
			if err != nil {
				return err
			}
			values.revokedTokenNonce = nonce

		case resp, more := <-values.throttleData:
			if !more {
				return status.Errorf(codes.Unavailable, "throttle data watch failed")
			}
			nonce, err := send(resp, resource.ThrottleDataType)
			if err != nil {
				return err
			}
			values.throttleDataNonce = nonce

		case resp, more := <-values.responses:
			if more {
				if resp == errorResponse {
					return status.Errorf(codes.Unavailable, "resource watch failed")
				}
				typeUrl := resp.GetRequest().TypeUrl
				nonce, err := send(resp, typeUrl)
				if err != nil {
					return err
				}
				values.nonces[typeUrl] = nonce
			}

		case req, more := <-reqCh:
			// input stream ended or errored out
			if !more {
				return nil
			}
			if req == nil {
				return status.Errorf(codes.Unavailable, "empty request")
			}

			// node field in discovery request is delta-compressed
			if req.Node != nil {
				node = req.Node
			} else {
				req.Node = node
			}

			// nonces can be reused across streams; we verify nonce only if nonce is not initialized
			nonce := req.GetResponseNonce()

			// type URL is required for ADS but is implicit for xDS
			if defaultTypeURL == resource.AnyType {
				if req.TypeUrl == "" {
					return status.Errorf(codes.InvalidArgument, "type URL is required for ADS")
				}
			} else if req.TypeUrl == "" {
				req.TypeUrl = defaultTypeURL
			}

			if s.callbacks != nil {
				if err := s.callbacks.OnStreamRequest(streamID, req); err != nil {
					return err
				}
			}

			// cancel existing watches to (re-)request a newer version
			switch {
			case req.TypeUrl == resource.ConfigType:
				if values.configNonce == "" || values.configNonce == nonce {
					if values.configCancel != nil {
						values.configCancel()
					}
					values.configs, values.configCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.APIType:
				if values.apiNonce == "" || values.apiNonce == nonce {
					if values.apiCancel != nil {
						values.apiCancel()
					}
					values.apis, values.apiCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.SubscriptionListType:
				if values.subscriptionListNonce == "" || values.subscriptionListNonce == nonce {
					if values.subscriptionListCancel != nil {
						values.subscriptionListCancel()
					}
					values.subscriptionList, values.subscriptionListCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.APIListType:
				if values.apiListNonce == "" || values.apiListNonce == nonce {
					if values.apiListCancel != nil {
						values.apiListCancel()
					}
					values.apiList, values.apiListCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.ApplicationListType:
				if values.applicationListNonce == "" || values.applicationListNonce == nonce {
					if values.applicationListCancel != nil {
						values.applicationListCancel()
					}
					values.applicationList, values.applicationListCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.ApplicationPolicyListType:
				if values.applicationPolicyListNonce == "" || values.applicationPolicyListNonce == nonce {
					if values.applicationPolicyListCancel != nil {
						values.applicationPolicyListCancel()
					}
					values.applicationPolicyList, values.applicationPolicyListCancel = s.cache.CreateWatch(req)
				}

			case req.TypeUrl == resource.SubscriptionPolicyListType:
				if values.subscriptionPolicyListNonce == "" || values.subscriptionPolicyListNonce == nonce {
					if values.subscriptionPolicyListCancel != nil {
						values.subscriptionPolicyListCancel()
					}
					values.subscriptionPolicyList, values.subscriptionPolicyListCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.ApplicationKeyMappingListType:
				if values.applicationKeyMappingListNonce == "" || values.applicationKeyMappingListNonce == nonce {
					if values.applicationKeyMappingListCancel != nil {
						values.applicationKeyMappingListCancel()
					}
					values.applicationKeyMappingList, values.applicationKeyMappingListCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.KeyManagerType:
				if values.keyManagerNonce == "" || values.keyManagerNonce == nonce {
					if values.keyManagerCancel != nil {
						values.keyManagerCancel()
					}
					values.keyManagers, values.keyManagerCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.RevokedTokensType:
				if values.revokedTokenNonce == "" || values.revokedTokenNonce == nonce {
					if values.revokedTokenCancel != nil {
						values.revokedTokenCancel()
					}
					values.revokedTokens, values.revokedTokenCancel = s.cache.CreateWatch(req)
				}
			case req.TypeUrl == resource.ThrottleDataType:
				if values.throttleDataNonce == "" || values.throttleDataNonce == nonce {
					if values.throttleDataCancel != nil {
						values.throttleDataCancel()
					}
					values.throttleData, values.throttleDataCancel = s.cache.CreateWatch(req)
				}
			default:
				typeUrl := req.TypeUrl
				responseNonce, seen := values.nonces[typeUrl]
				if !seen || responseNonce == nonce {
					// We must signal goroutine termination to prevent a race between the cancel closing the watch
					// and the producer closing the watch.
					if terminate, exists := values.terminations[typeUrl]; exists {
						close(terminate)
					}
					if cancel, seen := values.cancellations[typeUrl]; seen && cancel != nil {
						cancel()
					}
					var watch chan cache.Response
					watch, values.cancellations[typeUrl] = s.cache.CreateWatch(req)
					// Muxing watches across multiple type URLs onto a single channel requires spawning
					// a go-routine. Golang does not allow selecting over a dynamic set of channels.
					terminate := make(chan struct{})
					values.terminations[typeUrl] = terminate
					go func() {
						select {
						case resp, more := <-watch:
							if more {
								values.responses <- resp
							} else {
								// Check again if the watch is cancelled.
								select {
								case <-terminate: // do nothing
								default:
									// We cannot close the responses channel since it can be closed twice.
									// Instead we send a fake error response.
									values.responses <- errorResponse
								}
							}
							break
						case <-terminate:
							break
						}
					}()
				}
			}
		}
	}
}

// StreamHandler converts a blocking read call to channels and initiates stream processing
func (s *server) StreamHandler(stream sotw.Stream, typeURL string) error {
	// a channel for receiving incoming requests
	reqCh := make(chan *discovery.DiscoveryRequest)
	reqStop := int32(0)
	go func() {
		for {
			req, err := stream.Recv()
			if atomic.LoadInt32(&reqStop) != 0 {
				return
			}
			if err != nil {
				close(reqCh)
				return
			}
			select {
			case reqCh <- req:
			case <-s.ctx.Done():
				return
			}
		}
	}()

	err := s.process(stream, reqCh, typeURL)

	// prevents writing to a closed channel if send failed on blocked recv
	// TODO(kuat) figure out how to unblock recv through gRPC API
	atomic.StoreInt32(&reqStop, 1)

	return err
}
