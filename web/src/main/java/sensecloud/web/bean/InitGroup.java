package sensecloud.web.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhangqiang
 * @since 2020/11/20 15:49
 */
@Data
@Accessors(chain = true)
public class InitGroup {
    private String username;
    private String groupName;
    private String repository;
    private String branch;
}
