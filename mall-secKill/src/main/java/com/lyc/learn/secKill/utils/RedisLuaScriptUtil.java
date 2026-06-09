package com.lyc.learn.secKill.utils;

import com.lyc.learn.secKill.utils.MD5Utils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class RedisLuaScriptUtil {
    
    private static String secKill_lua_val;
    private static String secKill_lua_md5;
    public static String getSecKillLua() throws IOException, NoSuchAlgorithmException {
        InputStream is = new ClassPathResource("lua/SecKillLua.lua").getInputStream();
        String inputStreamMD5 = MD5Utils.getInputStreamMD5(is);
        if (inputStreamMD5.equals(secKill_lua_md5)) return secKill_lua_val;

        try (InputStream is2 = new ClassPathResource("lua/SecKillLua.lua").getInputStream()) {
            secKill_lua_val = StreamUtils.copyToString(is2, StandardCharsets.UTF_8);
        }
        secKill_lua_md5 = inputStreamMD5;
        return secKill_lua_val;
    }
}
