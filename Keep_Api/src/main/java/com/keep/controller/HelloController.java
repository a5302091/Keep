package com.keep.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/9/3.
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(){
        return "hello boot";
    }
}
