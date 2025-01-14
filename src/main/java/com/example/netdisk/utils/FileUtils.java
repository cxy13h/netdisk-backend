package com.example.netdisk.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FileUtils {
    public static String getMD5(InputStream file) throws IOException {
        return DigestUtils.md5DigestAsHex(file);
    }
    public static String generateUniqueId() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
