package com.usermgr.admin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String admin = encoder.encode("admin123");
        String zhangsan = encoder.encode("123456");
        System.out.println("admin = " + admin);
        System.out.println("zhangsan = " + zhangsan);
    }
}
