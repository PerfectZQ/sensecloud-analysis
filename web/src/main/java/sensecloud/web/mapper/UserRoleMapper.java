package sensecloud.web.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import sensecloud.web.bean.vo.UserRoleProductVO;
import sensecloud.web.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-12-10
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 分页查询 UserRoleProductVO
     *
     * @param page
     * @param query
     * @return
     * @See resources/mapper/UserRoleMapper.xml, selectId = listUserRoleProductVOPage
     */
    List<UserRoleProductVO> listUserRoleProductVOPage(Page<UserRoleProductVO> page,
                                                      @Param("query") UserRoleProductVO query);

}
