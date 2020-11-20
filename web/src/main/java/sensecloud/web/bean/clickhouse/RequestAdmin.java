package sensecloud.web.bean.clickhouse;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhangqiang
 * @since 2020/11/19 15:16
 */
@Data
@Accessors(chain = true)
public class RequestAdmin {
    String userName;
}
