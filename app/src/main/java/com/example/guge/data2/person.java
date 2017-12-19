package com.example.guge.data2;

/**
 * Created by GUGE on 2017/12/14.
 */

public class person {
    private String name;
    private String bir;
    private String gift;
    public person(){

    }
    public person(String name,String bir,String gift){
        this.name = name;
        this.bir = bir;
        this.gift = gift;
    }
    public String getName(){
        return name;
    }
    public String getBir(){
        return bir;
    }
    public String getGift(){
        return gift;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setBir(String bir){
        this.bir = bir;
    }
    public void setGift(String gift){
        this.gift = gift;
    }
}
