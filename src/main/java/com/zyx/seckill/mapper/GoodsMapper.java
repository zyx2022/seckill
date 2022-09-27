package com.zyx.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyx.seckill.pojo.Goods;
import com.zyx.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 * zyx
 * @author jobob
 * @since 2022-07-23
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
