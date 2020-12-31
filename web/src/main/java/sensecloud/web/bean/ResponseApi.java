package sensecloud.web.bean;

import lombok.Data;

/**
 * @author zhangqiang
 * @since 2020/12/31 16:35
 */
@Data
public class ResponseApi<T> {

    private Integer code;

    private String msg;

    private T data;
}