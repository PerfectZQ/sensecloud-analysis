package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sensecloud.web.bean.vo.UserAuthorityVO;
import sensecloud.web.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

import static sensecloud.web.constant.CommonConstant.SENSE_ANALYSIS_ADMIN_NAME;

/**
 * @author zhangqiang
 * @since 2020/12/14 14:30
 */
@Service
@Slf4j
public class SenseCloudUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private UserAuthorityServiceImpl userRoleProductService;

    /**
     * 从数据库加载用户详细信息
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("====> Load user by username {}", username);
        UserEntity userEntity = userService.getOne(new QueryWrapper<>(new UserEntity().setUsername(username)));
        if (userEntity == null) {
            log.error("====> User {} not found", username);
            throw new UsernameNotFoundException(username + " not found");
        }
        // Get all roles info of this user
        List<UserAuthorityVO> userAuthorities = userRoleProductService.getBaseMapper()
                .listUserAuthorityVOPage(
                        new Page<>(1, Integer.MAX_VALUE),
                        new UserAuthorityVO().setUserId(userEntity.getId()));
        List<SimpleGrantedAuthority> authorities = userAuthorities.stream()
                .filter(userAuthority -> {
                    boolean filter = !StringUtils.isEmpty(userAuthority.getRoleName());
                    if (!filter)
                        log.error(username + ":　null or empty role " + userAuthority.getRoleId() + " is filtered");
                    return filter;
                })
                .map(userAuthority -> {
                    String roleName = userAuthority.getRoleName();
                    String productName = userAuthority.getProductName();
                    String suffix = SENSE_ANALYSIS_ADMIN_NAME.equalsIgnoreCase(roleName) ||
                            StringUtils.isEmpty(productName) ? ""
                            :
                            ":" + productName;
                    String authority = roleName + suffix;
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());
        String password = userEntity.getPassword() == null ?
                passwordEncoder.encode("") :
                passwordEncoder.encode(userEntity.getPassword());
        return new org.springframework.security.core.userdetails.User(username, password, authorities);
    }
}
