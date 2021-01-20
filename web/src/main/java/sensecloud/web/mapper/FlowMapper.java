package sensecloud.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import sensecloud.web.entity.FlowEntity;

import java.util.List;

public interface FlowMapper extends BaseMapper<FlowEntity> {

    @Results(id = "flow_with_code", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "name", column = "name"),
            @Result(property = "dagId", column = "dag_id"),
            @Result(property = "scheduleExpr", column = "schedule_expr"),
            @Result(property = "tasks", column = "id", many = @Many(select = "sensecloud.web.mapper.FlowCodeMapper.getLatestVersion", fetchType = FetchType.DEFAULT)),
            @Result(property = "createBy", column = "create_by"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateBy", column = "update_by"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "deleteBy", column = "delete_by"),
            @Result(property = "deleteTime", column = "delete_time")
    })
    @Select("select * from flow where id = #{_parameter} and deleted = 0")
    FlowEntity getWithTasks(Long id);
}
