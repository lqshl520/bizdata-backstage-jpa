package com.bizdata.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试
 * <p>
 * Created by sdevil507 on 2017/8/16.
 */
@Controller
public class TestController {

    @Autowired
    private RedisTemplate<String,String > redisTemplate;

    @RequestMapping(value = "/test")
    @ResponseBody
    public void test() {
        redisTemplate.opsForValue().set("username","sdevil507");
    }
}
