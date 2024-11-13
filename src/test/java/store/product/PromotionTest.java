package store.product;


import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import store.product.promotion.Promotion;
import store.product.promotion.PromotionResult;
import store.product.promotion.PromotionState;



/*프로모션 할인
- [x]오늘 날짜가 프로모션 기간 내에 포함된 경우에만 할인을 적용한다.
- [x]프로모션은 N개 구매 시 1개 무료 증정(Buy N Get 1 Free)의 형태로 진행된다.
* */

public class PromotionTest {

    @DisplayName("프로모션 표현")
    @Test
    void testPromotionRepresent(){
        int conditionCount = 2;
        LocalDateTime start = LocalDateTime.of(2024,12,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,12,31,23,59);
        Promotion promotion = new Promotion("2+1 할인",start,end,conditionCount);

        assertThat(promotion.getName()).isEqualTo("2+1 할인");
        assertThat(promotion.getConditionCount()).isEqualTo(conditionCount);
        assertThat(promotion.getStartDate()).isEqualTo("2024-12-01");
        assertThat(promotion.getEndDate()).isEqualTo("2024-12-31");

    }

    @DisplayName("프로모션 적용시, 무료 제공 개수를 알려준다.")
    @Test
    void testPromotionReturn(){
        int conditionCount = 2;
        LocalDateTime start = LocalDateTime.of(2024,12,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,12,31,23,59);
        Promotion promotion = new Promotion("2+1 할인",start,end,conditionCount);

        int buyCount = 6;

        assertThat(promotion.checkReturn(buyCount)).isEqualTo(2);

    }

    @DisplayName(" 날짜가 프로모션 기간 내에 포함되는지 알려준다.")
    @Test
    void testCheckPromotionActive(){
        int conditionCount = 2;
        LocalDateTime start = LocalDateTime.of(2024,12,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,12,31,23,59);
        Promotion promotion = new Promotion("2+1 할인",start,end,conditionCount);


        LocalDateTime invalid = LocalDateTime.of(2024,11,1,0,0);
        LocalDateTime valid = LocalDateTime.of(2024,12,2,0,0);

        assertThat(promotion.isActive(start)).isEqualTo(true);
        assertThat(promotion.isActive(end)).isEqualTo(true);

        assertThat(promotion.isActive(invalid)).isEqualTo(false);
        assertThat(promotion.isActive(valid)).isEqualTo(true);

    }

    @DisplayName("물건 구매시에 대한 누락된 프로모션 증정 개수를 반환한다 ")
    @Test
    void testCheckOmittedReturnCount(){
        int conditionCount = 2;
        LocalDateTime start = LocalDateTime.of(2024,12,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,12,31,23,59);
        Promotion promotion = new Promotion("2+1 할인",start,end,conditionCount);


        assertThat(promotion.checkOmittedReturn(10)).isEqualTo(0);
        assertThat(promotion.checkOmittedReturn(11)).isEqualTo(1);


    }

    @DisplayName("프로모션은 N개 구매 시 1개 무료 증정(Buy N Get 1 Free)의 형태로 진행된다.")
    @Test
    void testApplyPromotion(){
        int conditionCount = 2;
        LocalDateTime start = LocalDateTime.of(2024,12,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,12,31,23,59);
        Promotion promotion = new Promotion("2+1 할인",start,end,conditionCount);

        int count1 = 4;
        PromotionResult result1 = promotion.estimate("상품명",count1);
        assertThat(result1.getState()).as("무료 증정 조건을 만족하지 못하므로, APPLIED 상태가 된다").isEqualTo(PromotionState.APPLIED);
        assertThat(result1.getAppliedItemCount()).isEqualTo(1);
        assertThat(result1.getOmittedItemCount()).isEqualTo(0);

        assertThat(promotion.estimate("상품명",3).getState()).isEqualTo(PromotionState.APPLIED);
    }

    @DisplayName("물건 구매에 따른 프로모션 결과를 추정한다")
    @Test
    void testEstimatePromotionResult(){
        int conditionCount = 2;
        LocalDateTime start = LocalDateTime.of(2024,12,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,12,31,23,59);
        Promotion promotion = new Promotion("2+1 할인",start,end,conditionCount);

        PromotionResult promotionResult = promotion.estimate("상품명",11);
        assertThat(promotionResult.getState())
                .as("프로모션 적용보다 물건을 덜 가져오면, 물건이 더 필요한 상태임을 나타낸다.")
                .isEqualTo(PromotionState.OMISSION);
        assertThat(promotionResult.getOmittedItemCount())
                .as("프로모션 적용보다 물건을 덜 가져오면, 프로모션 적용을 위해 필요한 물건 개수를 나타낸다")
                .isEqualTo(1);

        promotionResult = promotion.estimate("상품명",6);
        assertThat(promotionResult.getState())
                .isEqualTo(PromotionState.APPLIED);
        assertThat(promotionResult.getOmittedItemCount())
                .isEqualTo(0);
    }


}
