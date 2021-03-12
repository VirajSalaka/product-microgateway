// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: wso2/discovery/config/enforcer/config.proto

package org.wso2.gateway.discovery.config.enforcer;

public interface ConfigOrBuilder extends
    // @@protoc_insertion_point(interface_extends:wso2.discovery.config.enforcer.Config)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .wso2.discovery.config.enforcer.Issuer jwtTokenConfig = 1;</code>
   */
  java.util.List<org.wso2.gateway.discovery.config.enforcer.Issuer> 
      getJwtTokenConfigList();
  /**
   * <code>repeated .wso2.discovery.config.enforcer.Issuer jwtTokenConfig = 1;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.Issuer getJwtTokenConfig(int index);
  /**
   * <code>repeated .wso2.discovery.config.enforcer.Issuer jwtTokenConfig = 1;</code>
   */
  int getJwtTokenConfigCount();
  /**
   * <code>repeated .wso2.discovery.config.enforcer.Issuer jwtTokenConfig = 1;</code>
   */
  java.util.List<? extends org.wso2.gateway.discovery.config.enforcer.IssuerOrBuilder> 
      getJwtTokenConfigOrBuilderList();
  /**
   * <code>repeated .wso2.discovery.config.enforcer.Issuer jwtTokenConfig = 1;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.IssuerOrBuilder getJwtTokenConfigOrBuilder(
      int index);

  /**
   * <code>.wso2.discovery.config.enforcer.CertStore keystore = 2;</code>
   * @return Whether the keystore field is set.
   */
  boolean hasKeystore();
  /**
   * <code>.wso2.discovery.config.enforcer.CertStore keystore = 2;</code>
   * @return The keystore.
   */
  org.wso2.gateway.discovery.config.enforcer.CertStore getKeystore();
  /**
   * <code>.wso2.discovery.config.enforcer.CertStore keystore = 2;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.CertStoreOrBuilder getKeystoreOrBuilder();

  /**
   * <code>.wso2.discovery.config.enforcer.CertStore truststore = 3;</code>
   * @return Whether the truststore field is set.
   */
  boolean hasTruststore();
  /**
   * <code>.wso2.discovery.config.enforcer.CertStore truststore = 3;</code>
   * @return The truststore.
   */
  org.wso2.gateway.discovery.config.enforcer.CertStore getTruststore();
  /**
   * <code>.wso2.discovery.config.enforcer.CertStore truststore = 3;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.CertStoreOrBuilder getTruststoreOrBuilder();

  /**
   * <code>.wso2.discovery.config.enforcer.EventHub eventhub = 4;</code>
   * @return Whether the eventhub field is set.
   */
  boolean hasEventhub();
  /**
   * <code>.wso2.discovery.config.enforcer.EventHub eventhub = 4;</code>
   * @return The eventhub.
   */
  org.wso2.gateway.discovery.config.enforcer.EventHub getEventhub();
  /**
   * <code>.wso2.discovery.config.enforcer.EventHub eventhub = 4;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.EventHubOrBuilder getEventhubOrBuilder();

  /**
   * <code>.wso2.discovery.config.enforcer.Service authService = 5;</code>
   * @return Whether the authService field is set.
   */
  boolean hasAuthService();
  /**
   * <code>.wso2.discovery.config.enforcer.Service authService = 5;</code>
   * @return The authService.
   */
  org.wso2.gateway.discovery.config.enforcer.Service getAuthService();
  /**
   * <code>.wso2.discovery.config.enforcer.Service authService = 5;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.ServiceOrBuilder getAuthServiceOrBuilder();

  /**
   * <code>.wso2.discovery.config.enforcer.AmCredentials apimCredentials = 6;</code>
   * @return Whether the apimCredentials field is set.
   */
  boolean hasApimCredentials();
  /**
   * <code>.wso2.discovery.config.enforcer.AmCredentials apimCredentials = 6;</code>
   * @return The apimCredentials.
   */
  org.wso2.gateway.discovery.config.enforcer.AmCredentials getApimCredentials();
  /**
   * <code>.wso2.discovery.config.enforcer.AmCredentials apimCredentials = 6;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.AmCredentialsOrBuilder getApimCredentialsOrBuilder();

  /**
   * <code>.wso2.discovery.config.enforcer.JWTGenerator jwtGenerator = 7;</code>
   * @return Whether the jwtGenerator field is set.
   */
  boolean hasJwtGenerator();
  /**
   * <code>.wso2.discovery.config.enforcer.JWTGenerator jwtGenerator = 7;</code>
   * @return The jwtGenerator.
   */
  org.wso2.gateway.discovery.config.enforcer.JWTGenerator getJwtGenerator();
  /**
   * <code>.wso2.discovery.config.enforcer.JWTGenerator jwtGenerator = 7;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.JWTGeneratorOrBuilder getJwtGeneratorOrBuilder();

  /**
   * <code>.wso2.discovery.config.enforcer.Throttling throttlingConfig = 8;</code>
   * @return Whether the throttlingConfig field is set.
   */
  boolean hasThrottlingConfig();
  /**
   * <code>.wso2.discovery.config.enforcer.Throttling throttlingConfig = 8;</code>
   * @return The throttlingConfig.
   */
  org.wso2.gateway.discovery.config.enforcer.Throttling getThrottlingConfig();
  /**
   * <code>.wso2.discovery.config.enforcer.Throttling throttlingConfig = 8;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.ThrottlingOrBuilder getThrottlingConfigOrBuilder();

  /**
   * <code>.wso2.discovery.config.enforcer.Cache cache = 9;</code>
   * @return Whether the cache field is set.
   */
  boolean hasCache();
  /**
   * <code>.wso2.discovery.config.enforcer.Cache cache = 9;</code>
   * @return The cache.
   */
  org.wso2.gateway.discovery.config.enforcer.Cache getCache();
  /**
   * <code>.wso2.discovery.config.enforcer.Cache cache = 9;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.CacheOrBuilder getCacheOrBuilder();

  /**
   * <code>.wso2.discovery.config.enforcer.Analytics analytics = 10;</code>
   * @return Whether the analytics field is set.
   */
  boolean hasAnalytics();
  /**
   * <code>.wso2.discovery.config.enforcer.Analytics analytics = 10;</code>
   * @return The analytics.
   */
  org.wso2.gateway.discovery.config.enforcer.Analytics getAnalytics();
  /**
   * <code>.wso2.discovery.config.enforcer.Analytics analytics = 10;</code>
   */
  org.wso2.gateway.discovery.config.enforcer.AnalyticsOrBuilder getAnalyticsOrBuilder();
}
