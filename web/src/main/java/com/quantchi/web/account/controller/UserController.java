package com.quantchi.web.account.controller;

import com.quantchi.web.account.entity.UserAccount;
import com.quantchi.web.account.service.UserService;
import com.quantchi.web.common.MessageHolder;
import com.quantchi.web.common.ResultDto;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Locale;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{:id\\d+}")
    public ResultDto getUser(@PathVariable("id") Integer id){
        UserAccount user =userService.get(id);
        return ResultDto.success(user);
    }


}
