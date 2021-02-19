package sensecloud.submitter.remote;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "service.submitter.env")
public class KubernetesClientConf {

    private String kubernetes_context;
    private String kubernetes_namespace;
    private String kubernetes_project;
    private String kubernetes_oauth_token;
    private String kubernetes_api_server;

}
