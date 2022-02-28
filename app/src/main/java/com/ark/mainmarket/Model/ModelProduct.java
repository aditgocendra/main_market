package com.ark.mainmarket.Model;

public class ModelProduct {

    private String item_name;
    private String price_normal;
    private String category;
    private String condition;
    private String stock;
    private String description;
    private String url_image;
    private String color_background;
    private boolean free_sending;
    private boolean wholesale;
    private String price_wholesale;
    private String min_buy_wholesale;
    private String disc;
    private String key;

    public ModelProduct(){

    }

    public ModelProduct(String item_name, String price_normal, String category, String condition, String stock, String description, String url_image, String color_background, boolean free_sending, boolean wholesale, String price_wholesale, String min_buy_wholesale, String disc) {
        this.item_name = item_name;
        this.price_normal = price_normal;
        this.category = category;
        this.condition = condition;
        this.stock = stock;
        this.description = description;
        this.url_image = url_image;
        this.color_background = color_background;
        this.free_sending = free_sending;
        this.wholesale = wholesale;
        this.price_wholesale = price_wholesale;
        this.min_buy_wholesale = min_buy_wholesale;
        this.disc = disc;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getPrice_normal() {
        return price_normal;
    }

    public void setPrice_normal(String price_normal) {
        this.price_normal = price_normal;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getColor_background() {
        return color_background;
    }

    public void setColor_background(String color_background) {
        this.color_background = color_background;
    }

    public boolean isFree_sending() {
        return free_sending;
    }

    public void setFree_sending(boolean free_sending) {
        this.free_sending = free_sending;
    }

    public boolean isWholesale() {
        return wholesale;
    }

    public void setWholesale(boolean wholesale) {
        this.wholesale = wholesale;
    }

    public String getPrice_wholesale() {
        return price_wholesale;
    }

    public void setPrice_wholesale(String price_wholesale) {
        this.price_wholesale = price_wholesale;
    }

    public String getMin_buy_wholesale() {
        return min_buy_wholesale;
    }

    public void setMin_buy_wholesale(String min_buy_wholesale) {
        this.min_buy_wholesale = min_buy_wholesale;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
