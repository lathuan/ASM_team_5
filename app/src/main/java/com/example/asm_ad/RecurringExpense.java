package com.example.asm_ad;

public class RecurringExpense {
    private int recurringExpenseId;
    private int userId;
    private int categoryId;
    private double amount;
    private String frequency;
    private String startDate;
    private String endDate;
    private String description;

    // Constructors
    public RecurringExpense() {}

    public RecurringExpense(int recurringExpenseId, int userId, int categoryId, double amount, String frequency, String startDate, String endDate, String description) {
        this.recurringExpenseId = recurringExpenseId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    // Getters and Setters
    public int getRecurringExpenseId() {
        return recurringExpenseId;
    }

    public void setRecurringExpenseId(int recurringExpenseId) {
        this.recurringExpenseId = recurringExpenseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}