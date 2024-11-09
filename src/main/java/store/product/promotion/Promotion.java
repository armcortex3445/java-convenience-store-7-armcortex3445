package store.product.promotion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import store.io.PromotionFile;
import store.product.Product;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;
import store.utils.Transformer;

public class Promotion implements Cloneable {

    public static final Promotion NULL = null;

    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int conditionCount;
    private int itemCount; // need to consider

    public static final int RETURN_COUNT = 1;

    public Promotion(){};

    public Promotion(Promotion src){
        this.name = src.name;
        this.startDate = src.startDate;
        this.endDate = src.endDate;
        this.conditionCount = src.conditionCount;
    }

    /*TODO
     *   - 재고가 부족하여 프로모션 할인이 적용되지 않는 경우 고려 필요
     * */
    public Promotion(String name, LocalDateTime startDate, LocalDateTime endDate, int conditionCount){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.conditionCount = conditionCount;
    }

    public boolean isActive(LocalDateTime today){
        /*TODO
           - today null값 예방하기
        * */
        return isEqualToEdgeDate(today) || isValidDate(today);
    }

    public int checkReturn(int count){
        final int unitCount = this.conditionCount + RETURN_COUNT;

        return count / unitCount;
    }

    public int checkNeededItem(int count){
        final int unitCount = this.conditionCount + RETURN_COUNT;

        int neededItem = unitCount - (count % unitCount);
        if(neededItem == unitCount){
            neededItem = 0;
        }

        return neededItem;
    }

    public PromotionResult getPromotionResult(int count){
        /*TODO
         *  - 입력값 양수 점검 필요
         *  - 함수 리팩토링 필요(라인 수 조절, 함수 쪼개기, 클래스 책임 고려)
         * */

        int applyItemCount =  this.checkReturn(count);
        int neededItemCount = this.checkNeededItem(count);;
        PromotionState state = PromotionState.APPLIED;

        if(neededItemCount > 0){
            state = PromotionState.MORE_NEEDED;
        }

        return new PromotionResult(state,applyItemCount,neededItemCount);

    }

    public String getStartDate(){
        return this.startDate.format(PromotionFile.DATE_TIME_FORMATTER);
    }

    public String getEndDate(){
        return this.endDate.format(PromotionFile.DATE_TIME_FORMATTER);
    }

    public String getName() { return this.name;}

    public int getConditionCount(){
        return this.conditionCount;
    }


    public boolean isEqualToEdgeDate(LocalDateTime today){
        return today.isEqual(this.startDate) || today.isEqual(this.endDate);
    }

    public boolean isValidDate(LocalDateTime today){
        return today.isAfter(this.startDate) && today.isBefore(this.endDate);
    }

    @Override
    public Promotion clone() {
        try {
             return (Promotion) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported");
        }
    }

    static public Promotion createPromotion(String name, LocalDateTime startDate, LocalDateTime endDate, int conditionCount){
        return new Promotion(name,startDate,endDate,conditionCount);
    }

    static public void validateReturnCount(String returnCount){
        if(Transformer.parsePositiveInt(returnCount) !=(Promotion.RETURN_COUNT)){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_RETURN_PROMOTION);
        }
    }
}
