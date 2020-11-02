package sensecloud.auth2.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TokenExt {

    private List<Role> roles;
    private List<Permission> permissions;
    private User identity;
    private Map<String, Object> rs;

}
