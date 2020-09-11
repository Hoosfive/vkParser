import com.vk.api.sdk.client.actors.UserActor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

class GetMainInfo implements Callable {

    private JSONObject jsonParser;
    private int userID;
    private UserActor actor;

    GetMainInfo(int userID, UserActor actor
    ) {

    }

    @Override
    public User call() {
        String mainInfoRequest = ("https://api.vk.com/method/users.get?user_id=" + userID + "&fields=country,city,contacts,photo_200,education,bdate,home_town&access_token=" + actor.getAccessToken() + "&v=5.103 ");
        String jsonResult = UsefulMethods.getResponse(mainInfoRequest);
        try {
            jsonParser = (new JSONObject(jsonResult)).getJSONArray("response").getJSONObject(0);
        } catch (JSONException je) {
            je.printStackTrace();

            mainWin.changeToken("Something wrong with token");
        }
        return new User(userID,
                UsefulMethods.checkIsNull(jsonParser, "mobile_phone", null),
                UsefulMethods.checkIsNull(jsonParser, "first_name", null),
                UsefulMethods.checkIsNull(jsonParser, "last_name", null),
                UsefulMethods.checkIsNull(jsonParser, "photo_200", null),
                UsefulMethods.checkIsNull(jsonParser, "title", "country"),
                UsefulMethods.checkIsNull(jsonParser, "title", "city"),
                UsefulMethods.checkIsNull(jsonParser, "university_name", null),
                UsefulMethods.checkIsNull(jsonParser, "bdate", null),
                UsefulMethods.checkIsNull(jsonParser, "home_town", null),
                isPrivate(jsonParser.getBoolean("can_access_closed")));
    }
    private String isPrivate(boolean isOpen) {
        if (isOpen)
            return "Открыт";
        else {
            return "Закрыт";
        }
    }
}