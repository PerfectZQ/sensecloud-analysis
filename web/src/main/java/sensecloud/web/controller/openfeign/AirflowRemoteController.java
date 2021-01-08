package sensecloud.web.controller.openfeign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sensecloud.web.bean.airflow.GitlabRepoVO;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.service.remote.AirflowRemoteService;

/**
 * @author zhangqiang
 * @since 2021/1/8 14:18
 */
@RestController
@RequestMapping("/api/v1/airflow/gitlabRepo")
public class AirflowRemoteController {

    @Autowired
    private AirflowRemoteService airflowRemoteService;

    @PostMapping("saveOrUpdateGitlabRepo")
    public ResultVO<String> saveOrUpdateGitlabRepo(@RequestBody GitlabRepoVO gitlabRepoVO) {
        return airflowRemoteService.saveOrUpdateGitlabRepo(gitlabRepoVO);
    }

}
