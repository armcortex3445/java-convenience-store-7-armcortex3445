package store.product;

import store.utils.ExceptionFactory;
import store.utils.ExceptionType;

public class PurchaseRequest {
    private String productName;
    private int countPurchased;
    public PurchaseRequest(){

    }

    public PurchaseRequest(String productName, int countPurchased){
        this.countPurchased = countPurchased;
        this.productName =productName;
    }

    public String getProductName(){
        return this.productName;
    }
    public int getCountPurchased(){
        return this.countPurchased;
    }

    public void decreaseCount(int value){
        if(value > this.countPurchased){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        this.countPurchased -=value;
    }

    public boolean isCountMet(){
        int zero = 0;
        return this.countPurchased == zero;
    }
}
