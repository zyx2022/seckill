package com.zyx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyx.seckill.pojo.User;
import com.zyx.seckill.vo.LoginVo;
import com.zyx.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 * zyx
 * @author jobob
 * @since 2022-07-14
 */
public interface IUserService extends IService<User> {

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
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

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
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

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
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
