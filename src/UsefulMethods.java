import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class UsefulMethods {
    static String getResponse(String url) {
        String result = "";
        try {
            URL query = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) query.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    static String checkIsNull(JSONObject jsonString, String field, String parentObject) {
        try {
            if (parentObject != null)
                field = jsonString.getJSONObject(parentObject).getString(field);
            else field = jsonString.getString(field);
            if (field.length() <= 1)
                field = "нет данных";
        } catch (JSONException ex) {
            field = "нет данных";
        }
        return field;
    }
}
