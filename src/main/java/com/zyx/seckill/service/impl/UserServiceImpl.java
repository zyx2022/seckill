package com.zyx.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyx.seckill.exception.GlobalException;
import com.zyx.seckill.mapper.UserMapper;
import com.zyx.seckill.pojo.User;
import com.zyx.seckill.service.IUserService;
import com.zyx.seckill.utils.CookieUtil;
import com.zyx.seckill.utils.MD5Util;
import com.zyx.seckill.utils.UUIDUtil;
import com.zyx.seckill.utils.ValidatorUtil;
import com.zyx.seckill.vo.LoginVo;
import com.zyx.seckill.vo.RespBean;
import com.zyx.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 * zyx
 * @author jobob
 * @since 2022-07-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 功能描述: 登录
     * @param loginVo
     * @param request
     * @param response
     * @return
     *
     * @since: 1.0.0
     * @Author:zyx
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        /*
        //参数校验,判断手机号或者密码是否为空
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //判断手机号格式是否正确
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
         */

        //根据手机号获取用户，判断用户是否存在
        //根据手机号获取用户
        User user = baseMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        boolean flag = MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword());
        if(!flag){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //生成Cookie
        String ticket = UUIDUtil.uuid();
        //将用户id作为键，user作为值存入redis
        redisTemplate.opsForValue().set("user:" + ticket, user);
//        request.getSession().setAttribute(ticket, user);
        //其实就是找到请求的发起地址，给发请求的地址设置一个cookie，后面的请求都让他带上这个userTicket
        CookieUtil.setCookie(request, response, "userTicket", ticket);
//        共享session：出于负载均衡的考虑，分布式服务会将用户信息的访问均衡到不同服务器上，
//        用户刷新一次访问可能会需要重新登录，为避免这个问题可以用redis将用户session集中管理，
//        在这种模式下只要保证redis的高可用和扩展性的，每次获取用户更新或查询登录信息 都直接从redis中集中获取。
        return RespBean.success(ticket);
    }


    /**
     * 功能描述: 根据cookie去获取用户
     * @param userTicket
     * @param request
     * @param response
     * @return
     *
     * @since: 1.0.0
     * @Author:zyx
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        //优化
        if (null != user){
            //用户不为空，重新把cookie设置一下，以防万一，刷新保存用户信息的过期时间
            CookieUtil.setCookie(request,response, "userTicket", userTicket);
        }
        return user;
    }


    /**
     * 功能描述: 更新密码
     * @param: userTicket
     * @param: password
     * @param: request
     * @param: response
     * @return:

     * @since: 1.0.0
     * @Author:zyx
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        //拿到user对象
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        //更新user对象
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = baseMapper.updateById(user);
        //删除redis中缓存的user对象信息
        if (1 == result) {
            //删除Redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
