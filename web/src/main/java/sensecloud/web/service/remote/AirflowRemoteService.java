package sensecloud.web.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sensecloud.web.bean.airflow.GitlabRepoVO;
import sensecloud.web.bean.vo.ResultVO;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "airflowRemoteService", url = "airflow-scheduler-sidecar:8088")
public interface AirflowRemoteService {

    @PostMapping("/gitlabRepo/saveOrUpdateGitlabRepo")
    ResultVO<String> saveOrUpdateGitlabRepo(@RequestBody GitlabRepoVO gitlabRepoVO);

}
