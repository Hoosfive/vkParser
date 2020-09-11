import java.util.Vector;

class UserAllData {
    User userMainData;
    Vector<Friend> friends;
    Vector<Video> videos;
    Vector<Group> groups;

    public UserAllData(User mainData, Vector<Video> videos, Vector<Friend> friends, Vector<Group> groups) {
        this.userMainData = mainData;
        this.videos = videos;
        this.friends = friends;
        this.groups = groups;
    }


    public UserAllData getValues() {
        return this;
    }

    public void clearLists() {
        friends.clear();
        videos.clear();
        groups.clear();
    }
}
