package store.product;

import java.time.LocalDateTime;
import java.util.Objects;
import store.product.promotion.Promotion;
import store.product.promotion.PromotionResult;
import store.product.promotion.PromotionState;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;
import camp.nextstep.edu.missionutils.DateTimes;

public class Product implements Cloneable{

    public static final String NO_PROMOTION = null;
    private String name;
    private int price;
    private Promotion promotion = Promotion.NULL;
    private String promotionName = NO_PROMOTION;
    private int count;

    public  Product(){

    }

    public Product(Product src){
        this.name = src.name;
        this.price = src.price;
        this.count = src.count;
        if(src.promotion != Promotion.NULL) {
            this.promotion = src.promotion.clone();
            this.promotionName = src.promotionName;
        }
    }

    public Product(String name, int price, int count,String promotionName){
        this.name = name;
        this.price = price;
        this.count = count;
        if(promotionName != Product.NO_PROMOTION){
            this.promotionName = promotionName;
        }
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

    public String getPromotionName(){
        return promotionName;
    }

    public Promotion getPromotion(){
        return promotion.clone();
    }

    public void setPromotion(String name, LocalDateTime start , LocalDateTime end, int conditionCount){
        if(this.promotion != Promotion.NULL){
            throw new IllegalStateException("Promotion is already exist.");
        }

        if(!this.promotionName.equals(name)){
            throw new IllegalStateException("Promotion is not correspond to promotionName field");
        }
        this.promotion = new Promotion(name,start,end,conditionCount);
    }

    public void setPromotion(Promotion promotion){
        if(this.promotion != Promotion.NULL){
            throw new IllegalStateException("Promotion is already exist.");
        }
        if(promotion == Promotion.NULL){
            return;
        }
        this.promotion = new Promotion(promotion);
    }

    public boolean isPromotionActive(LocalDateTime today){
        if(!isPromotionExist()){
            return false;
        }
        return this.promotion.isActive(today);
    }

    public boolean isBuyEnable(){
        int zero = 0;
        return this.count > zero;
    }

    @Override
    public Product clone(){
        try {
            Product cloned = (Product) super.clone();
            cloned.clonePromotion(this);
            return cloned;
        }catch (CloneNotSupportedException e){
            throw new AssertionError("Cloning not supported");
        }

    }

    public void clonePromotion(Product origin){
        if(origin.promotion != Promotion.NULL) {
            this.promotion = origin.promotion;
        }
    }

    public Receipt buy(int count){
        decreaseCount(count);

        return new Receipt(name,
                this.calculateTotalPrice(count),
                this.calculateDiscountPrice(count),
                this.price,
                calculateNonPromotedCount(count)
        );
    }

    public int calculateNonPromotedCount(int buyCount){
        int nonPromotedCount = buyCount;
        int zero = 0;
        if(this.calculateDiscountPrice(buyCount) > zero){
            int promotionUnit= this.getPromotion().getConditionCount() + Promotion.RETURN_COUNT;
            nonPromotedCount = buyCount % promotionUnit;
        }

        return nonPromotedCount;
    }

    public PromotionResult estimatePromotionResult(int count, LocalDateTime now){
        if(this.isPromotionExist() && this.isPromotionActive(now)) {
            return this.promotion.estimate(name,count);
        }

        return PromotionResult.createNoPromotion(name,count);
    }

    public int calculateMaxCountToBuy(int buyCount){
        if(count < buyCount){
            return count;
        }

        return buyCount;
    }

    public boolean isEnoughToBuy(int buyCount){
        return count > buyCount;
    }

    public boolean isPromotionExist(){
        boolean isPromotionNotNull = this.promotion != Promotion.NULL;
        boolean isPromotionNameExist = this.promotionName != NO_PROMOTION;
        if(isPromotionNotNull != isPromotionNameExist){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        return isPromotionNotNull;
    }

    public boolean isInsufficient(){
        int zero = 0;
        return count == zero;
    }

    private void decreaseCount(int count){
        if(count > this.count){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        this.count -=count;
    }

    private int calculateTotalPrice(int count){
        return this.price*count;
    }

    private int calculateDiscountPrice(int count){
        int result = 0;
        if(isPromotionActive(DateTimes.now())){
            result = this.promotion.checkReturn(count) * this.price;
        }

        return result;
    }

}
