package sensecloud.web.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import sensecloud.web.handler.TokenAuthenticationSuccessHandler;
import sensecloud.web.service.impl.SenseCloudUserDetailsServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangqiang
 * @since 2020/12/16 13:32
 */
@Slf4j
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SenseCloudUserDetailsServiceImpl userDetailsService;

    
    /**
     * 认证 attemptAuthentication，当返回不为 null，调用 {@link TokenAuthenticationSuccessHandler#onAuthenticationSuccess }
     *
     * @param attemptAuthentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication attemptAuthentication) throws AuthenticationException {
        if (!supports(attemptAuthentication.getClass())) {
            return null;
        }
        Object credentials = attemptAuthentication.getCredentials();
        String username = attemptAuthentication.getPrincipal().toString();
        log.info("====> Authenticating username: {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            log.error("====> Unable to obtain user information of {} from db", username);
            throw new InternalAuthenticationServiceException("Unable to obtain user information");
        }
        SenseCloudAuthenticationToken authentication = new SenseCloudAuthenticationToken(
                userDetails, userDetails.getAuthorities());
        authentication.setDetails(attemptAuthentication.getDetails());
        return authentication;
    }


    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the
     * indicated <Code>Authentication</code> object.
     * <p>
     * Returning <code>true</code> does not guarantee an
     * <code>AuthenticationProvider</code> will be able to authenticate the presented
     * instance of the <code>Authentication</code> class. It simply indicates it can
     * support closer evaluation of it. An <code>AuthenticationProvider</code> can still
     * return <code>null</code> from the {@link #authenticate(Authentication)} method to
     * indicate another <code>AuthenticationProvider</code> should be tried.
     * </p>
     * <p>
     * Selection of an <code>AuthenticationProvider</code> capable of performing
     * authentication is conducted at runtime the <code>ProviderManager</code>.
     * </p>
     *
     * @param authentication
     * @return <code>true</code> if the implementation can more closely evaluate the
     * <code>Authentication</code> class presented
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return SenseCloudAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
