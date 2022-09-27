package com.zyx.seckill.vo;

import com.zyx.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;


/**
 * 登录参数
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Data
public class LoginVo {
	@NotNull
	@IsMobile
	private String mobile;

	@NotNull
	@Length(min = 32)
	private String password;

}