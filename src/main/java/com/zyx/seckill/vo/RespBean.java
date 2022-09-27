package com.zyx.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共返回对象
 * @author zyx
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {

	private long code;
	private String message;
	private Object obj;

	/**
	 * 功能描述: 成功返回结果
	 *
	 * @param:
	 * @return:
	 *

	 *
	 * @since: 1.0.0
	 * @Author: zyx
	 */
	public static RespBean success(){
		return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(),null);
	}

	/**
	 * 功能描述: 成功返回结果
	 *
	 * @param:
	 * @return:
	 *
	 * @since: 1.0.0
	 * @Author: zyx
	 */
	public static RespBean success(Object obj){
		return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBean.success().getMessage(),obj);
	}


	/**
	 * 功能描述: 失败返回结果
	 *
	 * @param:
	 * @return:
	 *
	 * @since: 1.0.0
	 * @Author: zyx
	 */
	public static RespBean error(RespBeanEnum respBeanEnum){
		return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),null);
	}


	/**
	 * 功能描述: 失败返回结果
	 *
	 * @param:
	 * @return:
	 *

	 *
	 * @since: 1.0.0
	 * @Author: zyx
	 */
	public static RespBean error(RespBeanEnum respBeanEnum,Object obj){
		return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),obj);
	}

}