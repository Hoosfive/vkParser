import com.vk.api.sdk.client.actors.UserActor;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

public class mainWin extends JFrame{

    public int userID;
    private String jsonResult;
    private JPanel panel1;
    private JTextField userIdField;
    public JButton getMainInfoButton;
    private JTable resultTable;
    private JButton writeButton;
    private JButton testFriendsButton;
    private JButton testVideosButton;
    private JButton changeTokenButton;
    private JButton testGroupsButton;
    private URL query;
    private HttpURLConnection connection;
    private JSONObject jsonParser;
    String first_name;
    String last_name;
    String country;
    String city;
    String mobile;
    private String photo;
    String education;
    String birthday;
    String hometown;
    String canAccess;

    private String connectionUrl = "jdbc:postgresql://127.0.0.1:5432/vkParserV2";
    String querySql;
    // ************************* Впиши после запятой ниже токен, либо потом нажми в проге кнопку changeToken
    UserActor actor = new UserActor(155549438, "b91894bb6f15c4970c67fbcd7615dd395849cbbb24c2232458ca1f82217e09b55293adbcbafe909e0cc24");


    private Statement statement = null;
    private Connection connect;
    private DefaultTableModel model = new DefaultTableModel();
    private String[] columnNames = {"ID", "Имя", "Фамилия", "Страна", "Город", "Телефон","Уч. заведение",
            "Родной город", "ДР", "Фото", "Профиль", "Экстремизм"};
    private Vector<String> queriesList = new Vector();
    private Vector<String> friendsList = new Vector();
    private Vector<String> videosList = new Vector();
    private Vector<String> groupsList = new Vector();

    private getMainInfo getMInfo = new getMainInfo();
    Thread getInfoThread = new Thread(getMInfo);
    private resultForm resForm = new resultForm(this);
    private Thread resultFormThread = new Thread(resForm);
    private Callable extr;

    mainWin() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.getContentPane().add(panel1);
        model.setColumnIdentifiers(columnNames);
        resultTable.setModel(model);

        getMainInfoButton.addActionListener(e -> createResForm());
        writeButton.addActionListener(e -> {
            // Подключение к базе данных
            connectDB();
            //  queriesList.forEach(this::dbQueriesExecute);
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

    class getMainInfo implements Runnable {
        @Override
        public void run() {
            userID = Integer.parseInt(userIdField.getText());
            String mainInfoRequest = ("https://api.vk.com/method/users.get?user_id="+userID +"&fields=country,city,contacts,photo_200,education,bdate,home_town&access_token="+actor.getAccessToken() + "&v=5.103 ");
            getResponse(mainInfoRequest);
            try {
                testFriendsButton.setEnabled(true);
                testVideosButton.setEnabled(true);
                testGroupsButton.setEnabled(true);

                jsonParser = (new JSONObject(jsonResult)).getJSONArray("response").getJSONObject(0);
                mobile = checkIsNull("mobile_phone",null);
                first_name = checkIsNull("first_name",null);
                last_name = checkIsNull("last_name",null);
                photo = checkIsNull("photo_200",null);
                country = checkIsNull("title","country");
                city = checkIsNull("title","city");
                education = checkIsNull("university_name", null);
                birthday = checkIsNull("bdate",null);
                hometown = checkIsNull("home_town",null);
                canAccess = isPrivate(jsonParser.getBoolean("can_access_closed"));
                System.out.println("info: " + userID + "\n" +  first_name + " \n " + last_name+ " \n "
                        + city + " \n "+ country + " \n "+ mobile + "\n" + photo);
                fillTable();
                /*querySql = ("insert into records (userID,userName,userSurname,userCountry,userCity,userPhone,photoLink) values " +
                        "('"+userID+"','"+first_name+"','"+last_name+"','"+country+"','"+city+"','"+mobile+"','"+photo+"');");
                queriesList.addElement(querySql);*/
            } catch (JSONException je)
            {
                je.printStackTrace();
                changeToken("Something wrong with token");
            }
        }
    }

    private String isPrivate(boolean isOpen)
    {
        if (isOpen)
            return "Открыт";
        else
        {
            testFriendsButton.setEnabled(false);
            testVideosButton.setEnabled(false);
            testGroupsButton.setEnabled(false);
            return "Закрыт";
        }

    }
    public void dbQueriesExecute(String element)
    {
        try {
            statement.executeUpdate(element);
            System.out.println(element);
        } catch (SQLException e1) {
            System.out.println("write in db exception  "  +  e1.getErrorCode() + " ***  " + e1.getLocalizedMessage());
            e1.printStackTrace();
            if (e1.getLocalizedMessage().contains("Не удалось установить соединение"))
            {
                element = "update" + element.substring(6);
                System.out.println("shiiit  ****   " + element);
                /*try {
                    statement.executeUpdate(element);
                } catch (SQLException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
    private String checkIsNull(String field, String parentObject)
    {
        try {
            if(parentObject != null)
                field = jsonParser.getJSONObject(parentObject).getString(field);
            else field = jsonParser.getString(field);
            if (field.length() <=1)
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
        row.addElement(education);
        row.addElement(hometown);
        row.addElement(birthday);
        row.addElement(photo);
        row.addElement(canAccess);
        model.addRow(row);
        resultTable.setModel(model);
    }
    public void connectDB()
    {
        try {
            connect = DriverManager.getConnection(connectionUrl,"postgres","bor9n");
            statement = connect.createStatement();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void disconnectDB() {
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
        try {
            System.out.println((new JSONObject(jsonResult)).getJSONObject("response").getInt("count"));
            while (counter < (new JSONObject(jsonResult)).getJSONObject("response").getInt("count")) {
                jsonParser = (new JSONObject(jsonResult)).getJSONObject("response").getJSONArray("items").getJSONObject(counter);
                friendID = jsonParser.getInt("id");
                friendName = jsonParser.getString("first_name");
                friendSurname = jsonParser.getString("last_name");
                querySql = ("insert into friendsRecords (ownerID,friendID,friendName) values " +
                        "('" + userID + "','" + friendID + "','" + friendName + " " + friendSurname + "');");
                friendsList.addElement(querySql);
                counter++;
            }
        } catch (JSONException e)
        {
            System.out.println(jsonResult);
            e.printStackTrace();
        }
        friendsList.forEach(System.out::println);
    }
    private void parseVideos()
    {
        Vector<String> nameList = new Vector();
        Vector<String> extremismList = new Vector<>();
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
                                    "('"+userID+"','"+jsonParser.getInt("owner_id")+"','"+ jsonParser.getInt("id") +"'"+
                                    ",'"+jsonParser.getString("title") + "','" + checkIsNull("player",null) +"');");
                            videosList.addElement(querySql);
                            nameList.addElement(jsonParser.getString("title"));
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
        for (String el:nameList) {
            extr = new extremismChecker("«"+el+"»");
            FutureTask<String> future = new FutureTask<>(extr);
            new Thread(future).start();
            try {
                //if(future.get() != "-1")
                System.out.println(el + " ////  " + future.get());
                if (!future.get().equals("-1"))
                    extremismList.addElement(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (extremismList.isEmpty())
            resultTable.setValueAt("Не найдено",resultTable.getRowCount()-1,resultTable.getColumnCount()-1);
        else
            resultTable.setValueAt(extremismList,resultTable.getRowCount()-1,resultTable.getColumnCount()-1);
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
    private void createResForm()
    {

        resForm.setLocationRelativeTo(null);
        resForm.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
            }
        });
        resForm.setTitle("Result Form");

        resultFormThread.run();
        resForm.pack();
        resForm.setSize(500,700);
        resForm.setVisible(true);
        try {
            resultFormThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
