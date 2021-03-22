package com.example.vko9;

public class Theatre {
    private String id;
    private String name;

    public Theatre(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }


    @Override
    public String toString() {
        return name;
    }
}
