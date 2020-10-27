package sensecloud.sso.model;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class User {

    private String id;
    private String name;
    private String familyName;
    private String givenName;
    private boolean status;
    private String phoneNumber;
    private String email;
    private String identityType;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
