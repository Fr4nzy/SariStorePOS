package com.projectfkklp.saristorepos.models;

import com.projectfkklp.saristorepos.utils.ModelUtils;

public class Store {
    private String id;
    private String name;

    public Store(){
        id = ModelUtils.createShortId();
    }

    public Store(String id, String name){
        this.id = id;
        this.name = name;
    }

    // Getter and Setter methods for 'id'
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter methods for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
