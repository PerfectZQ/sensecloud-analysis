package sensecloud.connector.submitter.airflow;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Autowired
    private GitConf gitConf;

    @Autowired
    private AirflowDAGProvider airflowDAGProvider;

    @Autowired
    private AirflowRestInvoker airflowRestInvoker;

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
    public boolean submit(String jobId, String name, JSONObject jobConf) {
        this.init();
        String code = this.airflowDAGProvider.dag(name, jobConf);
        this.gitClient.pull(this.gitConf.getRemoteBranch());
        File codeFile = this.writeCode(jobId, code);
        this.gitClient.addFile(codeFile);
        this.gitClient.commit("Submit a DAG[" + jobId + "]  at " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        this.gitClient.push(this.gitConf.getRemoteBranch());

        AirflowRestInvoker.CommonResponse<JSONObject> response = this.airflowRestInvoker.triggerDAG(jobId, null, null);
        log.debug("Airflow feedback: {}, {}", response.getStatus(), response.getBody());

        return false;
    }

    private File writeCode (String runId, String code) {
        File dir = new File(this.gitConf.getLocalRepo());
        if(!dir.exists()){
            dir.mkdirs();
        }
        String codeFileName = runId + ".py";
        String codeFileLocation = this.gitConf.getLocalRepo() + "/" + this.gitConf.getProject() + "/" + codeFileName;
        File codeFile = new File(codeFileLocation);

        try (FileWriter writer = new FileWriter(codeFile);) {
            writer.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return codeFile;
    }

}
