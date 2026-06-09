package com.example.sevenxiao.data.model;

public class UserModel {
    private String uid;
    private String email;
    private String displayName;
    private String avatarUrl;
    private long createdAt;
    private double totalScore;
    private int totalExams;
    private int totalQuestions;
    private double avgAccuracy;
    private int rank;

    public UserModel() { }

    public UserModel(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = System.currentTimeMillis();
        this.totalScore = 0;
        this.totalExams = 0;
        this.totalQuestions = 0;
        this.avgAccuracy = 0;
        this.rank = 0;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }
    public int getTotalExams() { return totalExams; }
    public void setTotalExams(int totalExams) { this.totalExams = totalExams; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public double getAvgAccuracy() { return avgAccuracy; }
    public void setAvgAccuracy(double avgAccuracy) { this.avgAccuracy = avgAccuracy; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}
