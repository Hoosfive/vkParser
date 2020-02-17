import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.video.Video;
import com.vk.api.sdk.objects.video.responses.GetResponse;
import com.vk.api.sdk.queries.video.VideoGetQuery;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

public class mainWin extends JFrame{

    private String userID;
    private String jsonResult;
    private VkApiClient vk;
    private JPanel panel1;
    private JTextField userIdField;
    private JButton clickMeButton;
    private JTable resultTable;
    private JButton writeButton;
    private JButton testFriendsButton;
    private JButton testVideosButton;
    private JButton changeTokenButton;
    private URL query;
    private HttpURLConnection connection;
    private JSONObject jsonParser;
    private String first_name;
    private String last_name;
    private String country;
    private String city;
    private String mobile;
    private String photo;
    private String connectionUrl = "jdbc:sqlserver://SOMEDEVICE\\MSSQLSERVERTRUE;databaseName=usersInfo;integratedSecurity=true;";
    String querySql;
    // ************************* Впиши после запятой ниже токен, либо потом нажми в проге кнопку changeToken
    UserActor actor = new UserActor(155549438, "cbd1d1ec76a8bb70ad41de7e6a7f929a671b8792a88cca2de09d698411b1c1bc9465a198c4470e8738797");

    private Statement statement = null;
    private Connection connect;
    private DefaultTableModel model = new DefaultTableModel();
    private String[] columnNames = {"ID", "Имя", "Фамилия", "Страна", "Город", "Телефон", "Фото"};
    private Vector<String> queriesList = new Vector();
    private Vector<String> friendsList = new Vector();
    private Vector<String> videosList = new Vector();



    mainWin() {
        this.getContentPane().add(panel1);
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        model.setColumnIdentifiers(columnNames);
        resultTable.setModel(model);
        VideoGetQuery resp = new VideoGetQuery(vk,actor);
        resp = vk.videos().get(actor).count(5);
        clickMeButton.addActionListener(e -> {
            userID = userIdField.getText();
            first_name = null;
            last_name = null;
            country = null;
            city = null;
            mobile = null;
            String url = ("https://api.vk.com/method/users.get?user_id="+userID +"&fields=country,city,contacts,photo_200&access_token="+actor.getAccessToken() + "&v=5.103 ");
            getResponse(url);
            jsonParser = (new JSONObject(jsonResult)).getJSONArray("response").getJSONObject(0);

            mobile = checkIsNull("mobile_phone",null);
            first_name = checkIsNull("first_name",null);
            last_name = checkIsNull("last_name",null);
            photo = checkIsNull("photo_200",null);
            country = checkIsNull("title","country");
            city = checkIsNull("title","city");
            System.out.println("info: " + userID + "\n" +  first_name + " \n " + last_name+ " \n "
                    + city + " \n "+ country + " \n "+ mobile + "\n" + photo);
            fillTable();
            querySql = ("insert into records (userName,userSurname,userCity,userPhone,vkID,userCountry,photoLink) values " +
                    "("+"'"+first_name+"'"+","+"'"+last_name+"'"+","+"'"+city+"'"+","+"'"+mobile+"'"+","+"'"+userID+"'"+","+"'"+country+"'"+","+"'"+photo+"'"+");");
            queriesList.addElement(querySql);
        });

        writeButton.addActionListener(e -> {
            // Подключение к базе данных
            connectDB();
            queriesList.forEach((element) -> {
                try {
                    statement.executeUpdate(element);
                    System.out.println(element);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    System.out.println("write main info in db exception");
                }
            });
            friendsList.forEach((element) -> {
                try {
                    statement.executeUpdate(element);
                    System.out.println(element);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    System.out.println("write friends  in db exception");
                }
            });
            videosList.forEach((element) -> {
                try {
                    statement.executeUpdate(element);
                    System.out.println(element);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    System.out.println("write videos in db exception");
                }
            });
            disconnectDB();
            queriesList.clear();
            friendsList.clear();
            videosList.clear();
            model.addRow(new String[]{"All rows before", "has been writed!"});

        });
        testFriendsButton.addActionListener(e -> parseFriends());
        testVideosButton.addActionListener(e -> parseVideos());
        changeTokenButton.addActionListener(e -> changeToken());
    }

    private String checkIsNull(String field, String parentObject)
    {
        try {
            if(parentObject != null)
                field = jsonParser.getJSONObject(parentObject).getString(field);
            else field = jsonParser.getString(field);
            if (field.isBlank())
                field = "нет данных";
        }   catch (JSONException ex) {
            field = "нет данных";
        }
        return field;
    }

    private void fillTable()
    {
        Vector<String> row = new Vector<>(7);
        row.addElement(userID);
        row.addElement(first_name);
        row.addElement(last_name);
        row.addElement(country);
        row.addElement(city);
        row.addElement(mobile);
        row.addElement(photo);
        model.addRow(row);
        resultTable.setModel(model);
    }
    private void connectDB()
    {
        try {
            connect = DriverManager.getConnection(connectionUrl);
            statement = connect.createStatement();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private void disconnectDB() {
        try {
            statement.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private void parseFriends()
    {
        String friendsRequest = ("https://api.vk.com/method/friends.get?user_id="+userID +"&order=name&count=10000&fields=first_name&access_token="+actor.getAccessToken() + "&v=5.103 ");
        getResponse(friendsRequest);
        int counter = 0;
        String friendID,friendName,friendSurname;
        System.out.println((new JSONObject(jsonResult)).getJSONObject("response").getInt("count"));
        while(counter < (new JSONObject(jsonResult)).getJSONObject("response").getInt("count"))
        {
            jsonParser = (new JSONObject(jsonResult)).getJSONObject("response").getJSONArray("items").getJSONObject(counter);
            friendID = String.valueOf(jsonParser.getInt("id"));
            friendName = jsonParser.getString("first_name");
            friendSurname = jsonParser.getString("last_name");
            querySql = ("insert into friendsRecords (ownerID,friendID,friendName) values " +
                    "("+"'"+userID+"'"+","+"'"+friendID+"'"+","+"'"+friendName + " " + friendSurname +"'"+");");
            friendsList.addElement(querySql);
            counter++;
        }
        friendsList.forEach(System.out::println);
    }
    private void parseVideos()
    {
        int offset = 0,counter,total;
        String videosRequest = ("https://api.vk.com/method/video.get?owner_id="+userID +"&count=10&offset="+ offset +"&access_token="+actor.getAccessToken() + "&v=5.103 ");
        System.out.println(videosRequest);
        getResponse(videosRequest);
        total = (new JSONObject(jsonResult)).getJSONObject("response").getInt("count");
        System.out.println(total);
        //System.out.println((new JSONObject(jsonResult)).getJSONObject("response").getJSONArray("items").getJSONObject(0).getInt("id"));
        String videoID,videoName,mainOwnerID,videoLink;
        while (total > 0)
        {
            videosRequest = ("https://api.vk.com/method/video.get?owner_id="+userID +"&count=200&offset="+ offset +"&access_token="+actor.getAccessToken() + "&v=5.103 ");
            getResponse(videosRequest);
            counter = 0;
            while(counter < 200 & counter < total-1)
            {
                jsonParser = (new JSONObject(jsonResult)).getJSONObject("response").getJSONArray("items").getJSONObject(counter);
                videoID = String.valueOf(jsonParser.getInt("id"));
                videoName = jsonParser.getString("title");
                mainOwnerID = String.valueOf(jsonParser.getInt("owner_id"));
                videoLink = checkIsNull("player",null);
                querySql = ("insert into friendsRecords (ownerID,mainOwnerID,videoID,videoName,videoLink) values " +
                        "("+"'"+userID+"'"+","+"'"+mainOwnerID+"'"+","+"'"+videoID+"'"+","+"'"+videoName + " " + videoLink +"'"+");");
                videosList.addElement(querySql);
                counter++;
                offset+=200;
            }
            total-=200;
        }
        videosList.forEach(System.out::println);
    }
    private void getResponse(String url)
    {
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
            System.out.println(jsonResult);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    private void changeToken()
    {
        String token = JOptionPane.showInputDialog(null,
                new String[]{"Enter your access token:", "You can get it on vkhost.github.io"},
                "Enter token",
                JOptionPane.WARNING_MESSAGE);
        actor = new UserActor(actor.getId(),token);
    }

}

