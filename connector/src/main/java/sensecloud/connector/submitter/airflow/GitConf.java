package sensecloud.connector.submitter.airflow;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "submitter.airflow.git")
public class GitConf {

    private String localRepo;
    private String project;
    private String remoteUrl;
    private String username;
    private String password;
    private String remoteBranch;
    private String remote;


}
