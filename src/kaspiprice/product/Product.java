/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kaspiprice.product;

import javafx.application.HostServices;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;

/**
 *
 * @author zhans
 */
public class Product {

    private HostServices hostServices;
    private String name;
    private int id;
    private int cost;
    private CheckBox checkbox;
    private int number;
    private Hyperlink link=new Hyperlink();
    
    

    public Product( String name, int id, int cost, int number, HostServices hostServices) {
        this.name= name;
        this.id = id;
        this.cost = cost;
        this.checkbox = new CheckBox();
        this.number=number;
        this.link.setText("на Kaspi");
        this.link.setOnAction(e ->{
            hostServices.showDocument("https://kaspi.kz/shop/search/?text="+name);
        }
        );
    }

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = this.cost+cost;
    }
    
     public CheckBox getCheckbox() {
        return checkbox;
    }
 
    public void setCheckBox() {
        this.checkbox.setSelected(!checkbox.isSelected());
    }
    public void setCheckBoxTrue(){
        this.checkbox.setSelected(true);
    }
    
    public void setCheckBoxFalse(){
        this.checkbox.setSelected(false);
    }
    
    public int getNumber(){
        return number;
    }
    
    public void setNumber(int number){
        this.number=number;
    }
    public Hyperlink getLink(){
        return link;
    }
    public void setLink(Hyperlink link){
        this.link=link;
    }
    
    
    
}

