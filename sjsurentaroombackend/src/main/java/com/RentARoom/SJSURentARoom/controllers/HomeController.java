package com.RentARoom.SJSURentARoom.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



// Homepage
// Most basic endpoint
@RestController
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "hello";
    }
}
