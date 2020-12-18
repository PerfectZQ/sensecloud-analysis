package sensecloud.web.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * 自定义一个认证登录 Token
 *
 * @author zhangqiang
 * @since 2020/12/16 13:16
 */
@Slf4j
public class SenseCloudAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    public SenseCloudAuthenticationToken(String username) {
        super(null);
        this.principal = username;
        // 在过滤器时，会生成一个未认证的 AuthenticationToken，此时调用的是自定义 token 的 setAuthenticated(false) -> 未认证
        this.setAuthenticated(false);
        log.info("====> Init an unAuthenticated Token, setAuthenticated(false)");
    }

    public SenseCloudAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        // 在提供者时，会生成一个已认证的 AuthenticationToken，此时调用的是父类的setAuthenticated(true) -> 已认证
        this.setAuthenticated(true);
        log.info("====> Init an authenticated Token, setAuthenticated(true)");
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

}
