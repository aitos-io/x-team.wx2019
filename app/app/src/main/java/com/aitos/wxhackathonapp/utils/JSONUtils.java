package com.aitos.wxhackathonapp.utils;

import org.json.JSONObject;

import java.util.HashMap;

public class JSONUtils {

    public static String toJsonStr(String msg) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", msg);
        return toJsonStr(map);
    }

    public static String toJsonStr(HashMap<String, Object> map) {
        JSONObject json = new JSONObject(map);
        return json.toString();
    }

}
