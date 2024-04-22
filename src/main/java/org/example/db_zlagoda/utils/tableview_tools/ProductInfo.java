package org.example.db_zlagoda.utils.tableview_tools;

public class ProductInfo{

    private String category;
    private int id;
    private String description;
    public String name;
    public ProductInfo(int id, String category, String name, String description) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ProductInfo otherPI)) return false;
        return this.id == ((ProductInfo) other).getId();
    }
}
