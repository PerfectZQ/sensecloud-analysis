package sensecloud.web.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;

/**
 * 开启 Security 的注解: @EnableGlobalMethodSecurity(prePostEnabled = true)，这样我们就可以在需要
 * 控制权限的方法上面使用 @PreAuthorize，@PreFilter 这些注解。
 *
 * @author zhangqiang
 * @see <a href="https://stackoverflow.com/questions/38134121/how-do-i-remove-the-role-prefix-from-spring-security-with-javaconfig/46817507#46817507">How do I remove the ROLE_ prefix from Spring Security with JavaConfig?</a>
 * @since 2020/12/14 15:07
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class CustomGlobalMethodSecurity extends GlobalMethodSecurityConfiguration {

    protected AccessDecisionManager accessDecisionManager() {
        AffirmativeBased accessDecisionManager = (AffirmativeBased) super.accessDecisionManager();
        // Remove the `ROLE_` prefix from RoleVoter for @Secured and hasRole checks on methods
        accessDecisionManager.getDecisionVoters().stream()
                .filter(RoleVoter.class::isInstance)
                .map(RoleVoter.class::cast)
                .forEach(it -> it.setRolePrefix(""));
        return accessDecisionManager;
    }

    /**
     * Allows providing defaults for {@link GrantedAuthority}
     *
     * @return 授权的默认配置
     */
    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the `ROLE_` prefix for @PreAuthorize
        return new GrantedAuthorityDefaults("");
    }
}
