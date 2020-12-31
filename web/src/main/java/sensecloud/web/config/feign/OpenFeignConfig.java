package sensecloud.web.config.feign;

import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;

/**
 * @author zhangqiang
 * @since 2020/12/31 16:32
 */
public class OpenFeignConfig {
    @Bean
    public Decoder feignDecoder() {
        return new FeignResultDecoder();
    }
}
