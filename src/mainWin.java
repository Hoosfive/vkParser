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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.json.*;

public class mainWin extends JFrame{

    private Vector data = new Vector();
    private String userID;
    private String jsonResult;
    private VkApiClient vk;
    private JPanel panel1;
    private JTextField useridField;
    private JButton тыкниЁптаButton;
    private JTable resultTable;
    private JButton writeButton;
    private URL query;
    private HttpURLConnection connection;
    private JSONObject jsonParser;
    private int id;
    private String first_name;
    private String last_name;
    private String country;
    private String city;
    private String mobile;
    private String connectionUrl = "jdbc:sqlserver://SOMEDEVICE\\MSSQLSERVERTRUE;databaseName=usersInfo;integratedSecurity=true;";
    UserActor actor = new UserActor(155549438, "debc7d22c13b11dc31d1475f1bcbc6926033f32ca19f795f3c77cc0b7fc503fd64694df2b08678c1f84cb");
    private Statement statement = null;
    private Connection connect;
    private DefaultTableModel model = new DefaultTableModel();
    private Vector columnNames = new Vector();
    private ResultSet resultSet;
    private String idStr;






    mainWin() {
        this.getContentPane().add(panel1);
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        for (int i = 1; i <= 6; i++) {
            columnNames.addElement(i);
            model.addColumn(i);
        }


        тыкниЁптаButton.addActionListener(e -> {
            userID = useridField.getText();
            first_name = null;
            last_name = null;
            country = null;
            city = null;
            mobile = null;
            idStr = null;
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
                System.out.println(jsonResult);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            jsonParser = new JSONObject(jsonResult);
// Достаём firstName and lastName 147753101

            JSONArray jsonArr = jsonParser.getJSONArray("response");

            try {
                id = (jsonArr.getJSONObject(0).getInt("id"));
                idStr = (""+id+"");
                System.out.println(idStr);
                first_name = jsonArr.getJSONObject(0).getString("first_name");
                last_name = jsonArr.getJSONObject(0).getString("last_name");
                try {
                    city = jsonArr.getJSONObject(0  ).getJSONObject("city").getString("title");
                } catch (JSONException ex) {

                }
                try {
                    country = jsonArr.getJSONObject(0  ).getJSONObject("country").getString("title");
                } catch (JSONException ex) {

                }
                try {
                    mobile = jsonArr.getJSONObject(0).getString("mobile_phone");
                } catch (JSONException ex) {

                }
                System.out.println("info: " + id + "\n" +  first_name + " \n " + last_name+ " \n "
                        + city + " \n "+ country + " \n "+ mobile);
                fillTable();
            } catch (JSONException ex) {
                System.out.println("info: " + id + "\n" +  first_name + " \n " + last_name+ " \n "
                        + city + " \n "+ country + " \n "+ mobile);
                fillTable();
            }
        });

        writeButton.addActionListener(e -> {
            try {
                // Подключение к базе данных
                String querySql = ("insert into records (userName,userSurname,userCity,userPhone,vkID,userCountry) values " +
                        "("+"'"+first_name+"'"+","+"'"+last_name+"'"+","+"'"+city+"'"+","+"'"+mobile+"'"+","+"'"+idStr+"'"+","+"'"+country+"'"+");");
                connectDB();
                System.out.println(querySql);
                resultSet = statement.executeQuery(querySql);
                System.out.println(querySql);
                System.out.println(resultSet);
                disconnectDB();
            } catch (Exception r) {
                System.out.println(r);
                System.out.println(resultSet);

            }
        });
    }


    private void fillTable()
    {
        Vector row = new Vector(6);
        row.addElement(idStr);
        row.addElement(first_name);
        row.addElement(last_name);
        row.addElement(city);
        row.addElement(country);
        row.addElement(mobile);
        data.addElement(row);
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
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

