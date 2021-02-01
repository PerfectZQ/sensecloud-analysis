package sensecloud.web;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 开启 ServletComponentScan: @ServletComponentScan 那些被标注了 @WebFilter，@WebServlet，@WebListener 的
 * Bean 类将会注册到容器中。需要注意的一点是，这个扫描动作只在当我们使用的是嵌入式 Servlet 容器的时候才起作用。
 * 完成 Bean 注册工作的类是 {@link org.springframework.boot.web.servlet.ServletComponentScanRegistrar}，
 * 它实现了 Spring 的 {@link ImportBeanDefinitionRegistrar} 接口。
 */
@SpringBootApplication(
        scanBasePackages = {"sensecloud"},
        exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class}
)
@ServletComponentScan
@ConfigurationPropertiesScan("sensecloud")
@EnableAutoConfiguration
@EnableFeignClients
@EnableAsync
@EnableScheduling
@MapperScan({"sensecloud"})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
