package sensecloud.web.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author zhangqiang
 * @since 2020/12/31 17:03
 */
public class JacksonUtil {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static <T> T json2obj(String jsonStr, Type targetType) {
        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(targetType);
            return mapper.readValue(jsonStr, javaType);
        } catch (IOException e) {
            throw new IllegalArgumentException("将 JSON 转换为对象时发生错误:" + jsonStr, e);
        }
    }

}
