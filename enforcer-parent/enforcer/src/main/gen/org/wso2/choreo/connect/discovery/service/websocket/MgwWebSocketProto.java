// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: wso2/discovery/service/websocket/frame_service.proto

package org.wso2.choreo.connect.discovery.service.websocket;

public final class MgwWebSocketProto {
  private MgwWebSocketProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_ExtAuthzMetadataEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_ExtAuthzMetadataEntry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n4wso2/discovery/service/websocket/frame" +
      "_service.proto\0223envoy.extensions.filters" +
      ".http.mgw_wasm_websocket.v3\"\354\002\n\025WebSocke" +
      "tFrameRequest\022\017\n\007node_id\030\001 \001(\t\022O\n\010metada" +
      "ta\030\002 \001(\0132=.envoy.extensions.filters.http" +
      ".mgw_wasm_websocket.v3.Metadata\022\024\n\014frame" +
      "_length\030\003 \001(\005\022\021\n\tremote_ip\030\004 \001(\t\022\017\n\007payl" +
      "oad\030\005 \001(\014\022n\n\tdirection\030\006 \001(\0162[.envoy.ext" +
      "ensions.filters.http.mgw_wasm_websocket." +
      "v3.WebSocketFrameRequest.MessageDirectio" +
      "n\022\027\n\017apim_error_code\030\007 \001(\005\".\n\020MessageDir" +
      "ection\022\013\n\007PUBLISH\020\000\022\r\n\tSUBSCRIBE\020\001\"\341\001\n\026W" +
      "ebSocketFrameResponse\022h\n\016throttle_state\030" +
      "\001 \001(\0162P.envoy.extensions.filters.http.mg" +
      "w_wasm_websocket.v3.WebSocketFrameRespon" +
      "se.Code\022\027\n\017throttle_period\030\002 \001(\003\022\027\n\017apim" +
      "_error_code\030\003 \001(\005\"+\n\004Code\022\013\n\007UNKNOWN\020\000\022\006" +
      "\n\002OK\020\001\022\016\n\nOVER_LIMIT\020\002\"\264\001\n\010Metadata\022o\n\022e" +
      "xt_authz_metadata\030\001 \003(\0132S.envoy.extensio" +
      "ns.filters.http.mgw_wasm_websocket.v3.Me" +
      "tadata.ExtAuthzMetadataEntry\0327\n\025ExtAuthz" +
      "MetadataEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(" +
      "\t:\0028\0012\313\001\n\025WebSocketFrameService\022\261\001\n\020Publ" +
      "ishFrameData\022J.envoy.extensions.filters." +
      "http.mgw_wasm_websocket.v3.WebSocketFram" +
      "eRequest\032K.envoy.extensions.filters.http" +
      ".mgw_wasm_websocket.v3.WebSocketFrameRes" +
      "ponse\"\000(\0010\001BJ\n3org.wso2.choreo.connect.d" +
      "iscovery.service.websocketB\021MgwWebSocket" +
      "ProtoP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameRequest_descriptor,
        new java.lang.String[] { "NodeId", "Metadata", "FrameLength", "RemoteIp", "Payload", "Direction", "ApimErrorCode", });
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_WebSocketFrameResponse_descriptor,
        new java.lang.String[] { "ThrottleState", "ThrottlePeriod", "ApimErrorCode", });
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_descriptor,
        new java.lang.String[] { "ExtAuthzMetadata", });
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_ExtAuthzMetadataEntry_descriptor =
      internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_descriptor.getNestedTypes().get(0);
    internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_ExtAuthzMetadataEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_envoy_extensions_filters_http_mgw_wasm_websocket_v3_Metadata_ExtAuthzMetadataEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
