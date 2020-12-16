package sensecloud.web.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 把当前登录的用户放到系统 Session。在 Spring Security 中，用户信息保存在 {@link SecurityContextHolder}。
 * Spring Security 使用一个 {@link Authentication} 对象来持有所有系统的安全认证相关的信息。这个信息的内容
 * 格式如下:
 * {
 * *   "accountNonExpired":true,
 * *   "accountNonLocked":true,
 * *   "authorities":[{
 * *     "authority":"ROLE_ADMIN"
 * *   },{
 * *     "authority":"ROLE_USER"
 * *   }],
 * *   "credentialsNonExpired":true,
 * *   "enabled":true,
 * *   "username":"root"
 * }
 * 这个 {@link Authentication} 对象信息其实就是 {@link User} 实体的信息(除了密码)
 * <p>
 * 指定过滤器执行顺序: @Order 值越小，越先执行
 * <p>
 * 使用 @WebFilter 需要在 SpringBoot 的入口处加注解 @ServletComponentScan, 如果不指定，默认 url-pattern
 * 是 `/*`, 所有请求都会执行过滤
 *
 * @author zhangqiang
 * @since 2020/12/14 15:44
 */
@Order(2)
@WebFilter(filterName = "principalFilter", urlPatterns = {"/*"})
@Slf4j
public class PrincipalFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("====> Principal: " + JSON.toJSONString(principal, SerializerFeature.PrettyFormat));
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        session.setAttribute("username", username);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
