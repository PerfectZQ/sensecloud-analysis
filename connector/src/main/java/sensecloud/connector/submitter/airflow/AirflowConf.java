package sensecloud.connector.submitter.airflow;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "submitter.airflow.conf")
public class AirflowConf {

    private String templatePath;

}
