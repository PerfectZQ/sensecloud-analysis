package sensecloud.web.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sensecloud.web.bean.airflow.GitlabRepoVO;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.vo.DagFileVO;
import sensecloud.web.bean.vo.FlowRunVO;
import sensecloud.web.bean.vo.ResultVO;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "airflowRemoteService", url = "${remote.rest.airflow_service.url}")
public interface AirflowRemoteService {

    @PostMapping("/gitlabRepo/saveOrUpdateGitlabRepo")
    ResultVO<String> saveOrUpdateGitlabRepo(@RequestBody GitlabRepoVO gitlabRepoVO);

    @PostMapping(value = "/api/v1/airflow/dags/createOrUpdateDagFile", consumes = "application/json")
    ResultVO<String> createOrUpdateDagFile(@RequestBody DagFileVO dagFileVO);

    @DeleteMapping("/api/v1/airflow/dags/deleteDagFile")
    ResultVO<String> deleteDagFile(@RequestParam("dagFileName") String dagFileName, @RequestParam("groupName") String groupName);

    @PostMapping("/api/v1/airflow/dags/dagPause")
    ResultVO<String> dagPause(@RequestParam("dagId") String dagId, @RequestParam("paused") boolean paused);

    @PostMapping("/api/v1/airflow/dags/dagTrigger")
    ResultVO<String> dagTrigger(@RequestParam("dagId") String dagId);

    @PostMapping("/api/v1/airflow/dags/listDagRunsByUser")
    PageResult listDagRunsByUser(@RequestParam("username") String username, @RequestParam(value = "pageId", required = false) int pageId, @RequestParam(value = "pageSize", required = false) int pageSize);

    @GetMapping("/api/v1/airflow/dags/listDagRunsByUserAndDagId")
    PageResult listDagRuns(@RequestParam("username") String username, @RequestParam("dagId") String dagId, @RequestParam(value = "pageId", required = false) int pageId, @RequestParam(value = "pageSize", required = false) int pageSize);

}
