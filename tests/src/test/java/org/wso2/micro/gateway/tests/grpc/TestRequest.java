// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: test.proto

package org.wso2.micro.gateway.tests.grpc;

/**
 * Protobuf type {@code TestRequest}
 */
public  final class TestRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:TestRequest)
    TestRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use TestRequest.newBuilder() to construct.
  private TestRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private TestRequest() {
    testReqString_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new TestRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private TestRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            testReqString_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.wso2.micro.gateway.tests.grpc.Test.internal_static_TestRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.wso2.micro.gateway.tests.grpc.Test.internal_static_TestRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.wso2.micro.gateway.tests.grpc.TestRequest.class, org.wso2.micro.gateway.tests.grpc.TestRequest.Builder.class);
  }

  public static final int TESTREQSTRING_FIELD_NUMBER = 1;
  private volatile java.lang.Object testReqString_;
  /**
   * <code>string testReqString = 1;</code>
   * @return The testReqString.
   */
  public java.lang.String getTestReqString() {
    java.lang.Object ref = testReqString_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      testReqString_ = s;
      return s;
    }
  }
  /**
   * <code>string testReqString = 1;</code>
   * @return The bytes for testReqString.
   */
  public com.google.protobuf.ByteString
      getTestReqStringBytes() {
    java.lang.Object ref = testReqString_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      testReqString_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getTestReqStringBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, testReqString_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getTestReqStringBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, testReqString_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.wso2.micro.gateway.tests.grpc.TestRequest)) {
      return super.equals(obj);
    }
    org.wso2.micro.gateway.tests.grpc.TestRequest other = (org.wso2.micro.gateway.tests.grpc.TestRequest) obj;

    if (!getTestReqString()
        .equals(other.getTestReqString())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + TESTREQSTRING_FIELD_NUMBER;
    hash = (53 * hash) + getTestReqString().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.wso2.micro.gateway.tests.grpc.TestRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.wso2.micro.gateway.tests.grpc.TestRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code TestRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:TestRequest)
      org.wso2.micro.gateway.tests.grpc.TestRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.wso2.micro.gateway.tests.grpc.Test.internal_static_TestRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.wso2.micro.gateway.tests.grpc.Test.internal_static_TestRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.wso2.micro.gateway.tests.grpc.TestRequest.class, org.wso2.micro.gateway.tests.grpc.TestRequest.Builder.class);
    }

    // Construct using org.wso2.micro.gateway.tests.grpc.TestRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      testReqString_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.wso2.micro.gateway.tests.grpc.Test.internal_static_TestRequest_descriptor;
    }

    @java.lang.Override
    public org.wso2.micro.gateway.tests.grpc.TestRequest getDefaultInstanceForType() {
      return org.wso2.micro.gateway.tests.grpc.TestRequest.getDefaultInstance();
    }

    @java.lang.Override
    public org.wso2.micro.gateway.tests.grpc.TestRequest build() {
      org.wso2.micro.gateway.tests.grpc.TestRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.wso2.micro.gateway.tests.grpc.TestRequest buildPartial() {
      org.wso2.micro.gateway.tests.grpc.TestRequest result = new org.wso2.micro.gateway.tests.grpc.TestRequest(this);
      result.testReqString_ = testReqString_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.wso2.micro.gateway.tests.grpc.TestRequest) {
        return mergeFrom((org.wso2.micro.gateway.tests.grpc.TestRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.wso2.micro.gateway.tests.grpc.TestRequest other) {
      if (other == org.wso2.micro.gateway.tests.grpc.TestRequest.getDefaultInstance()) return this;
      if (!other.getTestReqString().isEmpty()) {
        testReqString_ = other.testReqString_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.wso2.micro.gateway.tests.grpc.TestRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.wso2.micro.gateway.tests.grpc.TestRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object testReqString_ = "";
    /**
     * <code>string testReqString = 1;</code>
     * @return The testReqString.
     */
    public java.lang.String getTestReqString() {
      java.lang.Object ref = testReqString_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        testReqString_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string testReqString = 1;</code>
     * @return The bytes for testReqString.
     */
    public com.google.protobuf.ByteString
        getTestReqStringBytes() {
      java.lang.Object ref = testReqString_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        testReqString_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string testReqString = 1;</code>
     * @param value The testReqString to set.
     * @return This builder for chaining.
     */
    public Builder setTestReqString(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      testReqString_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string testReqString = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearTestReqString() {
      
      testReqString_ = getDefaultInstance().getTestReqString();
      onChanged();
      return this;
    }
    /**
     * <code>string testReqString = 1;</code>
     * @param value The bytes for testReqString to set.
     * @return This builder for chaining.
     */
    public Builder setTestReqStringBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      testReqString_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:TestRequest)
  }

  // @@protoc_insertion_point(class_scope:TestRequest)
  private static final org.wso2.micro.gateway.tests.grpc.TestRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.wso2.micro.gateway.tests.grpc.TestRequest();
  }

  public static org.wso2.micro.gateway.tests.grpc.TestRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<TestRequest>
      PARSER = new com.google.protobuf.AbstractParser<TestRequest>() {
    @java.lang.Override
    public TestRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new TestRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<TestRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<TestRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.wso2.micro.gateway.tests.grpc.TestRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

