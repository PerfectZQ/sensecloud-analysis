package sensecloud.web.bean.clickhouse;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhangqiang
 * @since 2020/11/19 14:55
 */
@Data
@Accessors(chain = true)
public class RequestProduct {
    String userName;
    String productLine;
}
