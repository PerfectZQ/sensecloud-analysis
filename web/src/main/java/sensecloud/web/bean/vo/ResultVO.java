package sensecloud.web.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "通用结果数据模型")
public class ResultVO<E> {
    @ApiModelProperty(name = "code", value = "响应代码", example = "200")
    private int code;
    @ApiModelProperty(name = "msg", value = "响应信息", example = "success")
    private String msg;
    @ApiModelProperty(name = "data", value = "响应数据")
    private E data;

    public ResultVO<E> setData(E data) {
        this.data = data;
        return this;
    }

    public static <E> ResultVO<E> ok(E data) {
        ResultVO<E> result = new ResultVO<E>();
        result.code = 0;
        result.msg = "success";
        result.data = data;
        return result;
    }

    public static <E> ResultVO<E> error(int code, String error) {
        ResultVO<E> result = new ResultVO<E>();
        result.code = code;
        result.msg = error;
        return result;
    }

    public static <E> ResultVO<E> error(String error) {
        return error(-1, error);
    }


}
