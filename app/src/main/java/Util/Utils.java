package Util;


import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    public static final String BASE_URL = "api.openweathermap.org/data/2.5/weather?q=";
    public static final String ICON_URL = "openweathermap.org/img/w/";

    public static JSONObject getObject(String tagName, JSONObject jsonObject) throws JSONException{
        JSONObject jObject  = jsonObject.getJSONObject(tagName);
        return jObject;
    }

    public static String getString(String tagName, JSONObject jsonObject) throws JSONException{
        return jsonObject.getString(tagName);
    }
    public static float getFloat(String tagName, JSONObject jsonObject) throws JSONException{
        return (float) jsonObject.getDouble(tagName);
    }
    public static double getDouble(String tagName, JSONObject jsonObject) throws JSONException{
        return jsonObject.getDouble(tagName);
    }
    public static int getInt(String tagName, JSONObject jsonObject) throws JSONException{
        return jsonObject.getInt(tagName);
    }

}
