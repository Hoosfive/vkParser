

import javax.swing.*;

public class vkParser extends JFrame {
    public static void main(String[] arg)
    {
        mainWin win = new mainWin();
        win.pack();
        win.setSize(1000,800);
        win.setLocationRelativeTo(null);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setTitle("VK PARSER");
        win.setVisible(true);
    }
}
