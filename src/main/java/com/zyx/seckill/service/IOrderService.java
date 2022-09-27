package com.zyx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyx.seckill.pojo.Order;
import com.zyx.seckill.pojo.User;
import com.zyx.seckill.vo.GoodsVo;
import com.zyx.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 * zyx
 * @author jobob
 * @since 2022-07-23
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order secKill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    Boolean checkPath(User user, Long goodsId, String path);

    Boolean checkCptcha(User user, Long goodsId, String captcha);
}
