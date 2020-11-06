package sensecloud.web.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AbUser对象", description="")
public class AbUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    private Boolean active;

    private String email;

    private LocalDateTime lastLogin;

    private Integer loginCount;

    private Integer failLoginCount;

    private LocalDateTime createdOn;

    private LocalDateTime changedOn;

    private Integer createdByFk;

    private Integer changedByFk;

}
