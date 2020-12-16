package sensecloud.web.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import sensecloud.auth2.config.SSOConfiguration;
import sensecloud.auth2.model.SSOToken;
import sensecloud.auth2.model.UserInfo;
import sensecloud.web.security.SenseCloudAuthenticationToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义登录认证过滤器
 *
 * @author zhangqiang
 * @since 2020/12/16 11:47
 */
@Slf4j
@Component
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${spring.security.auth.postOnly:false}")
    private Boolean postOnly;
    @Autowired
    private SSOConfiguration configuration;
    @Autowired
    private HttpServletResponse response;


    /**
     * Creates a new instance
     *
     * @param requiresAuthenticationRequestMatcher the {@link RequestMatcher} used to
     *                                             determine if authentication is required. Cannot be null.
     */
    protected TokenAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(new AntPathRequestMatcher("/**"));
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
    private void checkToken(String xIdToken) {
        if (StringUtils.isEmpty(xIdToken)) {
            throw new AuthenticationServiceException("Authentication failed, header " + configuration.getId_token_header() + " is blank.");
        }
    }

    /**
     * 提取所需请求参数数据，生成一个未认证的 {@link AbstractAuthenticationToken} 传递给
     * {@link AuthenticationManager} 认证，其中 {@link ProviderManager} 会依次尝试调用
     * {@link AuthenticationProvider#authenticate(Authentication)} 去认证
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
        log.info("====> SSOConfiguration: {}", this.configuration);
        if (postOnly && !request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String xIdToken = request.getHeader(configuration.getId_token_header());
        checkToken(xIdToken);
        UserInfo userInfo = getUserInfo(xIdToken);
        // Todo 拿产品线管理员信息
        SenseCloudAuthenticationToken authenticationToken =
                new SenseCloudAuthenticationToken(userInfo.getUsername());
        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
        log.info("====> attemptAuthentication, authenticationToken.getName(): {}", authenticationToken.getName());
        return getAuthenticationManager().authenticate(authenticationToken);
    }
}
