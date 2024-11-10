package store.product.promotion;


public class PromotionResult {

    private int appliedItemCount = 0;
    private int neededItemCount = 0;
    private PromotionState state;
    private String productName;

    public PromotionResult(){

    }

    public PromotionResult(PromotionState state, int appliedItemCount, int neededItemCount, String productName){
        this.state = state;
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

    public PromotionState getState() { return state; }

    public String getProductName() {return  productName;}

    public PromotionResult transitState(PromotionState newState){
        return new PromotionResult(newState,appliedItemCount,neededItemCount,productName);
    }

    static public PromotionResult createNoPromotion(String productName){
        final int zero = 0;
        return new PromotionResult(PromotionState.NO_PROMOTION,zero,zero,productName);
    }


}
