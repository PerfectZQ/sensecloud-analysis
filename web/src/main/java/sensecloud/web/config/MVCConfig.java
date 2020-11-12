package sensecloud.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sensecloud.auth2.interceptor.SSOInterceptor;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Bean
    public SSOInterceptor ssoInterceptor(){
        return new SSOInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ssoInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login");
    }

}
