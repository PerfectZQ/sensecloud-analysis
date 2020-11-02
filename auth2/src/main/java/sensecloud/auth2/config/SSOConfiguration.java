package sensecloud.auth2.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

//@ConfigurationProperties(prefix = "service.sso")
public class SSOConfiguration {

    private String gatewayUrl;
    private String scope;
    private String clientId;
    private String redirectUri;
    private String clientSecret;
    private String code;
    private String grantType;

}
