package sensecloud.auth2.model;

import lombok.Data;

@Data
public class Permission {

    private String domain;
    private String resource;
    private String action;
}
