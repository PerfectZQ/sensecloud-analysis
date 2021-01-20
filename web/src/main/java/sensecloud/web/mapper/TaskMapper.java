package sensecloud.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import sensecloud.web.entity.TaskEntity;

import java.util.List;
import java.util.Set;

public interface TaskMapper extends BaseMapper<TaskEntity>{

    @Select("select * from task where deleted = 0 and flow_id = #{flowId}")
    List<TaskEntity> getTasks(@Param("flowId") Long flowId);

}
