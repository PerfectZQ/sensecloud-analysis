package sensecloud.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sensecloud.auth2.interceptor.SSOInterceptor;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SSOInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login");
    }

}
