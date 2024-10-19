package com.example.sedora;

public class FAQItem {
    private String title;
    private String content;
    private boolean isExpanded;

    // Constructor vacío necesario para evitar el error
    public FAQItem() {
        // Inicializa con valores por defecto
        this.title = "";
        this.content = "";
        this.isExpanded = false;
    }

    // Constructor con parámetros
    public FAQItem(String title, String content) {
        this.title = title;
        this.content = content;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
