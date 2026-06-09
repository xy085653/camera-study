package com.example.sevenxiao.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamResultModel {
    private String difficulty;
    private List<QuestionModel> questions;
    private Map<String, String> userAnswers;   // questionId → userAnswer
    private int correctCount;
    private int totalCount;
    private long timeSpent;     // 秒
    private long completedAt;

    public ExamResultModel() {
        this.userAnswers = new HashMap<>();
        this.questions = new ArrayList<>();
    }

    public ExamResultModel(String difficulty, List<QuestionModel> questions) {
        this.difficulty = difficulty;
        this.questions = questions;
        this.userAnswers = new HashMap<>();
        this.totalCount = questions.size();
        this.correctCount = 0;
        this.timeSpent = 0;
        this.completedAt = System.currentTimeMillis();
    }

    public void addAnswer(String questionId, String answer) {
        userAnswers.put(questionId, answer);
    }

    public boolean isCorrect(QuestionModel question) {
        String userAnswer = userAnswers.get(question.getId());
        return userAnswer != null && userAnswer.equals(question.getCorrectAnswer());
    }

    public void calculateScore() {
        correctCount = 0;
        for (QuestionModel q : questions) {
            if (isCorrect(q)) correctCount++;
        }
    }

    public int getScore() {
        if (totalCount == 0) return 0;
        return (int) Math.round((double) correctCount / totalCount * 100);
    }

    // Getters
    public String getDifficulty() { return difficulty; }
    public List<QuestionModel> getQuestions() { return questions; }
    public Map<String, String> getUserAnswers() { return userAnswers; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalCount() { return totalCount; }
    public long getTimeSpent() { return timeSpent; }
    public void setTimeSpent(long timeSpent) { this.timeSpent = timeSpent; }
    public long getCompletedAt() { return completedAt; }
}
