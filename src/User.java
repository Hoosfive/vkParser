class User {
    int userID;
    String first_name;
    String last_name;
    String country;
    String city;
    String mobile;
    String photo;
    String education;
    String birthday;
    String hometown;
    String canAccess;

    User(int userID,
         String first_name,
         String last_name,
         String country,
         String city,
         String mobile,
         String photo,
         String education,
         String birthday,
         String hometown,
         String canAccess) {
        this.userID = userID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.country = country;
        this.city = city;
        this.mobile = mobile;
        this.photo = photo;
        this.education = education;
        this.birthday = birthday;
        this.hometown = hometown;
        this.canAccess = canAccess;
    }
}
