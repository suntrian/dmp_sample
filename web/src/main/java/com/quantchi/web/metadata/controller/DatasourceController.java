package com.quantchi.web.metadata.controller;

import com.quantchi.web.metadata.service.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metadata/datasource")
public class DatasourceController {

    @Autowired
    private DatasourceService datasourceService;
}
