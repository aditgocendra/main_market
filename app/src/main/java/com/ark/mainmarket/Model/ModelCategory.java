package com.ark.mainmarket.Model;

public class ModelCategory {

    private String name_category;
    private String icon_link;
    private String key;

    public ModelCategory(){

    }

    public ModelCategory(String name_category, String icon_link) {
        this.name_category = name_category;
        this.icon_link = icon_link;
    }

    public String getName_category() {
        return name_category;
    }

    public void setName_category(String name_category) {
        this.name_category = name_category;
    }

    public String getIcon_link() {
        return icon_link;
    }

    public void setIcon_link(String icon_link) {
        this.icon_link = icon_link;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return name_category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelCategory that = (ModelCategory) o;
        return key.equals(that.key);
    }


}
