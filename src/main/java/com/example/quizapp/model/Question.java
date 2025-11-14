package com.example.quizapp.model;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private int id;
    private String text;
    private List<String> correctAnswers; // 1. 核心改动：从 String 变成了 List<String>
    private String type; // "fixed" 或 "random"

    // 空的构造函数，有时框架需要它
    public Question() {}

    // 2. 核心改动：更新了带参数的构造函数
    public Question(int id, String text, List<String> correctAnswers, String type) {
        this.id = id;
        this.text = text;
        this.correctAnswers = correctAnswers;
        this.type = type;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getText() { return text; }
    public List<String> getCorrectAnswers() { return correctAnswers; } // 3. 核心改动：getter也变了
    public String getType() { return type; }
}
