package sensecloud.web.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import sensecloud.auth2.config.SSOConfiguration;
import sensecloud.web.filter.TokenAuthenticationFilter;
import sensecloud.web.service.impl.SenseCloudUserDetailsServiceImpl;

/**
 * @author zhangqiang
 * @since 2020/12/14 13:57
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SenseCloudUserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    @Qualifier("tokenAuthenticationProvider")
    private AuthenticationProvider tokenAuthenticationProvider;
    @Value("${spring.security.auth.postOnly:false}")
    private Boolean postOnly;
    @Autowired
    private SSOConfiguration configuration;
    @Autowired
    private AuthenticationSuccessHandler tokenAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler tokenAuthenticationFailureHandler;

    /**
     * 自定义认证
     *
     * @return
     */
    @Bean("dbAuthenticationProvider")
    public AuthenticationProvider dbAuthenticationProvider() {
        log.info("====> dbAuthenticationProvider");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        return authenticationProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 认证登录用户
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(dbAuthenticationProvider());
        auth.authenticationProvider(tokenAuthenticationProvider);
        configureTestUsersInMemory(auth);
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
        TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter(
                postOnly, configuration, new AntPathRequestMatcher("/api/**"));
        tokenAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        tokenAuthenticationFilter.setAuthenticationSuccessHandler(tokenAuthenticationSuccessHandler);
        tokenAuthenticationFilter.setAuthenticationFailureHandler(tokenAuthenticationFailureHandler);
        http.addFilterAfter(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/swagger-ui/**", "/swagger-resources/**", "/**/api-docs/**")
                .permitAll() // Access resources as `anonymousUser`
                .antMatchers("/login/**", "/logout/**")
                .permitAll() // Specify that URLs are allowed by anyone(includes anonymous user).
                .anyRequest()
                .authenticated() // Specify that URLs are allowed by any authenticated user.
                // .and()
                // .formLogin()
                // .loginPage("/login") // Indicate login URL
                // .defaultSuccessUrl("/api") // Indicate login success URL
                // .permitAll()
                // .and()
                // .logout()
                // .logoutSuccessUrl("/") // Indicate logout success URL
                // .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf()
                .disable()
                // 前后端分离是无状态的，所以不用 Session，将登陆信息保存在 Token 中。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * 在内存中配置四个不同角色的测试用户
     *
     * @param auth
     * @throws Exception
     */
    private void configureTestUsersInMemory(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("sre.bigdata")
                .password(passwordEncoder.encode("sre.bigdata@66"))
                // .roles() 默认会添加 `ROLE_` 前缀，写死在代码里了取消不掉
                .authorities("PlatformAdmin")
                .and()
                .withUser("dlink")
                .password(passwordEncoder.encode("dlink@66"))
                .authorities("ProductAdmin");
    }

}