package com.zyx.seckill.Controller;

import com.zyx.seckill.pojo.User;
import com.zyx.seckill.service.IGoodsService;
import com.zyx.seckill.service.IUserService;
import com.zyx.seckill.vo.DetailVo;
import com.zyx.seckill.vo.GoodsVo;
import com.zyx.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;//thymeleaf手动渲染

    /**
     * 功能描述：跳转到商品列表页面
     * 优化：不再做一个简单的页面跳转，做页面缓存
     * Windows 优化前QPS：1673.4/sec
     *         缓存优化后QPS：2598.1/sec
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){//Model model, 后端从控制层直接返回前端所需的数据
         /*优化前
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        log.info(model.getAttribute("goodsList").toString());
        return "goodsList";
        */

        //Redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        //如果为空，先准备数据model
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        //再手动渲染，存入Redis并返回
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(),
                model.asMap()); //model.asMap()：放入thymeleaf模板引擎中的数据
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);//模板名称，WebContext
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);//存入redis，并设置过期时间
        }
        return html;
    }


    /**
     * 功能描述: 跳转商品详情页--缓存优化
     * @param request
     * @param response
     * @return
     *
     * @since: 1.0.0
     * @Author:zyx
     */
//    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
//    @ResponseBody//一定记得加！因为这里返回的是一个页面对象
//    public String toDetail2(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response){
//        //Redis中获取页面，如果不为空，直接返回页面
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String html = (String) valueOperations.get("goodsDetail" + goodsId);
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//        //为空，先准备数据model
//        model.addAttribute("user", user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowdate = new Date();
//        //秒杀状态
//        int secKillStatus = 0;
//        //秒杀倒计时
//        int remainSeconds = 0;
//        //秒杀还未开始
//        if (nowdate.before(startDate)){
//            remainSeconds = (int) ((startDate.getTime() - nowdate.getTime()) / 1000);
//        }else if ((nowdate.after(endDate))){
//            //秒杀已结束
//            secKillStatus = 2;
//            remainSeconds = -1;
//        }else {
//            //秒杀中
//            secKillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("remainSeconds", remainSeconds);
//        model.addAttribute("secKillStatus", secKillStatus);
//        model.addAttribute("goods", goodsVo);
//
//        //手动渲染，存入redis，并返回
//        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(),
//                model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
//        if (!StringUtils.isEmpty(html)) {
//            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
//        }
//        return html;
//        //return "goodsDetail";
//    }


    /**
     * 功能描述: 跳转商品详情页--页面静态化，前后端分离
     * @param user
     * @param goodsId
     * @return
     *
     * @since: 1.0.0
     * @Author:zyx
     */
    @RequestMapping( "/detail/{goodsId}")
    @ResponseBody//一定记得加！因为这里返回的是一个页面对象
    public RespBean toDetail(Model model, User user, @PathVariable Long goodsId){
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowdate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        //秒杀还未开始
        if (nowdate.before(startDate)){
            secKillStatus = 0;
            remainSeconds = (int) ((startDate.getTime() - nowdate.getTime()) / 1000);
        }else if ((nowdate.after(endDate))){
            //秒杀已结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo(user, goodsVo, secKillStatus, remainSeconds);
        //return "goodsDetail";
        return RespBean.success(detailVo);

    }
}
