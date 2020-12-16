package sensecloud.web.config.security;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
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

    /**
     * 自定义认证
     *
     * @return
     */
    @Bean("dbAuthenticationProvider")
    public AuthenticationProvider dbAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        return authenticationProvider;
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
        auth.authenticationProvider(dbAuthenticationProvider());
        auth.authenticationProvider(tokenAuthenticationProvider);
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
        tokenAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        // tokenAuthenticationFilter.setAuthenticationSuccessHandler();
        // tokenAuthenticationFilter.setAuthenticationFailureHandler();
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
                .and()
                .logout()
                .logoutSuccessUrl("/") // Indicate logout success URL
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf()
                .disable();
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