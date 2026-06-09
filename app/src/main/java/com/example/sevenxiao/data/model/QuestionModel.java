package com.example.sevenxiao.data.model;

public class QuestionModel {
    private String id;
    private String type;           // "choice" or "judge"
    private String category;       // 关联缺陷维度
    private String difficulty;     // "basic" or "advanced"
    private String imageAsset;     // assets 路径
    private String questionText;   // 题干
    private String[] options;      // 选项
    private String correctAnswer;  // 正确答案
    private String explanation;    // 解析

    public QuestionModel() { }

    public QuestionModel(String id, String type, String category, String difficulty,
                         String imageAsset, String questionText,
                         String[] options, String correctAnswer, String explanation) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.difficulty = difficulty;
        this.imageAsset = imageAsset;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getImageAsset() { return imageAsset; }
    public void setImageAsset(String imageAsset) { this.imageAsset = imageAsset; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
