package sensecloud.web.bean;

import lombok.Data;

@Data
public class FlowCodeBean {

    private Long id;
    private Long flowId;
    private String version;
    private String code;

}
