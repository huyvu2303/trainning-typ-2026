package com.courseshop.courseshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Todo {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String name;
    boolean isCompleted;

    
    public Todo(){}
    public Todo(String name, boolean isCompleted) {
        this.name = name;
        this.isCompleted = isCompleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    @Override
    public String toString() {
        return "Todo [id=" + id + ", name=" + name + ", isCompleted=" + isCompleted + "]";
    }
    
    
}
