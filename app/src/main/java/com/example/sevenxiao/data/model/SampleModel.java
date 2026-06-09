package com.example.sevenxiao.data.model;

public class SampleModel {
    private String sampleId;
    private String title;
    private String type;          // "image" or "video"
    private String category;      // 缺陷维度
    private String fileName;
    private String description;
    private String storageUrl;
    private String localAsset;    // assets 路径
    private String difficulty;    // "basic" or "advanced"
    private String[] tags;

    public SampleModel() { }

    public SampleModel(String sampleId, String title, String type, String category,
                       String fileName, String description, String localAsset) {
        this.sampleId = sampleId;
        this.title = title;
        this.type = type;
        this.category = category;
        this.fileName = fileName;
        this.description = description;
        this.localAsset = localAsset;
        this.difficulty = "basic";
    }

    public String getSampleId() { return sampleId; }
    public void setSampleId(String sampleId) { this.sampleId = sampleId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStorageUrl() { return storageUrl; }
    public void setStorageUrl(String storageUrl) { this.storageUrl = storageUrl; }
    public String getLocalAsset() { return localAsset; }
    public void setLocalAsset(String localAsset) { this.localAsset = localAsset; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
}
