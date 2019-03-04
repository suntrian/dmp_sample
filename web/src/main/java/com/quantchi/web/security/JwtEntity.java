package com.quantchi.web.security;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtEntity implements Serializable {

    private String token;

}
