package sensecloud.web.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import sensecloud.web.entity.WebComponentRoleMapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import sensecloud.web.entity.WebComponentRoleMappingVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-11-05
 */
public interface WebComponentRoleMappingMapper extends BaseMapper<WebComponentRoleMapping> {
    @Select("select\n" +
            "   wcrm.id,\n" +
            "   wcrm.web_role_id,\n" +
            "   wr.name web_role_name,\n" +
            "   wr.component_id web_role_component_id,\n" +
            "   wc.name web_role_component_name,\n" +
            "   wcrm.component_role_id,\n" +
            "   cr.name component_role_name,\n" +
            "   cr.component_id component_role_component_id,\n" +
            "   cc.name component_role_component_name,\n" +
            "   wcrm.enabled\n" +
            "from\n" +
            "   web_component_role_mapping wcrm\n" +
            "left join `role` wr on\n" +
            "   wcrm.web_role_id = wr.id\n" +
            "left join `role` cr on \n" +
            "   wcrm.component_role_id = cr.id\n" +
            "left join component cc on \n" +
            "   cr.component_id = cc.id\n" +
            "left join component wc on \n" +
            "   wr.component_id = wc.id\n" +
            "where\n" +
            "   wcrm.web_role_id = #{webRoleId};")
    List<WebComponentRoleMappingVO> getWebComponentRoleMappingVOByWebRoleId(@Param("webRoleId") Integer webRoleId);
}
