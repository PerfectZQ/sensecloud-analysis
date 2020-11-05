package sensecloud.auth2.config.db;

import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"sensecloud.sso"})
public class MybatisConfig {

    /**
     * 分页插件
     *
     * @return
     */
    @Bean
    @Profile(value = {"dev", "test", "prod"})
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * PostgreSQL Id 生成器
     *
     * @return
     */
    @Bean
    public IKeyGenerator keyGenerator() {
        return new PostgreKeyGenerator();
    }

    /*
    @Bean(name = "mysqlDataSource")
    @Primary // 对 DataSource 类型的对象进行注入时如果有多个实例，优先选择该实例
    @ConfigurationProperties(prefix = "spring.datasource.mysql-test")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build.bat();
    }

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory
            (@Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager
            (@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate
            (@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    */

}
