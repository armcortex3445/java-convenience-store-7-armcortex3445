package store;

/*프로모션 할인
- [x]N+1 프로모션이 각각 지정된 상품에 적용된다.
- [x]프로모션 혜택은 프로모션 재고 내에서만 적용할 수 있다.
- [x]프로모션 기간 중이라면 프로모션 재고를 우선적으로 차감하며, 프로모션 재고가 부족할 경우에는 일반 재고를 사용한다.
- [model]프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음을 안내한다.
- [view]프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제하게 됨을 안내한다.
* */

/*입출력 요구 사항
- [model]구현에 필요한 상품 목록과 행사 목록을 파일 입출력을 통해 불러온다.
  - [ ] 상품 목록 읽기 기능
  - [ ] 행사 목록 입출력 기능
- [view] 구매할 상품과 수량을 입력 받는다. 상품명, 수량은 하이픈(-)으로, 개별 상품은 대괄호([])로 묶어 쉼표(,)로 구분한다.
- [view] 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 그 수량만큼 추가 여부를 입력받는다.
- [view] 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제할지 여부를 입력받는다.
- [view] 멤버십 할인 적용 여부를 입력 받는다
- [view] 추가 구매 여부를 입력 받는다.
* */

/*
* 재고 관리
- [ ]각 상품의 재고 수량을 고려하여 결제 가능 여부를 확인한다.
   - [model] 상품의 재고 수량 정보를 제공
   - [ ] 상품의 재고가 품절인 경우 결제 금지하는 기능
- [ ]고객이 상품을 구매할 때마다, 결제된 수량만큼 해당 상품의 재고에서 차감하여 수량을 관리한다.
- [ ]재고를 차감함으로써 시스템은 최신 재고 상태를 유지하며, 다음 고객이 구매할 때 정확한 재고 정보를 제공한다.*/


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static camp.nextstep.edu.missionutils.test.Assertions.assertNowTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.product.Product;
import store.product.PurchaseRequest;
import store.product.Receipt;
import store.product.promotion.Promotion;
import store.product.promotion.PromotionResult;
import store.product.promotion.PromotionState;

public class StoreModelTest {

    @DisplayName("상품 목록 한줄 처리 기능")
    @Test
    void testReadProduct(){
        String name = "콜라";
        String price = "1000";
        String quantity = "10";
        String promotion = "탄산2+1";
        String src = String.join(",",name,price,quantity,promotion);

        Product product = StoreModel.createProduct(src);

        assertThat(Integer.toString(product.getPrice())).isEqualTo(price);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(Integer.toString(product.getCount())).isEqualTo(quantity);
        assertThat(product.getPromotionName()).isEqualTo(promotion);

    }

    @DisplayName("상품 목록 읽기 기능")
    @Test
    void testReadProductList(){
        String src = "name,price,quantity,promotion\n"
                + "콜라,1000,10,탄산2+1\n"
                + "콜라,1000,10,null\n"
                + "사이다,1000,8,탄산2+1";

        List<Product> products = StoreModel.createProducts(src);

        assertThat(products.get(0).getName()).isEqualTo("콜라");
        assertThat(products.get(0).getPromotionName()).isEqualTo("탄산2+1");
        assertThat(products.get(1).getName()).isEqualTo("콜라");
        assertThat(products.get(1).getPromotionName()).isEqualTo(null);
        assertThat(products.get(2).getName()).isEqualTo("사이다");

    }

    @DisplayName("프로모션 한 줄 읽기")
    @Test
    void testReadPromotion(){
        String src = "탄산2+1,2,1,2024-01-01,2024-12-31";

        Promotion promotion = StoreModel.createPromotion(src);

        assertThat(promotion.getConditionCount()).isEqualTo(2);
        assertThat(promotion.getName()).isEqualTo("탄산2+1");
        assertThat(promotion.getStartDate()).isEqualTo("2024-01-01");
        assertThat(promotion.getEndDate()).isEqualTo("2024-12-31");

    }

    @DisplayName("프로모션 목록 읽기")
    @Test
    void testReadPromotionList(){
        String src = "name,buy,get,start_date,end_date\n"
                + "탄산2+1,2,1,2024-01-01,2024-12-31\n"
                + "MD추천상품,1,1,2024-01-01,2024-12-31\n"
                + "반짝할인,1,1,2024-11-01,2024-11-30";

        List<Promotion> promotions = StoreModel.createPromotions(src);

        assertThat(promotions.get(0).getName()).isEqualTo("탄산2+1");
        assertThat(promotions.get(1).getName()).isEqualTo("MD추천상품");
        assertThat(promotions.get(2).getName()).isEqualTo("반짝할인");
    }


   @DisplayName("N+1 프로모션이 각각 지정된 상품에 적용된다.")
   @Test
   void testApplyPromotionToProduct(){
        String promotionList = "name,buy,get,start_date,end_date\n"
                + "탄산2+1,2,1,2024-01-01,2024-12-31";
        String productList = "name,price,quantity,promotion\n"
                + "콜라,1000,10,탄산2+1\n"
                + "콜라,1000,1,null";

        List<Product> products = StoreModel.createProducts(productList);
        List<Promotion> promotions = StoreModel.createPromotions(promotionList);

        StoreModel storeModel = new StoreModel();

        assertThat(storeModel.findProduct("콜라").isEmpty()).isEqualTo(true);

        storeModel.initStore(products,promotions);

        Product promotedCoke = storeModel.findOneProduct("콜라",true);
        assertThat(promotedCoke.getPromotionName()).isEqualTo("탄산2+1");
        assertThat(promotedCoke.getCount()).isEqualTo(10);

        Product nonPromotedCoke = storeModel.findOneProduct("콜라", false);
        assertThat(nonPromotedCoke.getPromotionName()).isEqualTo(Product.NO_PROMOTION);
        assertThat(nonPromotedCoke.getCount()).isEqualTo(1);
    }

    @DisplayName("프로모션 혜택은 프로모션 재고 내에서만 적용할 수 있다.")
    @Test
    void testApplyPromotionToOnlyPromotionProduct(){
        assertNowTest(()-> {
            String promotionList = "name,buy,get,start_date,end_date\n"
                    + "탄산2+1,2,1,2024-01-01,2024-12-31";
            String productList = "name,price,quantity,promotion\n"
                    + "콜라,1000,10,탄산2+1\n"
                    + "콜라,1000,10,null";

            List<Product> products = StoreModel.createProducts(productList);
            List<Promotion> promotions = StoreModel.createPromotions(promotionList);

            StoreModel storeModel = new StoreModel();
            storeModel.initStore(products, promotions);

            PurchaseRequest request = new PurchaseRequest("콜라", 6);

            Receipt receipt = storeModel.buyProduct(request);
            assertThat(receipt.getActualPrice()).isEqualTo(6000);
            assertThat(receipt.getDisCountPrice()).isEqualTo(2000);

            Product productPromoted = storeModel.findOneProduct("콜라",true);
            Product productNonPromoted = storeModel.findOneProduct("콜라",false);
            assertThat(productPromoted.getCount()).isEqualTo(4);
            assertThat(productNonPromoted.getCount()).isEqualTo(10);
        }, LocalDate.of(2024, 2, 1).atStartOfDay());


    }

    @DisplayName("프로모션 기간 중이라면 프로모션 재고를 우선적으로 차감하며, 프로모션 재고가 부족할 경우에는 일반 재고를 사용한다.")
    @Test
    void testSellPromotedProductFirst(){
        String promotionList = "name,buy,get,start_date,end_date\n"
                + "탄산2+1,2,1,2024-01-01,2024-12-31";
        String productList = "name,price,quantity,promotion\n"
                + "콜라,1000,10,탄산2+1\n"
                + "콜라,1000,10,null";

        List<Product> products = StoreModel.createProducts(productList);
        List<Promotion> promotions = StoreModel.createPromotions(promotionList);

        StoreModel storeModel = new StoreModel();
        storeModel.initStore(products, promotions);

        PurchaseRequest request = new PurchaseRequest("콜라", 11);

        Receipt receipt = storeModel.buyProduct(request);

        Product productPromoted = storeModel.findOneProduct("콜라",true);
        Product productNonPromoted = storeModel.findOneProduct("콜라",false);
        assertThat(productPromoted.getCount()).isEqualTo(0);
        assertThat(productNonPromoted.getCount()).isEqualTo(9);
    }


    @DisplayName("오늘 날짜가 프로모션 기간 내에 포함된 경우에만 할인을 적용한다.")
    @Test
    void testApplyPromotedOnlyEventDays() {
        assertNowTest(() -> {
            String promotionList = "name,buy,get,start_date,end_date\n"
                    + "탄산2+1,2,1,2024-01-01,2024-12-31\n"
                    + "반짝할인,1,1,2024-11-01,2024-11-30";
            String productList = "name,price,quantity,promotion\n"
                    + "콜라,1000,10,탄산2+1\n"
                    + "콜라,1000,10,null\n"
                    + "사이다,1000,10,반짝할인";

            List<Product> products = StoreModel.createProducts(productList);
            List<Promotion> promotions = StoreModel.createPromotions(promotionList);

            StoreModel storeModel = new StoreModel();
            storeModel.initStore(products, promotions);

            PurchaseRequest request = new PurchaseRequest("콜라", 6);
            Receipt receipt = storeModel.buyProduct(request);
            assertThat(receipt.getDisCountPrice()).isEqualTo(2000);

            PurchaseRequest requestCider = new PurchaseRequest("사이다",10);
            Receipt receiptCider = storeModel.buyProduct(requestCider);
            assertThat(receiptCider.getDisCountPrice()).isEqualTo(0);

        }, LocalDateTime.of(2024, 1, 2, 0, 0));
    }

    @DisplayName("물건 구매시, 프로모션 적용 결과 정보를 알린다.")
    @Test
    void testProvidePromotionInfo(){
        LocalDateTime validTo2Plus1 = LocalDateTime.of(2024, 1, 2, 0, 0);
        LocalDateTime validToAll = LocalDateTime.of(2024,11,2,0,0);
        assertNowTest(() -> {
            String promotionList = "name,buy,get,start_date,end_date\n"
                    + "탄산2+1,2,1,2024-01-01,2024-12-31\n"
                    + "반짝할인,1,1,2024-11-01,2024-11-30";
            String productList = "name,price,quantity,promotion\n"
                    + "콜라,1000,10,탄산2+1\n"
                    + "콜라,1000,10,null\n"
                    + "사이다,1000,10,반짝할인\n"
                    +"과자,1000,20,null";

            List<Product> products = StoreModel.createProducts(productList);
            List<Promotion> promotions = StoreModel.createPromotions(promotionList);

            StoreModel storeModel = new StoreModel();
            storeModel.initStore(products, promotions);

            PromotionResult promotionResult = storeModel
                    .checkProductPromotionAvailable("콜라",5);
            assertThat(promotionResult.getState())
                    .as("프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 사는 경우,"
                            + " 무료 증정이 있음을 알린다.")
                    .isEqualTo(PromotionState.OMISSION);
            assertThat(promotionResult.getOmittedItemCount()).isEqualTo(1);

            promotionResult = storeModel
                    .checkProductPromotionAvailable("콜라",10);
            assertThat(promotionResult.getState())
                    .as("프로모션 적용이 가능한 상품에 대해 재고가 부족한 경우,"
                            + " 수량 부족 상태임을 알린다.")
                    .isEqualTo(PromotionState.INSUFFICIENT);

            promotionResult = storeModel
                    .checkProductPromotionAvailable("과자",10);
            assertThat(promotionResult.getState())
                    .as("프로모션 적용이 가능한 상품이 아닌경우,"
                            + " 프로모션 미적용 상태임을 알린다.")
                    .isEqualTo(PromotionState.NO_PROMOTION);


        }, validToAll);

    }

}
