package sensecloud.web.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import sensecloud.web.bean.vo.UserAuthorityVO;
import sensecloud.web.entity.UserAuthority;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-12-17
 */
public interface UserAuthorityMapper extends BaseMapper<UserAuthority> {

    /**
     * 分页查询 UserRoleProductVO
     *
     * @param page
     * @param query
     * @return
     * @See resources/mapper/UserRoleMapper.xml, selectId = listUserRoleProductVOPage
     */
    List<UserAuthorityVO> listUserAuthorityVOPage(Page<UserAuthorityVO> page,
                                                  @Param("query") UserAuthorityVO query);
}
