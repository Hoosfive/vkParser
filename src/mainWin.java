import com.vk.api.sdk.client.actors.UserActor;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class mainWin extends JFrame{

    private int userID;
    private String jsonResult;
    private JPanel panel1;
    private JTextField userIdField;
    private JButton getMainInfoButton;
    private JTable resultTable;
    private JButton writeButton;
    private JButton testFriendsButton;
    private JButton testVideosButton;
    private JButton changeTokenButton;
    private JButton testGroupsButton;
    private URL query;
    private HttpURLConnection connection;
    private JSONObject jsonParser;
    private String first_name;
    private String last_name;
    private String country;
    private String city;
    private String mobile;
    private String photo;
    private String connectionUrl = "jdbc:sqlserver://SOMEDEVICE;databaseName=usersInfo;integratedSecurity=true;";
    String querySql;
    // ************************* Впиши после запятой ниже токен, либо потом нажми в проге кнопку changeToken
    UserActor actor = new UserActor(, "");

    private Statement statement = null;
    private Connection connect;
    private DefaultTableModel model = new DefaultTableModel();
    private String[] columnNames = {"ID", "Имя", "Фамилия", "Страна", "Город", "Телефон", "Фото"};
    private Vector<String> queriesList = new Vector();
    private Vector<String> friendsList = new Vector();
    private Vector<String> videosList = new Vector();
    private Vector<String> groupsList = new Vector();



    mainWin() {
        this.getContentPane().add(panel1);
        model.setColumnIdentifiers(columnNames);
        resultTable.setModel(model);
        getMainInfoButton.addActionListener(e -> getMainInfo());
        writeButton.addActionListener(e -> {
            // Подключение к базе данных
            connectDB();
            queriesList.forEach(this::dbQueriesExecute);
            friendsList.forEach(this::dbQueriesExecute);
            videosList.forEach(this::dbQueriesExecute);
            groupsList.forEach(this::dbQueriesExecute);
            disconnectDB();
            queriesList.clear();
            friendsList.clear();
            videosList.clear();
            groupsList.clear();
            model.addRow(new String[]{"All rows before", "has been written!"});
        });
        testFriendsButton.addActionListener(e -> parseFriends());
        testVideosButton.addActionListener(e -> parseVideos());
        changeTokenButton.addActionListener(e -> changeToken("Enter token"));
        testGroupsButton.addActionListener(e -> parseGroups());
    }

    private void getMainInfo() {
        userID = Integer.parseInt(userIdField.getText());
        String mainInfoRequest = ("https://api.vk.com/method/users.get?user_id="+userID +"&fields=country,city,contacts,photo_200&access_token="+actor.getAccessToken() + "&v=5.103 ");
        getResponse(mainInfoRequest);
        try {
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
            querySql = ("insert into records (userID,userName,userSurname,userCountry,userCity,userPhone,photoLink) values " +
                    "("+"'"+userID+"'"+","+"'"+first_name+"'"+","+"'"+last_name+"'"+","+"'"+country+"'"+","+"'"+city+"'"+","+"'"+mobile+"'"+","+"'"+photo+"'"+");");
            queriesList.addElement(querySql);
        } catch (JSONException je)
        {
            je.printStackTrace();
            changeToken("Something wrong with token");
        }
    }

    private void dbQueriesExecute(String element)
    {
        try {
            statement.executeUpdate(element);
            System.out.println(element);
        } catch (SQLException e1) {
            e1.printStackTrace();
            System.out.println("write in db exception");
        }
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
        row.addElement(String.valueOf(userID));
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
        int friendID;
        String friendName;
        String friendSurname;
        System.out.println((new JSONObject(jsonResult)).getJSONObject("response").getInt("count"));
        while(counter < (new JSONObject(jsonResult)).getJSONObject("response").getInt("count"))
        {
            jsonParser = (new JSONObject(jsonResult)).getJSONObject("response").getJSONArray("items").getJSONObject(counter);
            friendID = jsonParser.getInt("id");
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
        int offset = 0;
        AtomicInteger counter = new AtomicInteger();
        int total;
        Vector jsonStrings = new Vector();
        String videosRequest = ("https://api.vk.com/method/video.get?owner_id="+userID +"&count=200&offset="+ offset +"&access_token="+actor.getAccessToken() + "&v=5.103 ");
        getResponse(videosRequest);
        total = (new JSONObject(jsonResult)).getJSONObject("response").getInt("count");
        while (offset < total)
        {
            jsonStrings.addElement(jsonResult);
            getResponse(videosRequest);
            offset+=200;
        }
        jsonStrings.forEach(element ->
                {
                    try {
                        counter.set(0);
                        while(counter.get() < 199  && counter.get() < total)
                        {
                            jsonParser = (new JSONObject(element.toString())).getJSONObject("response").getJSONArray("items").getJSONObject(counter.get());
                            querySql = ("insert into videosRecords (ownerID,mainOwnerID,videoID,videoName,videoLink) values " +
                                    "("+"'"+userID+"'"+","+"'"+jsonParser.getInt("owner_id")+"'"+","+"'"+ jsonParser.getInt("id") +"'"+
                                    ","+"'"+jsonParser.getString("title") + " " + checkIsNull("player",null) +"'"+");");
                            videosList.addElement(querySql);
                            counter.getAndIncrement();
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                        System.out.println("counter " + counter.get());
                    }
                }

        );
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseGroups()
    {
        String groupsRequest = ("https://api.vk.com/method/groups.get?user_id="+userID +"&extended=1&access_token="+actor.getAccessToken() + "&v=5.103 ");
        getResponse(groupsRequest);
        int counter = 0, groupID;
        String groupName;
        System.out.println((new JSONObject(jsonResult)).getJSONObject("response").getInt("count"));
        while(counter < (new JSONObject(jsonResult)).getJSONObject("response").getInt("count"))
        {
            jsonParser = (new JSONObject(jsonResult)).getJSONObject("response").getJSONArray("items").getJSONObject(counter);
            groupID = jsonParser.getInt("id");
            groupName = jsonParser.getString("name");
            querySql = ("insert into groupsRecords (ownerID,groupID,groupName) values " +
                    "("+"'"+userID+"'"+","+"'"+groupID+"'"+","+"'"+groupName+"'"+");");
            groupsList.addElement(querySql);
            counter++;
        }
        groupsList.forEach(System.out::println);
    }
    private void changeToken(String title)
    {
        String token = JOptionPane.showInputDialog(null,
                new String[]{"Enter your access token:", "You can get it on vkhost.github.io"},
                title,
                JOptionPane.WARNING_MESSAGE);
        actor = new UserActor(actor.getId(),token);
    }
}

