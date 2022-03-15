package com.ark.mainmarket.Model;

public class ModelShopCart {

    private String key_product;
    private String total_product;
    private String key;

    public ModelShopCart(){

    }

    public ModelShopCart(String key_product, String total_product) {
        this.key_product = key_product;
        this.total_product = total_product;
    }

    public String getKey_product() {
        return key_product;
    }

    public void setKey_product(String key_product) {
        this.key_product = key_product;
    }

    public String getTotal_product() {
        return total_product;
    }

    public void setTotal_product(String total_product) {
        this.total_product = total_product;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
