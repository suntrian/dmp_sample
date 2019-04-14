package com.quantchi.install;

import org.junit.Test;

public class TestInstall {

    @Test
    public void testInitDatabase() {

        Install install = Install.getInstance();
        install.init_database("", "", 0, "", "");

    }
}
