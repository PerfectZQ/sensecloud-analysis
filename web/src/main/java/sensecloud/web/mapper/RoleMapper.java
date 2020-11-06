package sensecloud.web.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import sensecloud.web.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import sensecloud.web.entity.RoleComponentVO;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-11-05
 */
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select\n" +
            "   r.id role_id,\n" +
            "   r.name role_name,\n" +
            "   r.component_id,\n" +
            "   c.name component_name\n" +
            "from\n" +
            "   `role` r\n" +
            "left join component c on\n" +
            "   r.component_id = c.id\n" +
            "where\n" +
            "   r.id = #{roleId};")
    RoleComponentVO getRoleComponentVO(@Param("roleId") Integer roleId);

    @Select("select\n" +
            "   r.id role_id,\n" +
            "   r.name role_name,\n" +
            "   r.component_id,\n" +
            "   c.name component_name\n" +
            "from\n" +
            "   `role` r\n" +
            "left join component c on\n" +
            "   r.component_id = c.id\n" +
            "where\n" +
            "   r.name = '#{roleName}'\n" +
            "   and c.name = '#{componentName}'")
    RoleComponentVO getRoleComponentVO(@Param("roleName") String roleName,
                                       @Param("componentName") String componentName);


}
