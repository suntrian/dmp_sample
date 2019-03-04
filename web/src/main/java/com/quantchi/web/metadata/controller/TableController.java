package com.quantchi.web.metadata.controller;


import com.quantchi.web.metadata.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metadata/table")
public class TableController {

    @Autowired
    private TableService tableService;
}
