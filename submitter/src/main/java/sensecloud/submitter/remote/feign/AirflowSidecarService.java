package sensecloud.submitter.remote.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sensecloud.submitter.remote.bean.DagFileVO;
import sensecloud.submitter.remote.bean.ResultVO;

@FeignClient(name = "airflowSidecarService", url = "${remote.rest.airflow_service.url}")
public interface AirflowSidecarService {
    @PostMapping(value = "/api/v1/airflow/dags/createOrUpdateDagFile", consumes = "application/json")
    ResultVO<String> createOrUpdateDagFile(@RequestBody DagFileVO dagFileVO);

    @DeleteMapping("/api/v1/airflow/dags/deleteDagFile")
    ResultVO<String> deleteDagFile(@RequestParam("dagFileName") String dagFileName, @RequestParam("groupName") String groupName);

    @PostMapping("/api/v1/airflow/dags/dagPause")
    ResultVO<String> dagPause(@RequestParam("dagId") String dagId, @RequestParam("paused") boolean paused);

    @PostMapping("/api/v1/airflow/dags/dagTrigger")
    ResultVO<String> dagTrigger(@RequestParam("dagId") String dagId);

}
