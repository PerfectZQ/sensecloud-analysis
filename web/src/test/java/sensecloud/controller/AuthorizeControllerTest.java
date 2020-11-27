package sensecloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sensecloud.web.App;

/**
 * @author zhangqiang
 * @since 2020/11/5 16:17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {App.class})
@AutoConfigureMockMvc
@Slf4j
public class AuthorizeControllerTest {

    @Test
    public void boundUserRoleToGroupTest() {

    }

}
