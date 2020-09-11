class Group {
    int ownerID;
    int groupID;
    String groupName;

    Group(int ownerID, int groupID, String groupName) {
        this.ownerID = ownerID;
        this.groupID = groupID;
        this.groupName = groupName;
    }

    Group getValues() {
        return this;
    }
}
