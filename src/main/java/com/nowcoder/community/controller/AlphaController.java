package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zrxggg
 * @create 2022-04-13-17:16
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @ResponseBody
    @RequestMapping("/hello")
    public String sayHello(){
        return "hello spring boot";
    }
}
