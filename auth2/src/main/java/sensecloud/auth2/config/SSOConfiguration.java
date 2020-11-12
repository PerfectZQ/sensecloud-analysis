package sensecloud.auth2.config;


import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Accessors(fluent = true)
@ConfigurationProperties(prefix = "service.sso")
public class SSOConfiguration {

    private String domain;
    private String id_token_header;
    private String access_token_header;
    private String no_auth_redirect_url;
    private String logout_uri;
    private String logout_redirect_url;

}
