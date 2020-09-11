import com.vk.api.sdk.client.actors.UserActor;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
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

public class mainWin extends JFrame {

    int userID;
    private JPanel panel1;
    private JTextField userIdField;
    public JButton getMainInfoButton;
    private JTable resultTable;
    private JButton writeInDBButton;
    private JButton testFriendsButton;
    private JButton testVideosButton;
    private JButton changeTokenButton;
    private JButton testGroupsButton;
    private JButton export;
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
    private String canAccess;

    // ************************* Впиши после запятой ниже токен, либо потом нажми в проге кнопку Изменить токен
    public UserActor actor = new UserActor(155549438, "b91894bb6f15c4970c67fbcd7615dd395849cbbb24c2232458ca1f82217e09b55293adbcbafe909e0cc24");


    private Statement statement = null;
    private DefaultTableModel model = new DefaultTableModel();
    private Vector<Friend> friendsList = new Vector();
    private Vector<Video> videosList = new Vector();
    private Vector<Group> groupsList = new Vector();
    private Vector<User> usersList = new Vector<>();

    private resultForm resForm = new resultForm(this);
    private Thread resultFormThread = new Thread(resForm);

    /*public getMainInfo getMInfo = new getMainInfo();
    public Thread getInfoThread = new Thread(getMInfo);*/

    mainWin() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.getContentPane().add(panel1);
        String[] columnNames = {"ID", "Имя", "Фамилия", "Страна", "Город", "Телефон", "Уч. заведение",
                "Родной город", "ДР", "Фото", "Профиль", "Экстремизм"};
        model.setColumnIdentifiers(columnNames);
        resultTable.setModel(model);

        getMainInfoButton.addActionListener(e ->
                doSomeShit());
        writeInDBButton.addActionListener(e -> {
            // Подключение к базе данных
            connectDB();
            //  queriesList.forEach(this::dbQueriesExecute);
            friendsList.forEach(this::makeFriendSql);
            videosList.forEach(this::makeVideoSql);
            groupsList.forEach(this::makeGroupSql);
            disconnectDB();
            friendsList.clear();
            videosList.clear();
            groupsList.clear();
            model.addRow(new String[]{"All rows before", "has been written!"});
        });
        testFriendsButton.addActionListener(e -> parseFriends());
        testVideosButton.addActionListener(e -> parseVideos());
        changeTokenButton.addActionListener(e -> changeToken("Enter token"));
        testGroupsButton.addActionListener(e -> parseGroups());
        export.addActionListener(e -> {
        });

    }


    private void doSomeShit() {
        userID = Integer.parseInt(userIdField.getText());
        createResForm();
    }

    public class getMainInfo implements Callable {
        @Override
        public User call() {
            userID = Integer.parseInt(userIdField.getText());
            String mainInfoRequest = ("https://api.vk.com/method/users.get?user_id=" + userID + "&fields=country,city,contacts,photo_200,education,bdate,home_town&access_token=" + actor.getAccessToken() + "&v=5.103 ");
            String jsonResult = getResponse(mainInfoRequest);
            try {
                testFriendsButton.setEnabled(true);
                testVideosButton.setEnabled(true);
                testGroupsButton.setEnabled(true);

                jsonParser = (new JSONObject(jsonResult)).getJSONArray("response").getJSONObject(0);
                mobile = checkIsNull("mobile_phone", null);
                first_name = checkIsNull("first_name", null);
                last_name = checkIsNull("last_name", null);
                photo = checkIsNull("photo_200", null);
                country = checkIsNull("title", "country");
                city = checkIsNull("title", "city");
                education = checkIsNull("university_name", null);
                birthday = checkIsNull("bdate", null);
                hometown = checkIsNull("home_town", null);
                canAccess = isPrivate(jsonParser.getBoolean("can_access_closed"));
                System.out.println("info: " + userID + "\n" + first_name + " \n " + last_name + " \n "
                        + city + " \n " + country + " \n " + mobile + "\n" + photo);
                fillTable();
                /*querySql = ("insert into records (userID,userName,userSurname,userCountry,userCity,userPhone,photoLink) values " +
                        "('"+userID+"','"+first_name+"','"+last_name+"','"+country+"','"+city+"','"+mobile+"','"+photo+"');");
                queriesList.addElement(querySql);*/
            } catch (JSONException je) {
                je.printStackTrace();
                changeToken("Something wrong with token");
            }
            return new User(userID,
                    checkIsNull("first_name", null),
                    checkIsNull("last_name", null),
                    checkIsNull("title", "country"),
                    checkIsNull("title", "city"),
                    checkIsNull("mobile_phone", null),
                    checkIsNull("photo_200", null),
                    checkIsNull("university_name", null),
                    checkIsNull("bdate", null),
                    checkIsNull("home_town", null),
                    isPrivate(jsonParser.getBoolean("can_access_closed")));
        }
    }

    private String isPrivate(boolean isOpen) {
        if (isOpen)
            return "Открыт";
        else {
            testFriendsButton.setEnabled(false);
            testVideosButton.setEnabled(false);
            testGroupsButton.setEnabled(false);
            return "Закрыт";
        }

    }

    void dbQueriesExecute(String element) {
        try {
            statement.executeUpdate(element);
            System.out.println(element);
        } catch (SQLException e1) {
            System.out.println("write in db exception  " + e1.getErrorCode() + " ***  " + e1.getLocalizedMessage());
            e1.printStackTrace();
            if (e1.getLocalizedMessage().contains("Не удалось установить соединение")) {
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

    private String checkIsNull(String field, String parentObject) {
        try {
            if (parentObject != null)
                field = jsonParser.getJSONObject(parentObject).getString(field);
            else field = jsonParser.getString(field);
            if (field.length() <= 1)
                field = "нет данных";
        } catch (JSONException ex) {
            field = "нет данных";
        }
        return field;
    }

    private void makeVideoSql(Video object) {
        Video values = object.getValues();
        dbQueriesExecute(String.format("insert into videosRecords (ownerID,mainOwnerID,videoID,videoName,videoLink) values ('%d','%d','%d','%s','%s');",
                values.ownerID, values.mainOwnerID, values.videoID, values.videoName, values.videoLink));
    }

    private void makeFriendSql(Friend object) {
        Friend values = object.getValues();
        dbQueriesExecute(String.format("insert into friendsRecords (ownerID,friendID,friendName) values ('%d','%d','%s" + "%s');",
                values.ownerID, values.friendID, values.friendName, values.friendSurname));
    }

    private void makeGroupSql(Group object) {
        Group values = object.getValues();
        dbQueriesExecute(String.format("insert into groupsRecords (ownerID,groupID,groupName) values " +
                "('%d','%d','%s');", values.ownerID, values.groupID, values.groupName));
    }


    private void fillTable() {
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

    void connectDB() {
        try {
            String connectionUrl = "jdbc:postgresql://127.0.0.1:5432/vkParserV2";
            Connection connect = DriverManager.getConnection(connectionUrl, "postgres", "bor9n");
            statement = connect.createStatement();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void disconnectDB() {
        try {
            statement.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void parseFriends() {
        String friendsRequest = ("https://api.vk.com/method/friends.get?user_id=" + userID + "&order=name&count=10000&fields=first_name&access_token=" + actor.getAccessToken() + "&v=5.103 ");
        String jsonResult = getResponse(friendsRequest);
        int counter = 0;
        try {
            System.out.println((new JSONObject(jsonResult)).getJSONObject("response").getInt("count"));
            while (counter < (new JSONObject(jsonResult)).getJSONObject("response").getInt("count")) {
                jsonParser = (new JSONObject(jsonResult)).getJSONObject("response").getJSONArray("items").getJSONObject(counter);

                friendsList.addElement(new Friend(userID,
                        jsonParser.getInt("id"),
                        jsonParser.getString("first_name"),
                        jsonParser.getString("last_name")));
                counter++;
            }
        } catch (JSONException e) {
            System.out.println(jsonResult);
            e.printStackTrace();
        }
        friendsList.forEach(System.out::println);
    }

    private void parseVideos() {
        Vector<String> nameList = new Vector();
        Vector<String> extremismList = new Vector<>();
        String jsonResult;
        int offset = 0;
        AtomicInteger counter = new AtomicInteger();
        int total;
        Vector jsonStrings = new Vector();
        String videosRequest = ("https://api.vk.com/method/video.get?owner_id=" + userID + "&count=200&offset=" + offset + "&access_token=" + actor.getAccessToken() + "&v=5.103 ");
        jsonResult = getResponse(videosRequest);
        total = (new JSONObject(jsonResult)).getJSONObject("response").getInt("count");
        while (offset < total) {
            videosRequest = ("https://api.vk.com/method/video.get?owner_id=" + userID + "&count=200&offset=" + offset + "&access_token=" + actor.getAccessToken() + "&v=5.103 ");
            jsonResult = getResponse(videosRequest);
            jsonStrings.addElement(jsonResult);
            offset += 200;
        }
        jsonStrings.forEach(element ->
                {
                    try {
                        counter.set(0);
                        while (counter.get() < 199 && counter.get() < total) {
                            jsonParser = (new JSONObject(element.toString())).getJSONObject("response").getJSONArray("items").getJSONObject(counter.get());

                            videosList.addElement(new Video(userID,
                                    jsonParser.getInt("owner_id"),
                                    jsonParser.getInt("id"),
                                    jsonParser.getString("title"),
                                    checkIsNull("player", null)));
//                            querySql.format("insert into videosRecords (ownerID,mainOwnerID,videoID,videoName,videoLink) values ('%d','%d','%d','%s','%s');",videosList.get(2));
                            nameList.addElement(jsonParser.getString("title"));
                            counter.getAndIncrement();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("counter " + counter.get());
                    }
                }
        );
//        videosList.forEach(System.out::println);
        for (String el : nameList) {
            if (!el.equals("")) {
                Callable extr = new extremismChecker("«" + el + "»");
                FutureTask<String> future = new FutureTask<>(extr);
                new Thread(future).start();
                try {
                    //if(future.get() != "-1")
                    System.out.println(el + " ////  " + future.get());
                    if (!future.get().equals("-1"))
                        extremismList.addElement(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        if (extremismList.isEmpty())
            resultTable.setValueAt("Не найдено", resultTable.getRowCount() - 1, resultTable.getColumnCount() - 1);
        else
            resultTable.setValueAt(extremismList, resultTable.getRowCount() - 1, resultTable.getColumnCount() - 1);
    }

    public String getResponse(String url) {
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

    private void parseGroups() {
        String groupsRequest = ("https://api.vk.com/method/groups.get?user_id=" + userID + "&extended=1&access_token=" + actor.getAccessToken() + "&v=5.103 ");
        String jsonResult = getResponse(groupsRequest);
        int counter = 0, groupID;
        String groupName;
        System.out.println((new JSONObject(jsonResult)).getJSONObject("response").getInt("count"));
        while (counter < (new JSONObject(jsonResult)).getJSONObject("response").getInt("count")) {
            jsonParser = (new JSONObject(jsonResult)).getJSONObject("response").getJSONArray("items").getJSONObject(counter);
            groupsList.addElement(new Group(userID, jsonParser.getInt("id"), jsonParser.getString("name")));
            counter++;
        }
        //groupsList.forEach(System.out::println);
    }

    private void changeToken(String title) {
        String token = JOptionPane.showInputDialog(null,
                new String[]{"Enter your access token:", "You can get it on vkhost.github.io"},
                title,
                JOptionPane.WARNING_MESSAGE);
        actor = new UserActor(actor.getId(), token);
    }

    private void createResForm() {
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
        resForm.setSize(500, 700);
        resForm.setVisible(true);
        try {
            resultFormThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
