package sensecloud.web.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

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
public class GlobalMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    /**
     * 配置 AccessDecisionManager, Spring 提供了 3 个决策管理器：
     * <p>
     * 1. AffirmativeBased：一票通过，只要有一个投票器通过就允许访问
     * 2. ConsensusBased：有一半以上投票器通过才允许访问资源
     * 3. UnanimousBased：所有投票器都通过才允许访问
     *
     * @return
     */
    protected AccessDecisionManager accessDecisionManager() {
        AffirmativeBased accessDecisionManager = (AffirmativeBased) super.accessDecisionManager();
        // Remove the `ROLE_` prefix from RoleVoter for @Secured and hasRole checks on methods
        accessDecisionManager.getDecisionVoters().stream()
                .filter(RoleVoter.class::isInstance)
                .map(RoleVoter.class::cast)
                .forEach(it -> it.setRolePrefix(""));
        return accessDecisionManager;
    }

}
