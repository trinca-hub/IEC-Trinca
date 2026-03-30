package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import ch.qos.logback.core.model.Model;

@Controller
@RequestMapping("/fatecads")
public class FatecAdsController {
    
    @GetMapping
    public String index(Model model){
        return"index";
    }
}
