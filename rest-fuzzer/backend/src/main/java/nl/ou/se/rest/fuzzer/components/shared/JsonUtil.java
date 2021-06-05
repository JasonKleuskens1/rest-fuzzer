package nl.ou.se.rest.fuzzer.components.shared;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public abstract class JsonUtil {

    // method(s)
    public static Map<String, Object> stringToMap(String json) {
        Map<String, Object> result = new HashMap<String, Object>();

        JSONObject jsonObject = new JSONObject(json);
        String keys[] = JSONObject.getNames(jsonObject);
        if (keys != null) {
            for (String key : keys) {
                result.put(key, jsonObject.get(key));
            }
        }

        return result;
    }

    public static String mapToString(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }
}