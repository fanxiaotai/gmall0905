package com.atguigu.gmall.manage.reidssonTest;

import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

/**
 * @Author: fanyitai
 * @Date: 2019/12/21 10:36
 * @Version 1.0
 */
@Controller
public class redissonTest {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("test")
    @ResponseBody
    public String testRedisson(){
        Jedis jedis =redisUtil.getJedis();

        RLock anyLock = redissonClient.getLock("anyLock");
        //加锁
        anyLock.lock();
        try {
            String v = jedis.get("k");
            if (StringUtils.isBlank(v)){
                v="1";
            }
            System.out.println(v);
            jedis.set("k",Integer.parseInt(v)+1+"");

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
            anyLock.unlock();
        }
        //System.out.println(anyLock);
        return "success";
    }
}
