package com.magurovs.techsupport.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class NetworkUtils {

    public static JSONObject buildJSON(Map<String, String> dataFromFields) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", dataFromFields.get("name"));
            jsonObject.put("lastName", dataFromFields.get("lastName"));
            jsonObject.put("middleName", dataFromFields.get("middleName"));
            jsonObject.put("comment", dataFromFields.get("comment"));
            jsonObject.put("timeProblem", dataFromFields.get("timeProblem"));
            jsonObject.put("typeProblem", dataFromFields.get("typeProblem"));
        } catch (JSONException e) {
            return null;
        }

        return jsonObject;
    }
}
