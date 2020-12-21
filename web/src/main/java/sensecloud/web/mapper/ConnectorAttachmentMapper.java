package sensecloud.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import sensecloud.web.entity.ConnectorAttachmentEntity;
import sensecloud.web.entity.ConnectorEntity;


@Mapper
public interface ConnectorAttachmentMapper extends BaseMapper<ConnectorAttachmentEntity> {

    @Update("update connector_attachment set deleted = true where connector_id = #{_parameter}")
    boolean deleteAll(String connectorId);

}
