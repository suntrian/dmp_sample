package com.quantchi.web.metadata.controller;

import com.quantchi.web.metadata.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metadata/system")
public class SystemController {

    @Autowired
    private SystemService systemService;
}
