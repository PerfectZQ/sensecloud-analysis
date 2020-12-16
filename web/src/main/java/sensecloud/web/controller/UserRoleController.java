package sensecloud.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.vo.UserRoleProductVO;
import sensecloud.web.entity.UserRole;
import sensecloud.web.service.impl.UserRoleServiceImpl;

import java.util.List;


/**
 * @author zhangqiang
 * @since 2020/11/5 16:17
 */
@RestController
@RequestMapping("/api/v1/userRole")
@Slf4j
public class UserRoleController {

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @ApiOperation(value = "分页获取 UserRoleByProductVO")
    @PostMapping("getUserRoleByProduct/{pageId}/{pageSize}")
    public PageResult getUserRoleByProduct(@PathVariable Integer pageId,
                                           @PathVariable Integer pageSize,
                                           @RequestBody UserRoleProductVO userRoleProductVO) {
        Page<UserRoleProductVO> page = new Page<>(pageId, pageSize);
        List<UserRoleProductVO> results = userRoleService.getBaseMapper().listUserRoleProductVOPage(page, userRoleProductVO);
        return PageResult.builder()
                .currentPageId(pageId)
                .currentPageElems(results)
                .totalPages(page.getPages())
                .totalElems(page.getTotal())
                .build();
    }

    @ApiOperation(value = "删除 UserRole")
    @PostMapping("deleteUserRole")
    public void deleteUserRole(@RequestBody UserRole userRole) {
        userRoleService.remove(new QueryWrapper<>(userRole));
    }

}
