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
public class AccountController {

    @Autowired
    private UserService userService;

    @RequiresPermissions("")
    @GetMapping("/welcome")
    public ResultDto welcome(){
        return ResultDto.success(Arrays.asList(
                MessageHolder.getMessage("message.abc"),
                MessageHolder.getMessage("index.welcome"),
                MessageHolder.getMessage("exception.npe"),
                MessageHolder.getMessage("exception.npe", Locale.ENGLISH),
                MessageHolder.getMessage("abc.abc","abc,abc")
        ));
    }

    @RequiresGuest
    @PostMapping("/register")
    public ResultDto register(UserAccount account){
        int c = userService.addUser(account);
        return c>0?ResultDto.success(MessageHolder.getMessage("message.addUserSucceed"))
                :ResultDto.failure(MessageHolder.getMessage("message.addUserFailure"));
    }

    @RequiresGuest
    @RequestMapping(value = "/login", method ={ RequestMethod.POST, RequestMethod.HEAD} )
    public ResultDto login(@RequestHeader HttpHeaders headers,
                           @RequestBody UserAccount userAccount){

        return ResultDto.success("");
    }

    @RequiresUser
    @PostMapping(value = "/logout")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void logout(){

    }

}
