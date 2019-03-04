package com.quantchi.web.metadata.controller;

import com.quantchi.web.metadata.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metadata/column")
public class ColumnController {

    @Autowired
    private ColumnService columnService;


}
