class Friend {
    int ownerID;
    int friendID;
    String friendName;
    String friendSurname;

    Friend(int ownerID, int friendID, String friendName, String friendSurname) {
        this.ownerID = ownerID;
        this.friendID = friendID;
        this.friendName = friendName;
        this.friendSurname = friendSurname;
    }

    Friend getValues() {
        return this;
    }
}
