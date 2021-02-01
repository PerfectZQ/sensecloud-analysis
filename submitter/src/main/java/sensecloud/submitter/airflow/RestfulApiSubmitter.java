package sensecloud.submitter.airflow;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sensecloud.event.EventAction;
import sensecloud.event.EventType;
import sensecloud.event.db.EventService;
import sensecloud.submitter.remote.bean.DagFileVO;
import sensecloud.submitter.remote.bean.ResultVO;
import sensecloud.submitter.remote.feign.AirflowSidecarService;

import java.util.Map;

@Slf4j
@Component
public class RestfulApiSubmitter {

    @Autowired
    private AirflowDAGGenerator dagGenerator;

    @Autowired
    private AirflowSidecarService airflowSidecarService;

    @Autowired
    private EventService eventService;

    public boolean submitConnectorJob(
            String group,
            String submitter,
            String appName,
            String appType,
            JSONObject appConf,
            Map<String, String> env) {
        String dagCode = this.dagGenerator.generateDAG(appName, appType, appConf, env);
        DagFileVO dag = new DagFileVO();
        dag.setFileName(appName + ".py");
        dag.setGroupName(group);
        dag.setSourceCode(dagCode);
        ResultVO<String> createResult = airflowSidecarService.createOrUpdateDagFile(dag);
        log.debug("Request airflow sidecar and return code = {}, message = {}", createResult.getCode(), createResult.getMsg());
        if (createResult.getCode() == 200) {
            eventService.raiseEvent(submitter,
                    "Create_Connector",
                    EventType.CONNECTOR,
                    EventAction.CREATE_DAG,
                    appName);
            return true;
        }
        return false;
    }

    public boolean renewConnectorJob(String group,
                                     String submitter,
                                     String appName,
                                     String appType,
                                     JSONObject appConf,
                                     Map<String, String> env) {
        String dagCode = this.dagGenerator.generateDAG(appName, appType, appConf, env);
        DagFileVO dag = new DagFileVO();
        dag.setFileName(appName + ".py");
        dag.setGroupName(group);
        dag.setSourceCode(dagCode);
        ResultVO<String> createResult = airflowSidecarService.createOrUpdateDagFile(dag);
        log.debug("Request airflow sidecar and return code = {}, message = {}", createResult.getCode(), createResult.getMsg());
        if (createResult.getCode() == 200) {
            eventService.raiseEvent(submitter,
                    "Update_Connector",
                    EventType.CONNECTOR,
                    EventAction.UPDATE_DAG,
                    appName);
            return true;
        }
        return false;
    }

    public boolean removeConnectorJob(String group,String submitter,String appName) {
        String fileName = appName + ".py";
        ResultVO<String> deleteResult = airflowSidecarService.deleteDagFile(fileName, group);
        log.debug("Request airflow sidecar and return code = {}, message = {}", deleteResult.getCode(), deleteResult.getMsg());
        if (deleteResult.getCode() == 200) {
            eventService.raiseEvent(submitter,
                    "Delete_Connector",
                    EventType.CONNECTOR,
                    EventAction.DELETE_DAG,
                    appName);
            return true;
        }
        return false;
    }

    public boolean submitFlowJob (
            String group,
            String submitter,
            String appName,
            String appCode) {
        DagFileVO dag = new DagFileVO();
        dag.setFileName(appName + ".py");
        dag.setGroupName(group);
        dag.setSourceCode(appCode);
        ResultVO<String> createResult = airflowSidecarService.createOrUpdateDagFile(dag);
        log.debug("Request airflow sidecar for dag {} and return code = {}, message = {}", appName, createResult.getCode(), createResult.getMsg());
        if (createResult.getCode() == 200) {
            eventService.raiseEvent(submitter,
                    "Create_Flow",
                    EventType.FLOW_MANAGER,
                    EventAction.CREATE_DAG,
                    appName);
            return true;
        }
        return false;
    }

}
