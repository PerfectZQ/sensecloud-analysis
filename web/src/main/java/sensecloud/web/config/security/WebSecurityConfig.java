package sensecloud.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import sensecloud.web.service.impl.SenseCloudUserDetailsServiceImpl;

/**
 * @author zhangqiang
 * @since 2020/12/14 13:57
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SenseCloudUserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider dbAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        return authenticationProvider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Configure Spring EL Handler
        web.expressionHandler(new DefaultWebSecurityExpressionHandler() {
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
        http
                .requestMatchers() // 直接访问的请求
                .antMatchers("/oauth/**", "/login/**", "/logout/**")
            .and()
                .authorizeRequests() // 授权的请求
                .antMatchers("/",
                        "/connector",
                        "/connector/**",
                        "/authorize/**",
                        "/product/**",
                        "/userRole/**"
                )
                .permitAll() // 放行，白名单路径
                .anyRequest()
                .authenticated() // 任何其他的请求都需要认证
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
                .csrf()
                .disable();

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
        // auth.authenticationProvider(dbAuthenticationProvider());
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
                .authorities("PlatformAdmin")
                .and()
                // 产品线管理员
                .withUser("product_admin")
                .password(passwordEncoder.encode("product_admin"))
                // roles() 默认会添加 `ROLE_` 前缀
                .authorities("ProductAdmin")
                .and()
                // 数据开发人员
                .withUser("data_developer")
                .password(passwordEncoder.encode("data_developer"))
                .authorities("DataDeveloper")
                .and()
                // 数据分析师
                .withUser("data_analyst")
                .password(passwordEncoder.encode("data_analyst"))
                .authorities("DataAnalyst");
    }

}