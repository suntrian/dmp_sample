package com.quantchi.web.metadata.controller;


import com.quantchi.web.metadata.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metadata/database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;
}
