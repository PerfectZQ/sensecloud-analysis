package sensecloud.flow.generator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DAGDefinitionProvider {

    private static ConcurrentHashMap<String, String> tpls = new ConcurrentHashMap<>();

    @Value("${service.flow.tpl.path}")
    private String tplPath;

    public void loadFromClassPath () {
        if(StringUtils.isNotBlank(this.tplPath)) {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources(this.tplPath + "/*.tpl");
                for (Resource resource : resources) {
                    String filename = resource.getFilename();
                    String baseName = FilenameUtils.getBaseName(filename);
                    byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                    String content = new String(bytes, "utf-8");

                    tpls.put(baseName, content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getTemplate(String name) {
        return tpls.get(name);
    }
}
