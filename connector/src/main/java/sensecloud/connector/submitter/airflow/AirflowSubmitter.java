package sensecloud.connector.submitter.airflow;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sensecloud.connector.submitter.remote.AirflowRestInvoker;
import sensecloud.connector.submitter.remote.GitClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class AirflowSubmitter {

    @Autowired
    private GitConf gitConf;

    @Autowired
    private AirflowDAGProvider airflowDAGProvider;

    @Autowired
    private AirflowRestInvoker airflowRestInvoker;

    @Value("${service.submitter.env.k8s.context}")
    private String env_kubernetes_context;

    @Value("${service.submitter.env.k8s.namespace}")
    private String env_kubernetes_namespace;

    @Value("${service.submitter.env.k8s.oauth_token}")
    private String env_kubernetes_oauth_token;

    @Value("${service.submitter.env.k8s.api_server}")
    private String env_kubernetes_api_server;

    private GitClient gitClient;

    private void init () {
        this.gitClient = new GitClient();
        File localRepoDir = new File(this.gitConf.getLocalRepo());
        if (!localRepoDir.exists()) {
            localRepoDir.mkdirs();
        }

        if(!this.gitClient.init(this.gitConf.getUsername(),
                this.gitConf.getPassword(), this.gitConf.getLocalRepo(),
                this.gitConf.getRemoteUrl(), this.gitConf.getRemote())) {
            this.gitClient.clone(this.gitConf.getRemoteBranch());
        }
    }

    /**
     * Generate an airflow DAG and submit to airflow
     * @param jobId An id to mark this submission
     * @param name A name refer to dag template
     * @param jobConf Configurations for the job
     * @return Return true if submission success
     */
    public boolean submit(String group, String jobId, String name, JSONObject jobConf) {
        this.init();

        JSONObject context = new JSONObject();
        context.put("appName", jobId);
        context.put("kubernetes_context", env_kubernetes_context);
        context.put("kubernetes_namespace", env_kubernetes_namespace);
        context.put("kubernetes_oauth_token", env_kubernetes_oauth_token);
        context.put("env_kubernetes_api_server", env_kubernetes_api_server);
        context.put("config", jobConf);
        String code = this.airflowDAGProvider.dag(name, context);

        this.gitClient.pull(this.gitConf.getRemoteBranch());
        File codeFile = this.writeCode(group, jobId, code);
        this.gitClient.addFile(codeFile);
        this.gitClient.commit("Submit a DAG[" + jobId + "]  at " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        this.gitClient.push(this.gitConf.getRemoteBranch());

//        try {
//            Thread.currentThread().sleep(120 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        AirflowRestInvoker.CommonResponse<JSONObject> response = this.airflowRestInvoker.triggerDAG(jobId, null, null);
//        log.debug("Airflow feedback: {}, {}", response.getStatus(), response.getBody());
//
//        if(response.getStatus() == 200) {
//            return true;
//        } else {
//            return false;
//        }

        return true;
    }

    private File writeCode (String group, String runId, String code) {
        File dir = new File(this.gitConf.getLocalRepo() + "/" + this.gitConf.getProject() + "/" +group);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String codeFileName = runId + ".py";
        String codeFileLocation = dir.getAbsolutePath() + "/" + codeFileName;
        File codeFile = new File(codeFileLocation);

        try (FileWriter writer = new FileWriter(codeFile);) {
            writer.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return codeFile;
    }

}
