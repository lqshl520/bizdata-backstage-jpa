package com.bizdata.admin.controller;

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

    @RequestMapping(value = "/test")
    @ResponseBody
    public void test() {
    }
}
