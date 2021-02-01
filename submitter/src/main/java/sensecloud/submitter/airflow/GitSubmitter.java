package sensecloud.submitter.airflow;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sensecloud.submitter.remote.GitClient;
import sensecloud.submitter.remote.AirflowRestInvoker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class GitSubmitter {

//    @Autowired
    private GitConf gitConf;

    @Autowired
    private AirflowDAGGenerator dagGenerator;

    @Autowired
    private AirflowRestInvoker airflowRestInvoker;

    private GitClient gitClient;

    protected void init (GitConf gitConf) {
        this.gitConf = gitConf;
        this.gitClient = new GitClient();
        File localRepoDir = new File(gitConf.getLocalRepo());
        if (!localRepoDir.exists()) {
            localRepoDir.mkdirs();
        }

        if(!this.gitClient.init(gitConf.getUsername(),
                gitConf.getPassword(), gitConf.getLocalRepo(),
                gitConf.getRemoteUrl(), gitConf.getRemote())) {
            this.gitClient.clone(gitConf.getRemoteBranch());
        }
    }

    /**
     * Generate an airflow DAG and submit to airflow
     * @param jobId An id to mark this submission
     * @param name A name refer to dag template
     * @param jobConf Configurations for the job
     * @return Return true if submission success
     */
    public boolean submit(String group, String jobId, String name, JSONObject jobConf, Map<String, String> env) {
        String code = dagGenerator.generateDAG(jobId, name, jobConf, env);
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

    protected File writeCode (String group, String runId, String code) {
        File dir = new File(this.gitConf.getLocalRepo() + "/" + this.gitConf.getProject());
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
