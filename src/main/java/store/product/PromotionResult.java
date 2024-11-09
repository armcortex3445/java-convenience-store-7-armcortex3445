package store.product;


public class PromotionResult {

    private int appliedItemCount = 0;
    private int neededItemCount = 0;
    private PromotionState state;

    public PromotionResult(){

    }

    public PromotionResult(PromotionState state, int appliedItemCount, int neededItemCount){
        this.state = state;
        this.appliedItemCount = appliedItemCount;
        this.neededItemCount = neededItemCount;

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


}
