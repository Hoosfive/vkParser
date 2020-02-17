

import javax.swing.*;

public class vkParser extends JFrame {
    public static void main(String[] arg)
    {
        mainWin win = new mainWin();
        win.pack();
        win.setSize(1000,800);
        win.setVisible(true);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setTitle("VK PARSER");
        /*win.setBackground(Color.orange);
        ImageIcon icon = new ImageIcon("D:\\JetBrains\\IntelliJ IDEA Community Edition 2018.3\\myProjects\\vkParser\\src\\tapimage.png");
        win.setIconImage(icon.getImage());*/
    }
}
