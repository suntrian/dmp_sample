package com.quantchi.web;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Profile("corp")
@Transactional
@SpringBootTest(classes = DMPApplication.class)
@RunWith(SpringRunner.class)
@WebAppConfiguration
public class TestBase {
}
