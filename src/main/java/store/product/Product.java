package store.product;

import java.time.LocalDateTime;
import java.util.Date;

public class Product {

    private String name;
    private int price;
    private Promotion promotion = null;
    int count;

    public  Product(){

    }

    public Product(Product src){
        this.name = src.name;
        this.price = src.price;
        this.promotion = src.promotion;
        this.count = src.count;
    }

    public Product(String name, int price, int count){
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public String getName(){
        return name;
    }

    public int getCount(){
        return count;
    }

    public void setPromotion(String name, LocalDateTime start , LocalDateTime end, int conditionCount){
        if(this.promotion != null){
            throw new IllegalStateException("Promotion is already exist.");
        }
        this.promotion = new Promotion(name,start,end,conditionCount);
    }

    public boolean isPromotionActive(LocalDateTime today){
        if(this.promotion == null){
            return false;
        }
        return this.promotion.isActive(today);
    }

    public boolean isBuyEnable(){
        int zero = 0;
        return this.count > zero;
    }


}
