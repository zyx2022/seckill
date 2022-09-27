package com.zyx.seckill.vo;


import com.zyx.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述：页面详情返回对象
 * @author zyx
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

	private User user;

	private GoodsVo goodsVo;

	private int secKillStatus;

	private int remainSeconds;
}
