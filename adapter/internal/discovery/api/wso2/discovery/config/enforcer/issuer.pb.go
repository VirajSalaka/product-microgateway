// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.25.0
// 	protoc        v3.14.0
// source: wso2/discovery/config/enforcer/issuer.proto

package enforcer

import (
	proto "github.com/golang/protobuf/proto"
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

// This is a compile-time assertion that a sufficiently up-to-date version
// of the legacy proto package is being used.
const _ = proto.ProtoPackageIsVersion4

// Token issuer model
type Issuer struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Issuer name
	Name string `protobuf:"bytes,1,opt,name=name,proto3" json:"name,omitempty"`
	// Issuer identifier
	Issuer string `protobuf:"bytes,2,opt,name=issuer,proto3" json:"issuer,omitempty"`
	// Alias of the local certificate to be used for token validation
	// with this issuer
	CertificateAlias string `protobuf:"bytes,3,opt,name=certificateAlias,proto3" json:"certificateAlias,omitempty"`
	// JWKS endpoint of the issuer
	JwksURL string `protobuf:"bytes,4,opt,name=jwksURL,proto3" json:"jwksURL,omitempty"`
	// Enable or disable subscription validation for this issuer
	ValidateSubscription bool `protobuf:"varint,5,opt,name=validateSubscription,proto3" json:"validateSubscription,omitempty"`
	// JWT claim that communicates the consumerKey value
	ConsumerKeyClaim string `protobuf:"bytes,6,opt,name=consumerKeyClaim,proto3" json:"consumerKeyClaim,omitempty"`
	// FilePath of the public certificate mounted in enforcer container
	CertificateFilePath string `protobuf:"bytes,7,opt,name=certificateFilePath,proto3" json:"certificateFilePath,omitempty"`
}

func (x *Issuer) Reset() {
	*x = Issuer{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wso2_discovery_config_enforcer_issuer_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Issuer) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Issuer) ProtoMessage() {}

func (x *Issuer) ProtoReflect() protoreflect.Message {
	mi := &file_wso2_discovery_config_enforcer_issuer_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Issuer.ProtoReflect.Descriptor instead.
func (*Issuer) Descriptor() ([]byte, []int) {
	return file_wso2_discovery_config_enforcer_issuer_proto_rawDescGZIP(), []int{0}
}

func (x *Issuer) GetName() string {
	if x != nil {
		return x.Name
	}
	return ""
}

func (x *Issuer) GetIssuer() string {
	if x != nil {
		return x.Issuer
	}
	return ""
}

func (x *Issuer) GetCertificateAlias() string {
	if x != nil {
		return x.CertificateAlias
	}
	return ""
}

func (x *Issuer) GetJwksURL() string {
	if x != nil {
		return x.JwksURL
	}
	return ""
}

func (x *Issuer) GetValidateSubscription() bool {
	if x != nil {
		return x.ValidateSubscription
	}
	return false
}

func (x *Issuer) GetConsumerKeyClaim() string {
	if x != nil {
		return x.ConsumerKeyClaim
	}
	return ""
}

func (x *Issuer) GetCertificateFilePath() string {
	if x != nil {
		return x.CertificateFilePath
	}
	return ""
}

var File_wso2_discovery_config_enforcer_issuer_proto protoreflect.FileDescriptor

var file_wso2_discovery_config_enforcer_issuer_proto_rawDesc = []byte{
	0x0a, 0x2b, 0x77, 0x73, 0x6f, 0x32, 0x2f, 0x64, 0x69, 0x73, 0x63, 0x6f, 0x76, 0x65, 0x72, 0x79,
	0x2f, 0x63, 0x6f, 0x6e, 0x66, 0x69, 0x67, 0x2f, 0x65, 0x6e, 0x66, 0x6f, 0x72, 0x63, 0x65, 0x72,
	0x2f, 0x69, 0x73, 0x73, 0x75, 0x65, 0x72, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x12, 0x1e, 0x77,
	0x73, 0x6f, 0x32, 0x2e, 0x64, 0x69, 0x73, 0x63, 0x6f, 0x76, 0x65, 0x72, 0x79, 0x2e, 0x63, 0x6f,
	0x6e, 0x66, 0x69, 0x67, 0x2e, 0x65, 0x6e, 0x66, 0x6f, 0x72, 0x63, 0x65, 0x72, 0x22, 0x8c, 0x02,
	0x0a, 0x06, 0x49, 0x73, 0x73, 0x75, 0x65, 0x72, 0x12, 0x12, 0x0a, 0x04, 0x6e, 0x61, 0x6d, 0x65,
	0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x04, 0x6e, 0x61, 0x6d, 0x65, 0x12, 0x16, 0x0a, 0x06,
	0x69, 0x73, 0x73, 0x75, 0x65, 0x72, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x06, 0x69, 0x73,
	0x73, 0x75, 0x65, 0x72, 0x12, 0x2a, 0x0a, 0x10, 0x63, 0x65, 0x72, 0x74, 0x69, 0x66, 0x69, 0x63,
	0x61, 0x74, 0x65, 0x41, 0x6c, 0x69, 0x61, 0x73, 0x18, 0x03, 0x20, 0x01, 0x28, 0x09, 0x52, 0x10,
	0x63, 0x65, 0x72, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x65, 0x41, 0x6c, 0x69, 0x61, 0x73,
	0x12, 0x18, 0x0a, 0x07, 0x6a, 0x77, 0x6b, 0x73, 0x55, 0x52, 0x4c, 0x18, 0x04, 0x20, 0x01, 0x28,
	0x09, 0x52, 0x07, 0x6a, 0x77, 0x6b, 0x73, 0x55, 0x52, 0x4c, 0x12, 0x32, 0x0a, 0x14, 0x76, 0x61,
	0x6c, 0x69, 0x64, 0x61, 0x74, 0x65, 0x53, 0x75, 0x62, 0x73, 0x63, 0x72, 0x69, 0x70, 0x74, 0x69,
	0x6f, 0x6e, 0x18, 0x05, 0x20, 0x01, 0x28, 0x08, 0x52, 0x14, 0x76, 0x61, 0x6c, 0x69, 0x64, 0x61,
	0x74, 0x65, 0x53, 0x75, 0x62, 0x73, 0x63, 0x72, 0x69, 0x70, 0x74, 0x69, 0x6f, 0x6e, 0x12, 0x2a,
	0x0a, 0x10, 0x63, 0x6f, 0x6e, 0x73, 0x75, 0x6d, 0x65, 0x72, 0x4b, 0x65, 0x79, 0x43, 0x6c, 0x61,
	0x69, 0x6d, 0x18, 0x06, 0x20, 0x01, 0x28, 0x09, 0x52, 0x10, 0x63, 0x6f, 0x6e, 0x73, 0x75, 0x6d,
	0x65, 0x72, 0x4b, 0x65, 0x79, 0x43, 0x6c, 0x61, 0x69, 0x6d, 0x12, 0x30, 0x0a, 0x13, 0x63, 0x65,
	0x72, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x65, 0x46, 0x69, 0x6c, 0x65, 0x50, 0x61, 0x74,
	0x68, 0x18, 0x07, 0x20, 0x01, 0x28, 0x09, 0x52, 0x13, 0x63, 0x65, 0x72, 0x74, 0x69, 0x66, 0x69,
	0x63, 0x61, 0x74, 0x65, 0x46, 0x69, 0x6c, 0x65, 0x50, 0x61, 0x74, 0x68, 0x42, 0x8b, 0x01, 0x0a,
	0x2a, 0x6f, 0x72, 0x67, 0x2e, 0x77, 0x73, 0x6f, 0x32, 0x2e, 0x67, 0x61, 0x74, 0x65, 0x77, 0x61,
	0x79, 0x2e, 0x64, 0x69, 0x73, 0x63, 0x6f, 0x76, 0x65, 0x72, 0x79, 0x2e, 0x63, 0x6f, 0x6e, 0x66,
	0x69, 0x67, 0x2e, 0x65, 0x6e, 0x66, 0x6f, 0x72, 0x63, 0x65, 0x72, 0x42, 0x0b, 0x49, 0x73, 0x73,
	0x75, 0x65, 0x72, 0x50, 0x72, 0x6f, 0x74, 0x6f, 0x50, 0x01, 0x5a, 0x4e, 0x67, 0x69, 0x74, 0x68,
	0x75, 0x62, 0x2e, 0x63, 0x6f, 0x6d, 0x2f, 0x65, 0x6e, 0x76, 0x6f, 0x79, 0x70, 0x72, 0x6f, 0x78,
	0x79, 0x2f, 0x67, 0x6f, 0x2d, 0x63, 0x6f, 0x6e, 0x74, 0x72, 0x6f, 0x6c, 0x2d, 0x70, 0x6c, 0x61,
	0x6e, 0x65, 0x2f, 0x77, 0x73, 0x6f, 0x32, 0x2f, 0x64, 0x69, 0x73, 0x63, 0x6f, 0x76, 0x65, 0x72,
	0x79, 0x2f, 0x63, 0x6f, 0x6e, 0x66, 0x69, 0x67, 0x2f, 0x65, 0x6e, 0x66, 0x6f, 0x72, 0x63, 0x65,
	0x72, 0x3b, 0x65, 0x6e, 0x66, 0x6f, 0x72, 0x63, 0x65, 0x72, 0x62, 0x06, 0x70, 0x72, 0x6f, 0x74,
	0x6f, 0x33,
}

var (
	file_wso2_discovery_config_enforcer_issuer_proto_rawDescOnce sync.Once
	file_wso2_discovery_config_enforcer_issuer_proto_rawDescData = file_wso2_discovery_config_enforcer_issuer_proto_rawDesc
)

func file_wso2_discovery_config_enforcer_issuer_proto_rawDescGZIP() []byte {
	file_wso2_discovery_config_enforcer_issuer_proto_rawDescOnce.Do(func() {
		file_wso2_discovery_config_enforcer_issuer_proto_rawDescData = protoimpl.X.CompressGZIP(file_wso2_discovery_config_enforcer_issuer_proto_rawDescData)
	})
	return file_wso2_discovery_config_enforcer_issuer_proto_rawDescData
}

var file_wso2_discovery_config_enforcer_issuer_proto_msgTypes = make([]protoimpl.MessageInfo, 1)
var file_wso2_discovery_config_enforcer_issuer_proto_goTypes = []interface{}{
	(*Issuer)(nil), // 0: wso2.discovery.config.enforcer.Issuer
}
var file_wso2_discovery_config_enforcer_issuer_proto_depIdxs = []int32{
	0, // [0:0] is the sub-list for method output_type
	0, // [0:0] is the sub-list for method input_type
	0, // [0:0] is the sub-list for extension type_name
	0, // [0:0] is the sub-list for extension extendee
	0, // [0:0] is the sub-list for field type_name
}

func init() { file_wso2_discovery_config_enforcer_issuer_proto_init() }
func file_wso2_discovery_config_enforcer_issuer_proto_init() {
	if File_wso2_discovery_config_enforcer_issuer_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_wso2_discovery_config_enforcer_issuer_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Issuer); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_wso2_discovery_config_enforcer_issuer_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   1,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_wso2_discovery_config_enforcer_issuer_proto_goTypes,
		DependencyIndexes: file_wso2_discovery_config_enforcer_issuer_proto_depIdxs,
		MessageInfos:      file_wso2_discovery_config_enforcer_issuer_proto_msgTypes,
	}.Build()
	File_wso2_discovery_config_enforcer_issuer_proto = out.File
	file_wso2_discovery_config_enforcer_issuer_proto_rawDesc = nil
	file_wso2_discovery_config_enforcer_issuer_proto_goTypes = nil
	file_wso2_discovery_config_enforcer_issuer_proto_depIdxs = nil
}
