package com.arabicprose.dto;

public class FrequencyDTO {
    private String item;
    private int frequency;
    private double percentage;
    
    public FrequencyDTO() {
        // Default constructor
    }
    
    public FrequencyDTO(String item, int frequency, double percentage) {
        this.item = item;
        this.frequency = frequency;
        this.percentage = percentage;
    }
    
    // Getters and Setters
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    
    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }
    
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    
    @Override
    public String toString() {
        return String.format("%s: %d (%.2f%%)", item, frequency, percentage);
    }
}