package com.zyx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyx.seckill.pojo.SeckillOrder;
import com.zyx.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 * zyx
 * @author jobob
 * @since 2022-07-23
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取用户秒杀的结果
     * @param user
     * @param goodsId
     * @return order:成功；-1:失败； 0:排队中
     */
    Long getSeckillResult(User user, String goodsId);
}
