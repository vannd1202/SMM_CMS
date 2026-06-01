package com.example.smm_cms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/index")
    public String homepage(){
        return "success";
    }

}
