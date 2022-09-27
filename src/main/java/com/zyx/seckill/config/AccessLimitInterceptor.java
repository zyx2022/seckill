package com.zyx.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.zyx.seckill.pojo.User;
import com.zyx.seckill.service.IUserService;
import com.zyx.seckill.utils.CookieUtil;
import com.zyx.seckill.vo.RespBean;
import com.zyx.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * 拦截器：在spring中用拦截器需要实现HandlerInterceptor接口
 * 或者它的实现子类：HandlerInterceptorAdapter，同时在applicationContext.xml文件中配置拦截器
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断它是不是一个被拦截的一个方法
        if(handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            //获取这个方法上面的注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            //看有没有这个注解
            if(accessLimit == null) {
                //没有这个注解 直接跳过拦截
                return true;
            }
            //拿到这个注解的相关描述
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            if(needLogin) {
                if(user == null) {
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
            }

            //开始限流处理，核心代码
            ValueOperations valueOperations = redisTemplate.opsForValue();
            //发起请求的地址，限制访问次数，
            String uri = request.getRequestURI();
            Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
            String key = uri + ":" + user.getId();
            if(count == null) {
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            } else if(count < maxCount) {
                valueOperations.increment(key);
            } else {
                render(response, RespBeanEnum.ACCESS_LIMIT_REAHCED);
                return false;
            }
        }

        return true;
    }

    /**
     * 构建返回对象
     * @param response
     * @param error
     * @throws IOException
     */
    private void render(HttpServletResponse response, RespBeanEnum error) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        RespBean respBean = RespBean.error(error);
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(respBean));
        writer.flush();
        writer.close();
    }


    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(ticket)) {
            return null;
        }
        return userService.getUserByCookie(ticket,request,response);
    }
}
