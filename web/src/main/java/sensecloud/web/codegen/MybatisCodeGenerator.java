package sensecloud.web.codegen;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class MybatisCodeGenerator {

    private final static String driverName = "com.mysql.jdbc.Driver";
    private final static String url = "jdbc:mysql://sh.paas.sensetime.com:34004/sensecloud?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private final static String username = "9mmyxokq";
    // 改成正确的密码，不要提交到 gitlab 否则会报安全漏洞
    private final static String passwd = "bxzqx4f5";
    private final static String moduleName = "web";
    private final static String[] includeTables = new String[]{
//            "component",
//            "role",
//            "web_componet_role_mapping",
//            "user",
//            "product",
//            "user_role",
//            "user_product",
            "product_service"
    };

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        String modulePath = String.join(System.getProperty("file.separator"),
                projectPath, moduleName);
        String outputDir = String.join(System.getProperty("file.separator"),
                modulePath, "src", "main", "java");

        gc.setOutputDir(outputDir);
        log.info("Set outputDir: " + outputDir);
        gc.setAuthor("ZhangQiang");
        gc.setOpen(false);
        gc.setSwagger2(true); // 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDriverName(driverName);
        dsc.setUrl(url);
        // dsc.setSchemaName("dbName");
        dsc.setUsername(username);
        dsc.setPassword(passwd);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        String packageInfo = "sensecloud.web";
        pc.setParent(packageInfo);
        // pc.setModuleName(moduleName);
        mpg.setPackageInfo(pc);
        log.info("Set package info: " + packageInfo);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        /**
         *  freemarker: /templates/mapper.xml.ftl
         *  velocity: /templates/mapper.xml.vm
         */

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义 Mapper XML 配置会被优先输出
        String templatePath = "/templates/mapper.xml.ftl";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                String targetPath = String.join(System.getProperty("file.separator"), modulePath, "src", "main", "resources",
                        "mapper", tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML);
                log.info("Generate XML to " + targetPath);
                return targetPath;
            }
        });
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录");
                return false;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        // 指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // 不生成 Controller
        templateConfig.setController(null);
        // 使用上面自定义的 XML 配置
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // 公共父类
        // strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        // strategy.setSuperEntityColumns("id");
        strategy.setInclude(includeTables);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix("");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }


}
