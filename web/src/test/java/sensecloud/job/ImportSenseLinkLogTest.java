package sensecloud.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import sensecloud.connector.rule.pebble.PebbleExpRuleEnforcer;
import sensecloud.web.App;
import sensecloud.web.bean.vo.ConnectorVO;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ImportSenseLinkLogTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void importLog() {
        ConnectorVO vo = new ConnectorVO();
        URL url = PebbleExpRuleEnforcer.class.getClassLoader().getResource("connector");
        try {
            String sourceJson = FileUtils.readFileToString(new File(url.getFile() + "/sample/senselink/source-kafka.json"), "utf-8");
            String sinkJson = FileUtils.readFileToString(new File(url.getFile() + "/sample/senselink/sink-clickhouse.json"), "utf-8");

            vo.setName("import-senselink-log");
            vo.setSourceName("senselink-kafka");
            vo.setSourceType("KAFKA");
            vo.setSourceConf(JSONObject.parseObject(sourceJson));
            vo.setSinkName("senselink-clickhouse");
            vo.setSinkType("CLICKHOUSE");
            vo.setSinkConf(JSONObject.parseObject(sinkJson));
            vo.setCreateBy("lijingyu2");
            vo.setCreateTime(LocalDateTime.now());

            RequestBuilder request = post("/connector")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(JSON.toJSONString(vo))
                    ;
            mvc.perform(request)
                    .andDo(print()) // 输出详细日志
                    .andExpect(status().is2xxSuccessful());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
