package sensecloud.connector.submitter.airflow;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import sensecloud.connector.utils.TextRenderer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AirflowDAGProvider {

    @Autowired
    private AirflowConf airflowConf;

    private TextRenderer renderer = new TextRenderer();

    private static ConcurrentHashMap<String, String> templates = new ConcurrentHashMap<>();

    public void reloadTemplates() {
        String templatePath = this.airflowConf.getTemplatePath();

        if(StringUtils.isNotBlank(templatePath)) {
            templates.clear();

            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources(templatePath + "/*.tpl");
                for(Resource resource : resources) {
                    String filename = resource.getFilename();
                    String baseName = FilenameUtils.getBaseName(filename);
                    byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                    String content = new String(bytes, "utf-8");

                    templates.put(baseName, content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTemplate(String name) {
        return this.templates.get(name);
    }

    public String dag (String name, JSONObject context) {
        String tpl = this.getTemplate(name);
        String dag = "";
        if (StringUtils.isNotBlank(tpl)) {
            dag = this.renderer.renderStringTemplate(tpl, context);
        }
        return dag;
    }

}
