class Video {
    int ownerID;
    int mainOwnerID;
    int videoID;
    String videoName;
    String videoLink;

    Video(int ownerID, int mainOwnerID, int videoID, String videoName, String videoLink) {
        this.ownerID = ownerID;
        this.mainOwnerID = mainOwnerID;
        this.videoID = videoID;
        this.videoName = videoName;
        this.videoLink = videoLink;
    }

    Video getValues() {
        return this;
    }
}
