package sensecloud.event;

import lombok.Data;

@Data
public class Event {

    private String name;
    private String type;
    private String action;
    private String status;
    private String dagId;
}
