package sensecloud.auth2.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;


@Data
@Accessors(chain = true)
public class UserInfo {

    private String username;
    private String domain;
    private String id;
    private String name;
    private String familyName;
    private String givenName;
    private String status;
    private String phoneNumber;
    private String email;
    private String identityType;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public String getUsername() {
        if(StringUtils.isBlank(username) && StringUtils.isNotBlank(email)) {
            username = email.split("@")[0];
        }
        return username;
    }

}
