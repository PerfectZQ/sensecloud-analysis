package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sensecloud.web.entity.Role;
import sensecloud.web.entity.User;
import sensecloud.web.entity.UserRole;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangqiang
 * @since 2020/12/14 14:30
 */
@Service
@Slf4j
public class SenseCloudUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getOne(new QueryWrapper<>(new User().setUsername(username)));
        if (user == null) throw new UsernameNotFoundException(username + " not found");
        List<UserRole> userRoles = userRoleService.list(new QueryWrapper<>(new UserRole().setUserId(user.getId())));
        List<SimpleGrantedAuthority> authorities = userRoles.stream()
                .filter(userRole -> {
                    Role role = roleService.getById(userRole.getRoleId());
                    boolean isFiltered = !(role == null || StringUtils.isEmpty(role.getName()));
                    if (isFiltered) {
                        log.error(username + ":　null or empty role " + userRole.getRoleId() + " is filtered");
                    }
                    return isFiltered;
                })
                .map(userRole -> new SimpleGrantedAuthority(roleService.getById(userRole.getRoleId()).getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
    }
}
