package com.zyx.seckill.config;

import com.zyx.seckill.pojo.User;

/**
 *
 * @author zyx
 * @since 1.0.0
 */
public class UserContext {

	private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

	public static void setUser(User user) {
		userHolder.set(user);
	}

	public static User getUser() {
		return userHolder.get();
	}
}
