// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: wso2/discovery/config/enforcer/issuer.proto

package org.wso2.gateway.discovery.config.enforcer;

public interface IssuerOrBuilder extends
    // @@protoc_insertion_point(interface_extends:wso2.discovery.config.enforcer.Issuer)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Issuer name
   * </pre>
   *
   * <code>string name = 1;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * Issuer name
   * </pre>
   *
   * <code>string name = 1;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * Issuer identifier
   * </pre>
   *
   * <code>string issuer = 2;</code>
   * @return The issuer.
   */
  java.lang.String getIssuer();
  /**
   * <pre>
   * Issuer identifier
   * </pre>
   *
   * <code>string issuer = 2;</code>
   * @return The bytes for issuer.
   */
  com.google.protobuf.ByteString
      getIssuerBytes();

  /**
   * <pre>
   * Alias of the local certificate to be used for token validation
   * with this issuer
   * </pre>
   *
   * <code>string certificateAlias = 3;</code>
   * @return The certificateAlias.
   */
  java.lang.String getCertificateAlias();
  /**
   * <pre>
   * Alias of the local certificate to be used for token validation
   * with this issuer
   * </pre>
   *
   * <code>string certificateAlias = 3;</code>
   * @return The bytes for certificateAlias.
   */
  com.google.protobuf.ByteString
      getCertificateAliasBytes();

  /**
   * <pre>
   * JWKS endpoint of the issuer
   * </pre>
   *
   * <code>string jwksURL = 4;</code>
   * @return The jwksURL.
   */
  java.lang.String getJwksURL();
  /**
   * <pre>
   * JWKS endpoint of the issuer
   * </pre>
   *
   * <code>string jwksURL = 4;</code>
   * @return The bytes for jwksURL.
   */
  com.google.protobuf.ByteString
      getJwksURLBytes();

  /**
   * <pre>
   * Enable or disable subscription validation for this issuer
   * </pre>
   *
   * <code>bool validateSubscription = 5;</code>
   * @return The validateSubscription.
   */
  boolean getValidateSubscription();

  /**
   * <pre>
   * JWT claim that communicates the consumerKey value
   * </pre>
   *
   * <code>string consumerKeyClaim = 6;</code>
   * @return The consumerKeyClaim.
   */
  java.lang.String getConsumerKeyClaim();
  /**
   * <pre>
   * JWT claim that communicates the consumerKey value
   * </pre>
   *
   * <code>string consumerKeyClaim = 6;</code>
   * @return The bytes for consumerKeyClaim.
   */
  com.google.protobuf.ByteString
      getConsumerKeyClaimBytes();

  /**
   * <pre>
   * FilePath of the public certificate mounted in enforcer container
   * </pre>
   *
   * <code>string certificateFilePath = 7;</code>
   * @return The certificateFilePath.
   */
  java.lang.String getCertificateFilePath();
  /**
   * <pre>
   * FilePath of the public certificate mounted in enforcer container
   * </pre>
   *
   * <code>string certificateFilePath = 7;</code>
   * @return The bytes for certificateFilePath.
   */
  com.google.protobuf.ByteString
      getCertificateFilePathBytes();
}
