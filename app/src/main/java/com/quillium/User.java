//package com.quillium;
//
//public class User {
//    private String fullname;
//    private String email;
//    private String userId;
//    private String dob; // Date of Birth
//    private int followerCount;
//
//    public int getFollowerCount() {
//        return followerCount;
//    }
//
//    public void setFollowerCount(int followerCount) {
//        this.followerCount = followerCount;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    private String coverPhotoUrl;
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getCoverPhotoUrl() {
//        return coverPhotoUrl;
//    }
//
//    public void setCoverPhotoUrl(String coverPhotoUrl) {
//        this.coverPhotoUrl = coverPhotoUrl;
//    }
//
//    public String getProfilePhotoUrl() {
//        return profilePhotoUrl;
//    }
//
//    public void setProfilePhotoUrl(String profilePhotoUrl) {
//        this.profilePhotoUrl = profilePhotoUrl;
//    }
//
//    private String profilePhotoUrl;
//
//    public User() {
//        // Default constructor required for calls to DataSnapshot.getValue(User.class)
//    }
//
//    public User(String fullname, String email, String dob) {
//        this.fullname = fullname;
//        this.email = email;
//        this.dob = dob;
//    }
//
//    public String getFullname() {
//        return fullname;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getDob() {
//        return dob;
//    }
//}
//
package com.quillium;

public class User {
    private String fullname;
    private String email;
    private String studentId;
    private String department;
    private String userId;
    private String dob; // Date of Birth
    private int followerCount;
    private String coverPhotoUrl;
    private String profilePhotoUrl;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String fullname, String email, String studentId, String department) {
        this.fullname = fullname;
        this.email = email;
        this.studentId = studentId;
        this.department = department;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
