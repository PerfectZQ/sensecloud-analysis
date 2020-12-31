package sensecloud.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sensecloud.web.bean.vo.ConnectorVO;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.constant.AttachmentCatalog;
import sensecloud.web.entity.ConnectorAttachmentEntity;
import sensecloud.web.entity.ConnectorEntity;
import sensecloud.web.service.impl.ConnectorAttachmentServiceImpl;
import sensecloud.web.service.impl.ConnectorServiceImpl;
import sensecloud.web.service.impl.SenseCloudUserDetailsServiceImpl;
import sensecloud.web.service.impl.UserAuthorityServiceImpl;
import sensecloud.web.service.remote.ClickHouseRemoteService;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static sensecloud.web.bean.vo.ResultVO.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/connector")
public class ConnectorController {

    @Value("${service.connector.upload.path}")
    private String uploadPath;

    @Value("${service.connector.clickhouse.url}")
    private String clickHouseJDBCUrl;

    @Autowired
    private ConnectorServiceImpl connectorService;

    @Autowired
    private ConnectorAttachmentServiceImpl connectorAttachmentService;

    @Autowired
    private UserAuthorityServiceImpl userAuthorityService;

    @Autowired
    private SenseCloudUserDetailsServiceImpl senseCloudUserDetailsService;

    @PostMapping
    public ResultVO<Boolean> add(@RequestBody ConnectorVO params) {
        //Todo: params validation
        //  ...

        Authentication user = this.currentUser();
        if (user == null || StringUtils.isBlank(user.getName())) {
            return error("Not a login user.");
        }

        ConnectorEntity entity = new ConnectorEntity();
        BeanUtils.copyProperties(params, entity);

        entity.setCreateBy(user.getName());
        try {
            boolean success = connectorService.saveOrUpdate(entity);

            if ("KAFKA".equalsIgnoreCase(params.getSourceType())) {
                JSONObject sourceAccountConf = params.getSourceAccountConf();
                Boolean securityEnable = sourceAccountConf.getBoolean("security.enable");

                if (securityEnable != null && securityEnable.booleanValue()) {
                    String keystoreLocation = sourceAccountConf.getString("ssl.keystore.location");
                    String truststoreLocation = sourceAccountConf.getString("ssl.truststore.location");
                    List<ConnectorAttachmentEntity> entities = new ArrayList<>();

                    if (StringUtils.isNotBlank(keystoreLocation)) {
                        ConnectorAttachmentEntity attachmentEntity = new ConnectorAttachmentEntity();
                        String catalog = AttachmentCatalog.KAFKA_KEYSTORE.name();
                        attachmentEntity.setCatalog(catalog);

                        String realLocation = this.uploadPath + "/" + keystoreLocation;
                        File attachment = new File(realLocation);
                        if(attachment.exists()) {
                            attachmentEntity.setContent(this.readAttachment(realLocation));
                        }

                        attachmentEntity.setConnectorId(entity.getId());

                        entities.add(attachmentEntity);
                    }

                    if (StringUtils.isNotBlank(truststoreLocation)) {
                        ConnectorAttachmentEntity attachmentEntity = new ConnectorAttachmentEntity();
                        String catalog = AttachmentCatalog.KAFKA_TRUSTSTORE.name();
                        attachmentEntity.setCatalog(catalog);

                        String realLocation = this.uploadPath + "/" + truststoreLocation;
                        File attachment = new File(realLocation);
                        if(attachment.exists()) {
                            attachmentEntity.setContent(this.readAttachment(realLocation));
                        }

                        attachmentEntity.setConnectorId(entity.getId());
                        entities.add(attachmentEntity);
                    }

                    success = connectorAttachmentService.saveBatch(entities);
                }

                if (success) {
                    this.assembleClickHouseConf(user.getName(), entity);
                    success = connectorService.submitKafkaJob(entity);
                }
            } else if ("MYSQL_CDC".equalsIgnoreCase(params.getSourceType())) {
                success = connectorService.addMysqlCDC(entity);
            }
            return ok(success);
        } catch(Exception e) {
            e.printStackTrace();
            return error(2020, "Exception occurred");
        }

    }

    @PutMapping
    public ResultVO<Boolean> update(@RequestBody ConnectorVO params) {
        //Todo: params validation
        //  ...

        Authentication user = this.currentUser();
        if (user == null || StringUtils.isBlank(user.getName())) {
            return error("Not a login user.");
        }

        ConnectorEntity entity = new ConnectorEntity();
        BeanUtils.copyProperties(params, entity);

        entity.setUpdateBy(user.getName());
        boolean updateResult = connectorService.updateById(entity);
        if ("KAFKA".equalsIgnoreCase(params.getSourceType())) {
            JSONObject sourceAccountConf = params.getSourceAccountConf();
            Boolean securityEnable = sourceAccountConf.getBoolean("security.enable");

            if (securityEnable != null && securityEnable.booleanValue()) {
                String keystoreLocation = sourceAccountConf.getString("ssl.keystore.location");
                String truststoreLocation = sourceAccountConf.getString("ssl.truststore.location");
                List<ConnectorAttachmentEntity> entities = new ArrayList<>();

                if (StringUtils.isNotBlank(keystoreLocation)) {
                    ConnectorAttachmentEntity attachmentEntity = new ConnectorAttachmentEntity();
                    String catalog = AttachmentCatalog.KAFKA_KEYSTORE.name();
                    attachmentEntity.setCatalog(catalog);
                    attachmentEntity.setConnectorId(entity.getId());

                    String realLocation = this.uploadPath + "/" + keystoreLocation;
                    File attachment = new File(realLocation);
                    if(attachment.exists()) {
                        attachmentEntity.setContent(this.readAttachment(realLocation));
                        entities.add(attachmentEntity);
                    }
                }

                if (StringUtils.isNotBlank(truststoreLocation)) {
                    ConnectorAttachmentEntity attachmentEntity = new ConnectorAttachmentEntity();
                    String catalog = AttachmentCatalog.KAFKA_TRUSTSTORE.name();
                    attachmentEntity.setConnectorId(entity.getId());
                    attachmentEntity.setCatalog(catalog);
                    String realLocation = this.uploadPath + "/" + truststoreLocation;
                    File attachment = new File(realLocation);
                    if(attachment.exists()) {
                        attachmentEntity.setContent(this.readAttachment(realLocation));
                        entities.add(attachmentEntity);
                    }
                }
                
                if(!entities.isEmpty()) {
                    updateResult = connectorAttachmentService.updateBatchById(entities, entities.size());
                }
            }

            if(updateResult) {
                this.assembleClickHouseConf(user.getName(), entity);
                updateResult = connectorService.submitKafkaJob(entity);
            }
        } else if ("MYSQL_CDC".equalsIgnoreCase(params.getSourceType())) {
            updateResult = connectorService.updateMysqlCDC(entity);
        }
        return ok(updateResult);
    }

    @DeleteMapping
    public ResultVO<Boolean> delete(@RequestParam Long id) {
        //Todo: params validation
        //  ...
        Authentication user = this.currentUser();
        if (user == null || StringUtils.isBlank(user.getName())) {
            return error("Not a login user.");
        }

        ConnectorEntity entity = connectorService.getById(id);
        entity.setDeleted(true);
        entity.setDeleteBy(user.getName());
        entity.setDeleteTime(LocalDateTime.now());
        boolean deleteResult = connectorService.updateById(entity);

        if(deleteResult && "KAFKA".equalsIgnoreCase(entity.getSourceType())) {
            JSONObject sourceAccountConf = entity.getSourceAccountConf();
            Boolean securityEnable = sourceAccountConf.getBoolean("security.enable");

            if (securityEnable != null && securityEnable.booleanValue()) {
                deleteResult = connectorAttachmentService.deleteAll(entity.getId());
            }

        } else if ("MYSQL_CDC".equalsIgnoreCase(entity.getSourceType())) {
            deleteResult = connectorService.deleteMysqlCDC(entity);
        }
        return ok(deleteResult);
    }

    @GetMapping
    public ResultVO<ConnectorEntity> get(@RequestParam Long id) {
        ConnectorEntity entity = connectorService.getById(id);
        return ok(entity);
    }

    @GetMapping("/query")
    public ResultVO<IPage<ConnectorEntity>> query (
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long size
    ) {
        Authentication user = this.currentUser();
        if (user != null && StringUtils.isNotBlank(user.getName())) {
            String username = user.getName();
            QueryChainWrapper<ConnectorEntity> query = connectorService.query()
                    .eq("create_by", username)
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
            query.orderByDesc("create_time");
            long total = query.count();
            IPage<ConnectorEntity> result = query.page(new Page<ConnectorEntity>(pageNum, pageSize, total));
            return ok(result);
        } else {
            log.warn("Accept a request but current user is null or username is empty.");
            return error("Not a login user.");
        }
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


    private byte[] readAttachment(String location) {
        log.debug("Attachment location: {}", location);
        byte[] res = null;
        try (FileInputStream in = new FileInputStream(new File(location))) {
            res = IOUtils.toByteArray(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private Authentication currentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    private void assembleClickHouseConf(String username, ConnectorEntity entity) {
        JSONObject chConf = connectorService.getClickHouseUser(username);
        log.debug(">>> connectorService.getClickHouseUser callback object: {}", chConf);
        entity.getSinkAccountConf().put("jdbc.url", clickHouseJDBCUrl);
        entity.getSinkAccountConf().put("jdbc.user", chConf.getString("ckUser"));
        entity.getSinkAccountConf().put("jdbc.password", chConf.getString("ckPassword"));
    }


}
