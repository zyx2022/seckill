package com.zyx.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {

//
//    @Autowired
//    private RedisTemplate redisTemplate;
//    @Autowired
//    private RedisScript<Long> script;
//
//    /**
//     * 若出现异常，会出现死锁问题
//     */
//    @Test
//    public void contextLoads() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //不存在才可以设置成功
//        Boolean isLock = valueOperations.setIfAbsent("k1", "y1");
//        //如果占位成功，进行正常操作
//        if(isLock) {
//            valueOperations.set("name", "xxxx");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name = " + name);
//            //操作结束，删除锁
//            redisTemplate.delete("k1");
//        } else {
//            System.out.println("有线程使用，请稍后再试");
//        }
//    }
//
//    /**
//     * 解决因异常出现死锁问题，设置超时时间
//     * 但还是会有问题，因为锁住部分操作时间不确定，容易发生紊乱,可能会出现后面线程的锁被前面线程删掉问题
//     */
//    @Test
//    public void testLock02() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        Boolean lock = valueOperations.setIfAbsent("k1", "y1", 10, TimeUnit.SECONDS);
//        if(lock) {
//            valueOperations.set("name", "xxxx");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name = " + name);
//            Integer.parseInt("XXXX");
//            redisTemplate.delete("name");
//        } else {
//            System.out.println("有线程使用，请稍后再试");
//        }
//    }
//
//    /**
//     * 加锁、获取锁、比较锁、删除锁
//     * 不是原子操作，通过Redis内置的lua脚本，在Redis服务端原子性的执行多个Redis命令，一次执行多个Redis命令
//     */
//    @Test
//    public void testLock03() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String value = UUID.randomUUID().toString();
//        Boolean isLock = valueOperations.setIfAbsent("k1", value, 120, TimeUnit.SECONDS);
//        if (isLock) {
//            valueOperations.set("name", "xxxx");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name = " + name);
//            System.out.println("k1 = " + valueOperations.get("k1"));
//
//            Boolean res = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"), value);
//            System.out.println(res);
//        } else {
//            System.out.println("有线程在使用，请稍后");
//        }
//
//    }

}
