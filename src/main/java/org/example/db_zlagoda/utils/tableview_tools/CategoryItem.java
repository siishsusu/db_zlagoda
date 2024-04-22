package org.example.db_zlagoda.utils.tableview_tools;

public class CategoryItem{

    private String name;
    private int id;

    public CategoryItem(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof CategoryItem otherPI)) return false;
        return this.id == otherPI.getId();
    }

    @Override
    public String toString(){
        return name;
    }
}
