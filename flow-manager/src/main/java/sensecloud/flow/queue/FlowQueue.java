package sensecloud.flow.queue;

import lombok.Data;
import sensecloud.flow.Flow;

@Data
public class FlowQueue {

    private String name;
    private String type;
    private Flow flow;
    private String action;
    private String status;
}
