package com.zhangwei.springboot.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/test1")
public class MvcController {

    @RequestMapping("/modelAndView")
    public ModelAndView modelAndView(){

        return new ModelAndView("view");

    }
}

