package com.example.sevenxiao.data.model;

public class KnowledgeEntry {
    private String id;
    private String title;        // 缺陷名称
    private String icon;         // 图标 emoji
    private String summary;      // 一句话概述
    private String description;  // 详细描述
    private String causes;       // 成因
    private String identification; // 识别方法
    private String solution;     // 改善建议

    public KnowledgeEntry() { }

    public KnowledgeEntry(String id, String title, String icon, String summary) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.summary = summary;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCauses() { return causes; }
    public void setCauses(String causes) { this.causes = causes; }
    public String getIdentification() { return identification; }
    public void setIdentification(String identification) { this.identification = identification; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
}
