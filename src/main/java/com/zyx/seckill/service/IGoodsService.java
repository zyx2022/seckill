package com.zyx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyx.seckill.pojo.Goods;
import com.zyx.seckill.vo.GoodsVo;

import java.util.List;

/**

 * @author zyx
 * @since 2022-07-23
 */
public interface IGoodsService extends IService<Goods> {
    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
