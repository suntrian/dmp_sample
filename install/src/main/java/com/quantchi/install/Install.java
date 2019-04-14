package com.quantchi.install;

import java.io.File;
import java.net.URL;

public class Install {

    private static Install instance = null;

    private Install() {
    }

    public static Install getInstance() {

        if (instance == null) {
            synchronized (Install.class) {
                if (instance == null) {
                    instance = new Install();
                    return instance;
                }
            }
        }
        return instance;
    }


    public void init_database(String type, String host, int port, String username, String password) {
        URL url = getClass().getResource("/mysql8_init.sql");
        File initSql = new File(url.getFile());


    }

    public void init_organization() {

    }

    public void init_configuration() {

    }

}
