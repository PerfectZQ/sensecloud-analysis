package sensecloud.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sensecloud.web.bean.vo.ConnectorVO;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.entity.ConnectorEntity;
import sensecloud.web.service.impl.ConnectorServiceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static sensecloud.web.bean.vo.ResultVO.*;

@Slf4j
@RestController
@RequestMapping("/connector")
public class ConnectorController {

    @Value("${service.connector.upload.path}")
    private String uploadPath;

    @Autowired
    private ConnectorServiceImpl connectorService;

    @PostMapping
    public ResultVO<Boolean> add(@RequestBody ConnectorVO params) {
        //Todo: params validation
        //  ...
        ConnectorEntity entity = new ConnectorEntity();
        BeanUtils.copyProperties(params, entity);
        boolean success = connectorService.saveOrUpdate(entity);
        if(success) {
            success = connectorService.submit(entity);
        }
        return ok(success);
    }

    @PutMapping
    public ResultVO<Boolean> update(@RequestBody ConnectorVO params) {
        //Todo: params validation
        //  ...
        ConnectorEntity entity = new ConnectorEntity();
        BeanUtils.copyProperties(params, entity);
        boolean updateResult = connectorService.updateById(entity);
        return ok(updateResult);
    }

    @DeleteMapping
    public ResultVO<Boolean> delete(@RequestParam Long id) {
        //Todo: params validation
        //  ...
        ConnectorEntity entity = new ConnectorEntity();
        entity.setId(String.valueOf(id));
        entity.setDeleted(true);
        boolean deleteResult = connectorService.updateById(entity);
        return ok(deleteResult);
    }

    @GetMapping
    public ResultVO<ConnectorEntity> get(@RequestParam Long id) {
        ConnectorEntity entity = connectorService.getById(id);
        return ok(entity);
    }

    @GetMapping("/query")
    public ResultVO<IPage<ConnectorEntity>> query(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long size
    ) {
//        User user = UserContextProvider.getContext().getCurrentUser();
//        if (user != null && StringUtils.isNotBlank(user.getUsername())) {
//            String username = user.getUsername();
//            String username = '';
            QueryChainWrapper<ConnectorEntity> query = connectorService.query()
//                    .eq("create_by", username)
                    .and(q -> q.eq("deleted", false));
            if(StringUtils.isNotBlank(name)) {
                query.and(q -> q.eq("name", name));
            }

            long pageNum = 1;
            long pageSize = 10;
            if(page != null) {
                pageNum = page.longValue();
            }

            if(size != null) {
                pageSize = size.longValue();
            }
            long total = query.count();
            IPage<ConnectorEntity> result = query.page(new Page<ConnectorEntity>(pageNum, pageSize, total));
            return ok(result);
//        } else {
//            log.warn("Accept a request but current user is null or username is empty.");
//            return error("Not a login user.");
//        }
    }


    @ApiOperation(value = "Upload a kafka keystore file, and return a ref path", notes = "上传kafka的keystore")
    @PostMapping("/upload/kafka_keystore")
    @ResponseBody
    public ResultVO<String> kafkaSSLKeyStoreUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return error("file is missing.");
        }
        String fileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String location = "kafka_keystore/" + uuid + "/" + fileName;
        File dir = new File(this.uploadPath + "/kafka_keystore/" + uuid);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir.getAbsolutePath() + "/" + fileName);
        try (InputStream in = file.getInputStream();
             OutputStream out = new FileOutputStream(dest);){
            IOUtils.copy(in, out);
            return ok(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error("Exception occurred.");
    }

    @ApiOperation(value = "Upload a kafka truststore file, and return a ref path", notes = "上传kafka的truststore")
    @PostMapping("/upload/kafka_truststore")
    @ResponseBody
    public ResultVO<String> kafkaSSLTrustStoreUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return error("file is missing.");
        }
        String fileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String location = "kafka_truststore/" + uuid + "/" + fileName;
        File dir = new File(this.uploadPath + "/kafka_truststore/" + uuid);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir.getAbsolutePath() + "/" + fileName);
        try (InputStream in = file.getInputStream();
             OutputStream out = new FileOutputStream(dest);){
            IOUtils.copy(in, out);
            return ok(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error("Exception occurred.");
    }
}
