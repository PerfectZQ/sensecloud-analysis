package sensecloud.connector.dag;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "service.connector.dag")
public class AirflowConf {

    private String tplPath;

}
