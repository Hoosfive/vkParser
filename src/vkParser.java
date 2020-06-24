

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

/* TODO list: 1) Допилить вкладку Объект, добаить форму для ввода недостающей инфы
    2) Сделать многооконный режим с блэк джеком и шлюзами, т.е. а) Статистику по каждому проверенному*/