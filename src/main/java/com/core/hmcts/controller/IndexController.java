package com.core.hmcts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {
    @RequestMapping(value = "/documentation", method= RequestMethod.GET)
    public String requestMethodName() {
        return "redirect:/swagger-ui/index.html";
    }
}

