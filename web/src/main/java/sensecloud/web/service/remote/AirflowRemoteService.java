package sensecloud.web.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sensecloud.web.bean.airflow.GitlabRepoVO;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.vo.DagFileVO;
import sensecloud.web.bean.vo.FlowRunVO;
import sensecloud.web.bean.vo.ResultVO;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "airflowRemoteService", url = "airflow-scheduler-sidecar:8088")
public interface AirflowRemoteService {

    @PostMapping("/gitlabRepo/saveOrUpdateGitlabRepo")
    ResultVO<String> saveOrUpdateGitlabRepo(@RequestBody GitlabRepoVO gitlabRepoVO);

    @PostMapping("/api/v1/airflow/dags/createOrUpdateDagFile")
    ResultVO<String> createOrUpdateDagFile(@RequestBody DagFileVO dagFileVO);

    @PostMapping("/api/v1/airflow/dags/deleteDagFile")
    ResultVO<String> deleteDagFile(@RequestBody DagFileVO dagFileVO);

    @PostMapping("/api/v1/airflow/dags/dagPause")
    ResultVO<String> dagPause(@RequestParam("dagId") String dagId, @RequestParam("paused") boolean paused);

    @PostMapping("/api/v1/airflow/dags/listDagRuns/{pageId}/{pageSize}")
    PageResult<FlowRunVO> listDagRuns(@RequestBody FlowRunVO dagRun, @PathVariable("pageId") int pageId, @PathVariable("pageSize") int pageSize);
}
