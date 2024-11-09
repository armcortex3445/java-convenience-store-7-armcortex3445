package store.product;


import camp.nextstep.edu.missionutils.test.NsTest;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static camp.nextstep.edu.missionutils.test.Assertions.assertNowTest;
import static camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import camp.nextstep.edu.missionutils.DateTimes;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;



/*프로모션 할인
- [x]오늘 날짜가 프로모션 기간 내에 포함된 경우에만 할인을 적용한다.
- [x]프로모션은 N개 구매 시 1개 무료 증정(Buy N Get 1 Free)의 형태로 진행된다.
* */

public class PromotionTest {

    @DisplayName(" 날짜가 프로모션 기간 내에 포함된 경우에만 할인을 적용한다.")
    @Test
    void testPromotionActive(){
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

    @DisplayName("프로모션은 N개 구매 시 1개 무료 증정(Buy N Get 1 Free)의 형태로 진행된다.")
    @Test
    void testApplyPromotion(){
        int conditionCount = 2;
        LocalDateTime start = LocalDateTime.of(2024,12,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,12,31,23,59);
        Promotion promotion = new Promotion("2+1 할인",start,end,conditionCount);

        int count1 = 4;
        PromotionResult result1 = promotion.getPromotionResult(count1);
        assertThat(result1.getState()).isEqualTo(PromotionState.MORE_NEEDED);
        assertThat(result1.getAppliedItemCount()).isEqualTo(1);
        assertThat(result1.getNeededItemCount()).isEqualTo(2);

        assertThat(promotion.getPromotionResult(3).getState()).isEqualTo(PromotionState.APPLIED);

    }


}
