package store.product.promotion;


import store.utils.ExceptionFactory;
import store.utils.ExceptionType;

public class PromotionResult {

    private int appliedItemCount = 0;
    private int neededItemCount = 0;
    private int totalItemCount = 0;
    private PromotionState state;
    private String productName;

    public PromotionResult(){

    }

    public PromotionResult(PromotionState state, int totalItemCount,int appliedItemCount, int neededItemCount, String productName){
        this.state = state;
        this.totalItemCount = totalItemCount;
        this.appliedItemCount = appliedItemCount;
        this.neededItemCount = neededItemCount;
        this.productName = productName;

    }

//    public void setAppliedItemCount(int appliedItemCount) {
//        this.appliedItemCount = appliedItemCount;
//    }
//
//    public void setNeededItemCount(int enableItemCount) {
//        this.neededItemCount = enableItemCount;
//    }

    public int getAppliedItemCount() {
        return appliedItemCount;
    }

    public int getNeededItemCount() {
        return neededItemCount;
    }

    public int getTotalItemCount() { return totalItemCount;}

    public int getNonPromotedCount() {
        int fullPromotedCount = totalItemCount + neededItemCount;
        int appliedCountWhenFullPromoted = appliedItemCount + 1;
        int zero = 0;
        if(fullPromotedCount% appliedCountWhenFullPromoted != zero){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        int promotionUnit = fullPromotedCount/appliedCountWhenFullPromoted;
        return totalItemCount % promotionUnit;
    }

    public PromotionState getState() { return state; }

    public String getProductName() {return  productName;}

    public PromotionResult transitState(PromotionState newState){
        return new PromotionResult(newState,totalItemCount,appliedItemCount,neededItemCount,productName);
    }

    static public PromotionResult createNoPromotion(String productName){
        final int zero = 0;
        return new PromotionResult(PromotionState.NO_PROMOTION,zero,zero,zero,productName);
    }


}
