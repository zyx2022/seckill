package com.zyx.seckill.Controller;


import com.zyx.seckill.pojo.User;
import com.zyx.seckill.service.IOrderService;
import com.zyx.seckill.vo.OrderDetailVo;
import com.zyx.seckill.vo.RespBean;
import com.zyx.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 * zyx
 * @author jobob
 * @since 2022-07-23
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    /**
     * 功能描述: 订单详情

     * @since: 1.0.0
     * @Author:zyx
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }
}
