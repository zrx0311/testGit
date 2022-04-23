package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author zrxggg
 * @create 2022-04-15-21:08
 */
public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密,只能加密不能解密
    //(hello+随机字符串) -> abc123def456abc
    public static String md5(String key){
        if (StringUtils.isBlank(key)){ //判断为空，空字符串，空格
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
    //服务器向浏览器返回的JSON数据可能有不同的包含内容，进行重载
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code, String msg){

        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){

        return getJSONString(code,null,null);
    }
}
