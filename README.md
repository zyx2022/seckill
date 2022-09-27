# 秒杀系统说明

## 项目简介
基于Spring Boot，构建项目框架，实现秒杀模块CRUD基本功能。
基于Redis，通过页面、对象缓存和页面静态化，实现页面优化。
通过Redis预减库存，内存标记，基于RabbitMQ完成异步下单，实现接口优化。
通过接口地址隐藏、验证码、接口限流等多种安全机制，实现安全优化。
使用JMeter压测，前后对比，高并发性能稳步提升。
项目中前端代码均为开源项目所提供，后端代码全部为本人所写并完善。

## 技术架构
用户层：H5、axios 

应用层：Spring boot、Spring MVC、Spring

数据层：MySQL、Mybatis、Redis、RabbitMQ

工具：git、maven、junit

## 项目框架搭建
	1.SpringBoot环境搭建
	2.集成Thymeleaf , RespBean
	3.MyBatis

## 分布式会话
	1.用户登录
	a.设计数据库
	b.明文密码二次MD5加密
	c.参数校验+全局异常处理
	2.共享session
		a. SpringSession
		b. Redis

## 功能开发
	1.商品列表
	2.商品详情
	3.秒杀
	4.订单详情

## 系统压测
	1.JMeter
	2.自定义变量模拟多用户
	3.JMeter命令行的使用
	4.正式压测

## 页面优化
	1.页面缓存+URL缓存+对象缓存
	2.页面静态化，前后端分离
	3.静态资源优化
	4.CDN优化

## 接口优化
	1. Redis预减库存减少数据库的访问
	2.内存标记减少Redis的访问
	3.RabbitMQ异步下单
		a.springBoot整合RabbitMQ
		b.交换机

## 安全优化
	1.秒杀接口地址隐藏
	2.算术验证码
	3.接口防刷

