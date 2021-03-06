package sensecloud.web.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import sensecloud.auth2.config.SSOConfiguration;
import sensecloud.web.filter.TokenAuthenticationFilter;
import sensecloud.web.service.impl.SenseCloudUserDetailsServiceImpl;

/**
 * ??????????????????: @EnableWebSecurity
 * <p>
 * ?????????????????????????????????: @EnableGlobalMethodSecurity
 * 1. prePostEnabled: ?????? Spring Security ???????????? [@PreAuthorize, @PostAuthorize, ..] ????????????
 * 2. secureEnabled:  ?????? Spring Security ???????????? [@Secured] ????????????
 * 3. jsr250Enabled:  ?????? JSR-250?????? [@RolesAllowed..] ????????????
 *
 * @author zhangqiang
 * @since 2020/12/14 13:57
 */
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
     * ???????????????
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

    /**
     * ?????? {@link AuthenticationManager}???????????? {@link AbstractAuthenticationProcessingFilter#getAuthenticationManager()}
     * ?????????????????????
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * ??????????????????
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
                // ????????????????????????????????????????????? Session??????????????????????????? Token ??????
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param auth
     * @throws Exception
     */
    private void configureTestUsersInMemory(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("sre.bigdata")
                .password(passwordEncoder.encode("sre.bigdata@66"))
                // .roles() ??????????????? `ROLE_` ??????????????????????????????????????????
                .authorities("PlatformAdmin")
                .and()
                .withUser("dlink")
                .password(passwordEncoder.encode("dlink@66"))
                .authorities("ProductAdmin");
    }

}