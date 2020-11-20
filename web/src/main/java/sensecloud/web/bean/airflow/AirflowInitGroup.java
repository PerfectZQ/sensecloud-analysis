package sensecloud.web.bean.airflow;

import lombok.Data;
import lombok.experimental.Accessors;
import sensecloud.web.bean.AbRole;

/**
 * @author zhangqiang
 * @since 2020/11/20 15:29
 */
@Data
@Accessors(chain = true)
public class AirflowInitGroup {
    GitlabRepo gitlabRepo;
    AbRole abRole;
}
