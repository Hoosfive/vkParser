import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class resultForm extends JFrame implements Runnable{
    private JPanel panel;
    private JTable formTable;
    private JButton saveResults;
    private String[] fieldsList = {"Фамилия акк", "Фамилия факт", "Имя акк", "Имя факт", "Отчество факт",
            "Дата рождения акк", "Дата рождения факт", "Родом из", "Фотоальбомы содержат", "Адрес акк", "Адрес факт",
            "Адрес устойчивые связи", "Учебное заведение акк", "Учебное заведение факт", "Телефон",
            "Админит группы", "Актив в группах", "Увлечения", "Обнаруженные риски", "Рассчёт степени риска",
            "Принадлежность к", "Связи в РФ", "Связи иностранные", "Тип связи подписка/дружба", "Примечание"};
    mainWin win;
    resultForm(mainWin winG)
    {
        win = winG;
        formTable.setRowHeight(22);


        saveResults.addActionListener(e -> {
            Vector<String> dataVector = new Vector();
            String querySql;
            dataVector.addElement(String.valueOf(win.userID));
            for (int i= 0; i < formTable.getRowCount();i++)
            {
                dataVector.addElement("'" + formTable.getValueAt(i,1) + "'");
            }
            String tempStr  = dataVector.toString();
            tempStr = tempStr.substring(1,tempStr.length()-1);
            querySql = ("insert into records values (" + tempStr + ");");
            System.out.println(querySql);
            win.connectDB();
            win.dbQueriesExecute(querySql);
            win.disconnectDB();
        });
    }

    @Override
    public void run() {
        this.getContentPane().add(panel);
        try {
            win.getInfoThread.run();
            win.getInfoThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object[] dataList = {win.last_name, "", win.first_name, "", "", "", win.birthday, "", win.hometown, "",
                win.country+", "+win.city, "", "", win.education, "", win.mobile, "", "", "", "", "", "", "", "", ""};
        DefaultTableModel tModel = new DefaultTableModel();
        tModel.addColumn("Поля",fieldsList);
        tModel.addColumn("Данные", dataList);
        // JComboBox<String> combo = new JComboBox<String>(new String[] { "цп", "в", "лс"});
        // DefaultCellEditor editor = new DefaultCellEditor(combo);
        //tModel.setDataVector(data,new String[]{"Поля", "Данные"});
        formTable.setModel(tModel);
        //formTable.getColumnModel().getColumn(1).setCellEditor(editor);
    }
}

