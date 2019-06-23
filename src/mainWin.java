import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.actions.Users;
import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UsersGetQuery;
import com.vk.api.sdk.queries.users.UsersSearchQuery;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.asynchttpclient.Request;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.*;

class result {
    String id;
    String first_name;
    String last_name;
    String country;
    String city;
    String mobile;
}

public class mainWin extends JFrame{

    private String userID;
    private String jsonResult;
    private VkApiClient vk;
    private JPanel panel1;
    private JTextField useridField;
    private JButton тыкниЁптаButton;
    private JTable resultTable;
    private URL query;
    private HttpURLConnection connection;
    private JSONObject jsonParser;

    private result result;



    mainWin() {
        this.getContentPane().add(panel1);
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        //HttpPost request = new HttpPost("https://oauth.vk.com/authorize?client_id=7029582&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.95&state=123456")

        UserActor actor = new UserActor(155549438, "debc7d22c13b11dc31d1475f1bcbc6926033f32ca19f795f3c77cc0b7fc503fd64694df2b08678c1f84cb");

        тыкниЁптаButton.addActionListener(e -> {
            userID = useridField.getText();
            String url = ("https://api.vk.com/method/users.get?user_id="+userID +"&fields=country,city,contacts&access_token="+actor.getAccessToken() + "&v=5.95");
            try {
                query = new URL(url);
                connection = (HttpURLConnection) query.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                jsonResult = response.toString();
              //  result = g.fromJson(jsonResult, result.class);


                // jsonResult = (Object) new JSONParser().parse(String.valueOf(response));
                System.out.println(jsonResult);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                jsonParser = new JSONObject(jsonResult);
                //jsonParser =  (JSONObject) jsonResult;
// Достаём firstName and lastName 155636030

                JSONArray arr = jsonParser.getJSONArray("response");

                        //String id = oneObject.getString("id");
               // result.id = (arr.getJSONObject(1).getString("id"));
                result.first_name = (arr.getJSONObject(1).getString("first_name"));
                result.last_name = (arr.getJSONObject(2).getString("last_name"));
                /*result.city = (arr.getJSONObject(5).getJSONArray("city").getJSONObject(2).getString("title"));
                result.country = (arr.getJSONObject(6).getJSONArray("country").getJSONObject(2).getString("title"));
                result.mobile = (arr.getJSONObject(7).getString("mobile_phone"));*/
                System.out.println("info: " +  result.first_name + " \n " + result.last_name/*+ " \n "
                        + result.country+ " \n "+ result.city+ " \n "+ result.*/);

            } catch (JSONException ex) {
                // Oops
            }/*
            String id = jsonParser.getJSONObject("responce").getString("id");
            String first_name = jsonParser.getJSONObject("responce").getString("first_name");
            String last_name = jsonParser.getJSONObject("responce").getString("last_name");
            String country = jsonParser.getJSONObject("responce").getJSONObject("country").getString("title");
            String city = jsonParser.getJSONObject("responce").getString("city");
            String mobile = jsonParser.getJSONObject("responce").getString("mobile_phone");
            System.out.println("info: " + id + first_name + " \n " + last_name+ " \n " + country+ " \n "+ city+ " \n "+ mobile);*/
        });
    }
}

