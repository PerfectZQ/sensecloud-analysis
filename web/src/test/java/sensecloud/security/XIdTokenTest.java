package sensecloud.security;

import com.alibaba.fastjson.JSON;
import org.springframework.util.Base64Utils;
import sensecloud.auth2.model.SSOToken;
import sensecloud.auth2.model.TokenExt;
import sensecloud.auth2.model.UserInfo;

import java.nio.charset.Charset;

/**
 * @author zhangqiang
 * @since 2020/12/16 19:26
 */
public class XIdTokenTest {

    public static void main(String[] args) {
        SSOToken ssoToken1 = new SSOToken().setExt(
                new TokenExt().setIdentity(
                        new UserInfo().setUsername("sre.bigdata")));
        String ssoTokenJson1 = JSON.toJSONString(ssoToken1);
        System.out.println(ssoTokenJson1);
        String encodeSsoToken1 = new String(Base64Utils.encode(ssoTokenJson1.getBytes(Charset.defaultCharset())));
        System.out.println("sre.bigdata: " + encodeSsoToken1);
        SSOToken ssoToken2 = new SSOToken().setExt(
                new TokenExt().setIdentity(
                        new UserInfo().setUsername("dlink")));
        String ssoTokenJson2 = JSON.toJSONString(ssoToken2);
        System.out.println(ssoTokenJson2);
        String encodeSsoToken2 = new String(Base64Utils.encode(ssoTokenJson2.getBytes(Charset.defaultCharset())));
        System.out.println("dlink: " + encodeSsoToken2);
        System.out.println(encodeSsoToken1.equals(encodeSsoToken2));
    }

}
