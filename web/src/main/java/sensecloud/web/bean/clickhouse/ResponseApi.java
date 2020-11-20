package sensecloud.web.bean.clickhouse;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhangqiang
 * @since 2020/11/19 14:54
 */
@Data
public class ResponseApi<T> {

    private Integer code;

    private String msg;

    private T data;
}