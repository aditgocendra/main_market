package com.ark.mainmarket.Model;

public class ModelProduct {

    private String item_name;
    private String price_normal;
    private String price_whole;
    private String description;
    private String url_image;
    private String key;

    public ModelProduct(){

    }

    public ModelProduct(String item_name, String price_normal, String price_whole, String description, String url_image) {
        this.item_name = item_name;
        this.price_normal = price_normal;
        this.price_whole = price_whole;
        this.description = description;
        this.url_image = url_image;
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

    public String getPrice_whole() {
        return price_whole;
    }

    public void setPrice_whole(String price_whole) {
        this.price_whole = price_whole;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
