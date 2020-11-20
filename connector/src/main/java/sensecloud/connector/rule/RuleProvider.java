package sensecloud.connector.rule;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleProvider {

    private static ConcurrentHashMap<String, String> rules = new ConcurrentHashMap<>();

    @Value("${service.connector.rule.path}")
    private String rulePath;

    public void loadFromClassPath() {
        if(StringUtils.isNotBlank(this.rulePath)) {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources(this.rulePath + "/*.rule");
                for (Resource resource : resources) {
                    String filename = resource.getFilename();
                    String baseName = FilenameUtils.getBaseName(filename);
                    byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                    String content = new String(bytes, "utf-8");

                    rules.put(baseName, content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRuleExpression(String name) {
        return rules.get(name);
    }

}
