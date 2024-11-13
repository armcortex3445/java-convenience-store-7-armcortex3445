package store.product;

import store.product.promotion.PromotionState;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;
import store.utils.Validator;

public class PurchaseRequest {
    private String productName;
    private int countPurchased;
    private PromotionState promotionState;
    public PurchaseRequest(){

    }

    public PurchaseRequest(String productName, int countPurchased, PromotionState promotionState){
        this.countPurchased = countPurchased;
        this.productName = productName;
        this.promotionState = promotionState;
    }

    public String getProductName(){
        return this.productName;
    }
    public int getCountPurchased(){
        return this.countPurchased;
    }

    public PromotionState getPromotionState(){
        return this.promotionState;
    }

    public void increaseCount(int value){
        Validator.validatePositiveNumber(value);
        this.countPurchased += value;
    }

    public PurchaseRequest tryToIncrease(boolean isIncrease){
        int step = 1;
        if(isIncrease){
            this.increaseCount(step);
            return this;
        }
        return this;
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
