package sensecloud.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

/**
 * @author zhangqiang
 * @since 2020/12/14 13:57
 */
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        // Configure Spring EL Handler
        webSecurity.expressionHandler(new DefaultWebSecurityExpressionHandler() {
            @Override
            protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
                WebSecurityExpressionRoot root = (WebSecurityExpressionRoot) super.createSecurityExpressionRoot(authentication, fi);
                root.setDefaultRolePrefix(""); // Remove the prefix `ROLE_`
                return root;
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // http.addFilterBefore(verifyCodeFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests() // 开启认证配置
                .antMatchers("/").permitAll()
                .antMatchers("/amchart/**",
                        "/bootstrap/**",
                        "/build/**",
                        "/css/**",
                        "/dist/**",
                        "/documentation/**",
                        "/fonts/**",
                        "/js/**",
                        "/pages/**",
                        "/plugins/**"
                ) // 白名单路径
                .permitAll()
                .anyRequest().authenticated()
                .and()
                /*
                .formLogin()
                .loginPage("/login") // 指定登录页面 URL 路径
                .defaultSuccessUrl("/httpapi") // 指定登录成功跳转 URL 路径
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/") // 退出成功后的默认跳转 URL 页面
                .permitAll()
                .and()
                 */
                .httpBasic()
                .and()
                .csrf().disable();

    }

    /**
     * 认证登录用户
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        configureTestUsersInMemory(auth);
    }

    /**
     * 在内存中配置四个不同角色的测试用户
     *
     * @param auth
     * @throws Exception
     */
    private void configureTestUsersInMemory(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                // 平台管理员
                .withUser("platform_admin")
                .password(passwordEncoder.encode("platform_admin"))
                .roles("PlatformAdmin")
                .and()
                // 产品线管理员
                .withUser("product_admin")
                .password(passwordEncoder.encode("product_admin"))
                .roles("ProductAdmin")
                .and()
                // 数据开发人员
                .withUser("data_developer")
                .password(passwordEncoder.encode("data_developer"))
                .roles("DataDeveloper")
                .and()
                // 数据分析师
                .withUser("data_analyst")
                .password(passwordEncoder.encode("data_analyst"))
                .roles("DataAnalyst");
    }

}