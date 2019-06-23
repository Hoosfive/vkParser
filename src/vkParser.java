import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import javax.swing.*;

public class vkParser extends JFrame {
    public static void main(String[] arg)
    {
        mainWin win = new mainWin();
        win.pack();
        win.setSize(800,600);
        win.setVisible(true);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
