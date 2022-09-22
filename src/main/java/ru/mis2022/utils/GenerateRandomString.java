package ru.mis2022.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GenerateRandomString {
    public String getRndStr(int len){
        String abc = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random(System.nanoTime());

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(abc.charAt(rnd.nextInt(abc.length())));
        }
        return sb.toString();
    }
}
