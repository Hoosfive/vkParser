import java.util.Vector;

public class userObjects {
    public class MainUser
    {
        int userID;
        String last_nameVK;
        String last_name;
        String first_nameVK;
        String first_name;
        String patron;
        Vector accounts;
        String birthdate;
        String birthdateReal;
        String city;
        String mobile;
        String photo;
    }

    String[] fieldsList = {"Фамилия акк", "Фамилия факт", "Имя акк", "Имя факт", "Отчество факт", "Акки выявл",
            "Дата рождения акк", "Дата рождения факт", "Родом из", "Фотоальбомы содержат", "Адрес акк", "Адрес факт",
            "Адрес устойчивые связи", "Учебное заведение акк", "Учебное заведение факт", "Телефон", "Входит в группы",
            "Админит группы", "Актив в группах", "Увлечения", "Обнаруженные риски", "Рассчёт степени риска",
            "Принадлежность к", "Связи в РФ", "Связи иностранные", "Тип связи подписка/дружба", "Лучшие друзья",
            "Родственники", "Примечание"};
    /*public class Video
    {
        String videoID;
        String videoName;
        int mainOwnerID;
        String videoLink;
    }
    public class Group
    {
        int groupID;
        String groupName;
    }
    public class Friend
    {
        int friendID;
        String friendName;
        String friendSurname;
    }*/
}
