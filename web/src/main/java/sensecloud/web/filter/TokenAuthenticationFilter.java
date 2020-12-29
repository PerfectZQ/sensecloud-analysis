package sensecloud.web.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Base64Utils;
import sensecloud.auth2.config.SSOConfiguration;
import sensecloud.auth2.model.SSOToken;
import sensecloud.auth2.model.UserInfo;
import sensecloud.web.security.SenseCloudAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 自定义登录认证过滤器
 *
 * @author zhangqiang
 * @since 2020/12/16 11:47
 */
@Slf4j
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private Boolean postOnly;
    private SSOConfiguration configuration;

    public TokenAuthenticationFilter(Boolean postOnly, SSOConfiguration configuration, RequestMatcher requestMatcher) {
        super(requestMatcher);
        this.postOnly = postOnly;
        this.configuration = configuration;
    }

    /**
     * 解析 UserInfo
     *
     * @param xIdToken
     * @return
     */
    private UserInfo getUserInfo(String xIdToken) {
        String value = new String(Base64Utils.decodeFromString(xIdToken));
        SSOToken token = JSON.parseObject(value, SSOToken.class);
        return token.getExt().getIdentity();
    }

    /**
     * 验证 Token
     *
     * @param xIdToken
     * @return
     */
    private Boolean checkToken(String xIdToken) {
        if (StringUtils.isEmpty(xIdToken)) {
            log.error("====> Authentication failed, header {} is blank.", configuration.getId_token_header());
            throw new AuthenticationServiceException("Authentication failed, header " + configuration.getId_token_header() + " is blank.");
            // return false;
        }
        return true;
    }

    /**
     * 提取所需请求参数数据，生成一个未认证的 {@link AbstractAuthenticationToken} 传递给
     * {@link AuthenticationManager} 认证，其中 {@link ProviderManager} 会依次尝试调用
     * {@link AuthenticationProvider#authenticate(Authentication)} 去认证。 当认证成功，
     * 执行 {@link AbstractAuthenticationProcessingFilter#successfulAuthentication(HttpServletRequest,
     * HttpServletResponse, FilterChain, Authentication)}
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("====> Attempt to authentication, SSO configuration: {}", this.configuration);
        if (postOnly && !request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            log.info(">>> HEADER name = {}, value = {}", name, request.getHeader(name));
        }

        
        String xIdToken = request.getHeader(configuration.getId_token_header());
        if (!checkToken(xIdToken)) return null;
        UserInfo userInfo = getUserInfo(xIdToken);
        log.info("====> RequestURL: {}, UserInfo: \n{}", request.getRequestURL(), JSON.toJSONString(userInfo, SerializerFeature.PrettyFormat));
        SenseCloudAuthenticationToken authenticationToken =
                new SenseCloudAuthenticationToken(userInfo.getUsername());
        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
        log.info("====> Attempt to authentication, authenticationToken.getName(): {}", authenticationToken.getName());
        return getAuthenticationManager().authenticate(authenticationToken);
    }


    /**
     * Invoke {@link AuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest, HttpServletResponse,
     * FilterChain, Authentication)} instead of {@link AuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest,
     * HttpServletResponse, Authentication)}
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: "
                    + authResult);
        }
        log.info("====> successfulAuthentication user: {}", authResult.getName());
        SecurityContextHolder.getContext().setAuthentication(authResult);
        getRememberMeServices().loginSuccess(request, response, authResult);
        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }
        getSuccessHandler().onAuthenticationSuccess(request, response, chain, authResult);
    }

}
