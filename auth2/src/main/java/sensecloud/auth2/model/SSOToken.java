package sensecloud.auth2.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SSOToken {
    private long iat;
    private String iss;
    private List<String> aud;
    private String nonce;
    private String sid;
    private String atHash;
    private LocalDateTime authTime;
    private TokenExt ext;
    private LocalDateTime exp;
    private String jti;
    private String sub;
    private String rat;

}
