package sensecloud.service;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sensecloud.web.App;
import sensecloud.web.entity.RoleComponentVO;
import sensecloud.web.entity.WebComponentRoleMappingVO;
import sensecloud.web.service.impl.RoleServiceImpl;
import sensecloud.web.service.impl.WebComponentRoleMappingServiceImpl;

import java.util.List;

/**
 * @author zhangqiang
 * @since 2020/11/27 17:02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {App.class})
@AutoConfigureMockMvc
@Slf4j
public class WebComponentRoleMappingServiceTest {

    @Autowired
    private WebComponentRoleMappingServiceImpl webComponentRoleMappingService;

    @Autowired
    private RoleServiceImpl roleService;

    @Test
    public void getWebComponentRoleMappingVOByWebRoleIdTest() {
        RoleComponentVO roleComponentVO = roleService.getProductManager();
        List<WebComponentRoleMappingVO> webComponentRoleMappingVOList = webComponentRoleMappingService
                .getBaseMapper().getWebComponentRoleMappingVOByWebRoleId(roleComponentVO.getRoleId());
        log.info(new GsonBuilder().create().toJson(webComponentRoleMappingVOList));
    }

}
