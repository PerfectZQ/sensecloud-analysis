package sensecloud.web.config.feign;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import sensecloud.web.bean.ResponseApi;
import sensecloud.web.utils.JacksonUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author zhangqiang
 * @since 2020/12/31 16:32
 */
public class FeignResultDecoder implements Decoder {
    /**
     * Decodes an http response into an object corresponding to its
     * {@link Method#getGenericReturnType() generic return type}. If you need to
     * wrap exceptions, please do so via {@link DecodeException}.
     *
     * @param response the response to decode
     * @param type     {@link Method#getGenericReturnType() generic return type} of the
     *                 method corresponding to this {@code response}.
     * @return instance of {@code type}
     * @throws IOException     will be propagated safely to the caller.
     * @throws DecodeException when decoding failed due to a checked exception besides IOException.
     * @throws FeignException  when decoding succeeds, but conveys the operation failed.
     */
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response.body() == null) {
            throw new DecodeException(response.status(), "没有返回有效的数据");
        }
        String bodyStr = Util.toString(response.body().asReader(Util.UTF_8));
        // 对结果进行转换
        ResponseApi result = (ResponseApi) JacksonUtil.json2obj(bodyStr, type);
        // 如果返回错误，且为内部错误，则直接抛出异常
        if (result.getCode() != HttpServletResponse.SC_OK) {

        }
        return result.getData();
    }


}
