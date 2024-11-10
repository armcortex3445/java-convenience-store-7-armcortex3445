package store.product;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.product.promotion.Promotion;


/*프로모션 할인
- [model]1+1 또는 2+1 프로모션이 각각 지정된 상품에 적용된다.
- [x]동일 상품에 여러 프로모션이 적용되지 않는다.
- [model]프로모션 혜택은 프로모션 재고 내에서만 적용할 수 있다.
- [model]프로모션 기간 중이라면 프로모션 재고를 우선적으로 차감하며, 프로모션 재고가 부족할 경우에는 일반 재고를 사용한다.
- []프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음을 안내한다.
- [view]프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제하게 됨을 안내한다.
* */


public class ProductTest {

    @DisplayName("상품을 표현한다")
    @Test
    void testProductRepresent(){
        Product product = new Product("연필",1000,100,"1+1");

        assertThat(product.getCount()).isEqualTo(100);
        assertThat(product.getPromotionName()).isEqualTo("1+1");
        assertThat(product.getName()).isEqualTo("연필");
        assertThat(product.getPrice()).isEqualTo(1000);
    }

    @DisplayName("상품에 프로모션을 적용할 수 있다.")
    @Test
    void testApplyPromotion(){
        Product product = new Product("연필",1000,100,"1+1");
        Promotion promotion = new Promotion("1+1",LocalDateTime.of(2024,1,1,0,0),LocalDateTime.of(2024,1,12,0,0),10);

        product.setPromotion(promotion);
        assertThat(product.getPromotion().getName()).isEqualTo(promotion.getName());
        assertThat(product.getPromotion().getConditionCount()).isEqualTo(promotion.getConditionCount());
        assertThat(product.getPromotion().getStartDate()).isEqualTo(promotion.getStartDate());
        assertThat(product.getPromotion().getEndDate()).isEqualTo(promotion.getEndDate());
    }

    @DisplayName("날짜를 통해 상품의 프로모션 적용을 확인할 수 있다.")
    @Test
    void testCheckPromotionActive(){
        Product product = new Product("연필",1000,100,"1+1");
        Promotion promotion = new Promotion("1+1",LocalDateTime.of(2024,1,1,0,0),LocalDateTime.of(2024,1,12,0,0),10);

        product.setPromotion(promotion);
        assertThat(product.isPromotionActive(LocalDateTime.of(2024,1,1,9,0,0))).isEqualTo(true);
    }

    @DisplayName("동일 상품에 여러 프로모션이 적용되지 않는다.")
    @Test
    void testExceptionWhenMultiplePromotion(){

        Product product = new Product("연필",1000,100,"1+1");

        LocalDateTime start = LocalDateTime.of(2024,1,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,1,2,0,0);

        product.setPromotion("1+1",start,end,1);

        assertThatThrownBy(()->product.setPromotion("1+1",start,end,1)).isInstanceOf(IllegalStateException.class);
    }
}
