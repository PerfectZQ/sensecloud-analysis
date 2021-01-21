package sensecloud.web.bean.vo;

import lombok.Data;
import sensecloud.web.bean.FlowRunBean;

import java.time.LocalDateTime;

@Data
public class FlowRunVO extends FlowRunBean {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
