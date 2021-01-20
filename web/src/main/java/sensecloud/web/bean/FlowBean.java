package sensecloud.web.bean;

import lombok.Data;
import sensecloud.flow.Flow;

@Data
public class FlowBean extends Flow {

    private String saas;

    private FlowCodeBean code;

}
