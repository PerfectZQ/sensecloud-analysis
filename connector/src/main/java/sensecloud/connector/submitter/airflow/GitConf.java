package sensecloud.connector.submitter.airflow;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component @ConfigurationProperties(prefix = "submitter.airflow.git")
public class GitConf {

    private String localRepo;
    private String project;
    private String remoteUrl;
    private String username;
    private String password;
    private String remoteBranch;
    private String remote;


}
