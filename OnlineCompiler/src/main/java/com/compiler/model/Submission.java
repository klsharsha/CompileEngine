package com.compiler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String language;

    // Large source code (Java, C, C++, etc.)
    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String code;

    // Program input (can be multiline)
    @Lob
    @Column(columnDefinition = "TEXT")
    private String input;

    // Program output / error logs
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String output;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ---------- CONSTRUCTORS ----------

    public Submission() {
    }
    public Submission(String language,String code,String input,String output,String status,LocalDateTime createdAt) {
    	this.language=language;
    	this.code=code;
    	this.input=input;
    	this.output=output;
    	this.status=status;
    	this.createdAt=createdAt;
    }
    // ---------- GETTERS & SETTERS ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
