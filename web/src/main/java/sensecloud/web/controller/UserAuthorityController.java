package sensecloud.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.vo.ResultVO;
import sensecloud.web.bean.vo.UserAuthorityVO;
import sensecloud.web.entity.UserAuthority;
import sensecloud.web.service.impl.UserAuthorityServiceImpl;

import java.util.List;


/**
 * @author zhangqiang
 * @since 2020/11/5 16:17
 */
@RestController
@RequestMapping("/api/v1/userAuthority")
@Slf4j
public class UserAuthorityController {

    @Autowired
    private UserAuthorityServiceImpl userAuthorityService;

    @ApiOperation(value = "分页获取 UserAuthorityVO")
    @PostMapping("listUserAuthorities/{pageId}/{pageSize}")
    public PageResult listUserAuthorities(@PathVariable Integer pageId,
                                          @PathVariable Integer pageSize,
                                          @RequestBody UserAuthorityVO userAuthorityVO) {

        Page<UserAuthorityVO> page = new Page<>(pageId, pageSize);
        List<UserAuthorityVO> results = userAuthorityService.getBaseMapper().listUserAuthorityVOPage(page, userAuthorityVO);
        return PageResult.builder()
                .currentPageId(pageId)
                .currentPageElems(results)
                .totalPages(page.getPages())
                .totalElems(page.getTotal())
                .build();
    }

    @ApiOperation(value = "Delete UserAuthority")
    @PostMapping("deleteUserAuthority")
    public ResultVO<String> deleteUserAuthority(@RequestBody UserAuthority userAuthority) {
        if (userAuthorityService.remove(new QueryWrapper<>(userAuthority)))
            return ResultVO.ok("");
        else
            return ResultVO.error("删除失败");
    }

}
