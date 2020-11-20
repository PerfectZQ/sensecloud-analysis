package sensecloud.connector.submitter.airflow;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "submitter.airflow.k8s")
public class KubernetesConf {

    private String namespace;

}
