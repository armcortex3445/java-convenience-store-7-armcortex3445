package store.product;

import store.product.promotion.PromotionResult;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;

public class Receipt {

    private String productName;
    private int actualPrice;
    private int disCountPrice;
    private int pricePerOne;
    private int nonPromotedCount;
    PromotionResult promotionResult;

    public Receipt(){}

    public Receipt(String productName, int actualPrice, int disCountPrice, int pricePerOne, int nonPromotedCount){
        this.productName = productName;
        this.actualPrice = actualPrice;
        this.disCountPrice = disCountPrice;
        this.pricePerOne = pricePerOne;
        this.nonPromotedCount = nonPromotedCount;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public int getDisCountPrice() {
        return disCountPrice;
    }

    public String getProductName() { return productName; }

    public int getPricePerOne() { return pricePerOne; }

    public int getNonPromotedCount() { return nonPromotedCount; }

    public Receipt combine(Receipt receipt){
        if(!this.productName.equals(receipt.productName)){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        int actualPrice = this.actualPrice + receipt.actualPrice;
        int discountPrice = this.disCountPrice + receipt.disCountPrice;
        int nonPromotedCount = this.nonPromotedCount + receipt.nonPromotedCount;

        return new Receipt(this.productName,actualPrice,discountPrice,Math.max(pricePerOne,receipt.pricePerOne),nonPromotedCount);
    }

    public static Receipt createEmptyReceipt(String productName){
        int zero = 0;
        return new Receipt(productName,zero,zero,zero,zero);
    }

    public boolean isPromotionApplied(){
        int zero = 0;

        return disCountPrice > zero;
    }

    public int getNonPromotedPrice(){
        return nonPromotedCount * pricePerOne;
    }

    public int getActualCount(){
        int zero = 0;
        if(actualPrice%pricePerOne != zero){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        return actualPrice / pricePerOne;
    }

    public int getPromotedCount(){
        int zero = 0;
        if(disCountPrice%pricePerOne != zero){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        return disCountPrice / pricePerOne;
    }


}
