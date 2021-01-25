package sensecloud.web.bean;

import lombok.Data;
import sensecloud.flow.queue.FlowQueue;

@Data
public class FlowQueueBean extends FlowQueue {

    private FlowBean flow;

}
