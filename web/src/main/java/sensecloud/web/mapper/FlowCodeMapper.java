package sensecloud.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import sensecloud.web.entity.FlowCodeEntity;

public interface FlowCodeMapper extends BaseMapper<FlowCodeEntity> {

    @Select("select * from flow_code where flow_id = #{_parameter} and deleted = 0 order by version desc limit 1")
    FlowCodeEntity getLatestVersion(Long flowId);

}
