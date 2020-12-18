package sensecloud.auth2.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class TokenExt {

    private List<Role> roles;
    private List<Permission> permissions;
    private UserInfo identity;
    private Map<String, Object> rs;

}
