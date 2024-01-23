package com.sbspro.midproject.domain.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/adm/home")
@Controller
public class AdminHomeController {
    @GetMapping("/main")
    public String showMain() {
        return "adm/home/main";
    }
}
