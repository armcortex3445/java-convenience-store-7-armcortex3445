package store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


public class LoadModelTest {

    @DisplayName("특정 경로에서 파일을 읽을 수 있다.")
    @Test
    void testReadFile(){
        LoadModel loadModel = new LoadModel();
        String path = "./src/test/test.md";
        String contents = loadModel.readFileAsString(path);

        assertThat(contents).isEqualTo("hello world~");
    }

    @DisplayName("파일 위치가 잘못된 경우 예외가 발생한다")
    @Test
    void testExceptionWrongPath(){
        LoadModel loadModel = new LoadModel();
        String path = "./src/test/wrongPath";
        assertThatThrownBy(()->loadModel.readFileAsString(path)).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("파일이 빈 경우 예외가 발생한다")
    @Test
    void testExceptionEmptyFile(){
        LoadModel loadModel = new LoadModel();
        String path = "./src/test/empty.md";
        assertThatThrownBy(()->loadModel.readFileAsString(path)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("products.md 내용이 형식을 벗어난 경우 예외가 발생한다")
    @ParameterizedTest()
    @ValueSource(strings = {
            "name,price,quantity,promotion\n"
                + "콜라,1000,10,탄산2+1\n"
                + "콜라,1000,"
            ,"name,price,quantity,promotion\n"
                +"콜라,,10,탄산2+1"
            ,"콜라,1000,10,탄산2+1"
            ,"name,price,quantity"
            ,"\n"
            ,"    "
    })
    void testExceptionProductFileContentFormat(String products){
        assertThatThrownBy(()->LoadModel.validateProductList(products)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("promotions.md 내용이 형식을 벗어난 경우 예외가 발생한다")
    @ParameterizedTest()
    @ValueSource(strings = {
            "name,buy,get,start_date,end_date\n"
                    + "탄산2+1,2,1,,2024-12-31"
            ,"name,buy,get,start_date,end_date\n"
                + "탄산2+1,2,1,2024-01-01,2024-12-31\n"
                + "MD추천상품,1,1,,2024-12-31"
            ,"name,buy,get,start_date,"
            ,"\n"
            , "   "
    })
    void testExceptionPromotionFileContentFormat(String promotions){
        assertThatThrownBy(()->LoadModel.validatePromotionList(promotions)).isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("src/main/resources/products.md 파일을 읽을 수 있다.")
    @Test
    void testReadProductListFile(){
        LoadModel loadModel = new LoadModel();
        String productList = loadModel.loadProductList();

        assertThat(productList)
                .contains(
                        "name,price,quantity,promotion\n"
                        ,"오렌지주스,1800,9,MD추천상품\n"
                        ,"탄산수,1200,5,탄산2+1\n"
                );
    }

    @DisplayName("src/main/resources/promotions.md 파일을 읽을 수 있다")
    @Test
    void testReadPromotionListFile(){
        LoadModel loadModel = new LoadModel();
        String productList = loadModel.loadPromotionList();

        assertThat(productList)
                .contains(
                        "name,buy,get,start_date,end_date\n"
                        ,"탄산2+1,2,1,2024-01-01,2024-12-31"
                );
    }


}
