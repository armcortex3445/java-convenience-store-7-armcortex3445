package store.product;

import store.product.promotion.PromotionResult;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;

public class Receipt {

    private String productName;
    private int actualPrice;
    private int disCountPrice;
    PromotionResult promotionResult;

    public Receipt(){}

    public Receipt(String productName, int actualPrice, int disCountPrice){
        this.productName = productName;
        this.actualPrice = actualPrice;
        this.disCountPrice = disCountPrice;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public int getDisCountPrice() {
        return disCountPrice;
    }

    public String getProductName() { return productName; }

    public Receipt combine(Receipt receipt){
        if(!this.productName.equals(receipt.productName)){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        int actualPrice = this.actualPrice + receipt.actualPrice;
        int discountPrice = this.disCountPrice + receipt.disCountPrice;

        return new Receipt(this.productName,actualPrice,discountPrice);
    }

    public static Receipt createEmptyRecipt(String productName){
        int zero = 0;
        return new Receipt(productName,zero,zero);
    }
}
