package store;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static camp.nextstep.edu.missionutils.test.Assertions.assertNowTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class StoreInputViewTest {

    @DisplayName("구매 목록에서 정보를 추출한다")
    @Test
    void testExtractInfoFromPurchase(){
        String purchase = "[사이다-12]";
        StoreInputView.validatePurchase(purchase);
        List<String> info = StoreInputView.extractElementsFromPurchase(purchase);
        assertThat(info.get(0)).isEqualTo("사이다");
        assertThat(info.get(1)).isEqualTo("12");
    }

    @DisplayName("구매 목록의 유효성 점검한다")
    @ParameterizedTest
    @ValueSource(strings = {"[사이다-100]","[Coke1-10]"})
    void testExtractInfoFromPurchase(String purchase ){
        StoreInputView.validatePurchase(purchase);
    }

    @DisplayName("구매 목록의 형식이 잘못되면 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"[사이다-]"," \n", "[사이다-2","사이다-2","[사이다-사이다]", "[사이다-?]"})
    void testExceptionValidatePurchaseProduct(String purchase){
        assertThatThrownBy(()->{
            StoreInputView.validatePurchase(purchase);
        }).isInstanceOfAny(IllegalArgumentException.class);
    }

    @DisplayName("구매 목록 리스트의 형식이 잘못되면 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"[사이다-1],[콜라-]","[사이다-1] - [콜라-2]","[과자-?],[사이다-2]","[사이다-1]\n[과자-2]"})
    void testExceptionValidatePurchaseProductList(String purchaseList){
        assertThatThrownBy(()->{
            StoreInputView.validatePurchaseList(purchaseList);
        }).isInstanceOfAny(IllegalArgumentException.class);
    }

    @DisplayName("대답이 Y/N 형식이 아니면 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(strings = {"?","c","\n","YES"})
    void testExceptionValidateAnswer(String answer){
        assertThatThrownBy(()->{
            StoreInputView.validateAnswer(answer);
        }).isInstanceOfAny(IllegalArgumentException.class);
    }
}
