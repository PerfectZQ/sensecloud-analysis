package sensecloud.controller;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import sensecloud.web.App;
import sensecloud.web.bean.InitProduct;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author zhangqiang
 * @since 2020/11/5 16:17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {App.class})
@AutoConfigureMockMvc
@Slf4j
public class AuthorizeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void initGroupTest() throws Exception {
        RequestBuilder request;
        String content = new GsonBuilder().create().toJson(new InitProduct()
                .setProductName("dlink_test")
                .setUsername("zhangqiang")
                .setRepository("https://gitlab.bj.sensetime.com/plat-bigdata/sensecloud-analysis")
                .setBranch("master")
        );
        log.info("====> initGroupTest content: " + content);
        request = post("/authorize/initGroup")
                // .header("", "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andDo(print()) // 输出详细日志
                .andExpect(status().isOk());
    }

    @Test
    public void boundUserRoleToGroupTest() throws Exception {
        RequestBuilder request;
        request = post("/authorize/boundUserRoleToGroup")
                // .header("", "")
                .param("username", "zhangqiang")
                .param("rolename", "数据开发人员")
                .param("groupName", "dlink_test")
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andDo(print()) // 输出详细日志
                .andExpect(status().isOk());
    }

    @Test
    public void unboundUserRoleFromGroupTest() throws Exception {
        RequestBuilder request;
        request = post("/authorize/unboundUserRoleFromGroup")
                // .header("", "")
                .param("username", "zhangqiang")
                .param("rolename", "数据开发人员")
                .param("groupName", "dlink_test")
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andDo(print()) // 输出详细日志
                .andExpect(status().isOk());
    }

}
