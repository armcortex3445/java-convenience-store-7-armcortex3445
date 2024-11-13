package store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import store.io.ProductFile;
import store.io.PromotionFile;
import store.product.Product;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;
import store.utils.Transformer;
import store.utils.Validator;

public class LoadModel {
    public final static String PRODUCT_LIST_FORMAT = "name,price,quantity,promotion";
    public final static String PROMOTION_LIST_FROMAT= "name,buy,get,start_date,end_date";
    public final static String DELIMITER = "\n";
    final static String RESOURCE_PATH = "./src/main/resources/";
    final static String PRODUCT_LIST = "products.md";
    final static String PROMOTION_LIST = "promotions.md";
    public LoadModel(){
    }

    public String loadProductList(){
        String result = readFileAsString(RESOURCE_PATH + PRODUCT_LIST);
        validateProductList(result);
        return result;
    }

    public String loadPromotionList(){
        String result = readFileAsString(RESOURCE_PATH + PROMOTION_LIST);
        validatePromotionList(result);
        return result;
    }

    public List<String> readFile(String path){
        Validator.validateFileSize(path);
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException exception){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.READ_FILE_FAIL,exception);
            throw new RuntimeException();
        }
    }

    public String readFileAsString(String path){

        List<String> contents = readFile(path);
        if(contents.isEmpty()) {
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.EMPTY_FILE);
        }
        return Transformer.concatenateList(contents,DELIMITER);
    }

    public static void validateProductList(String products){
        if(products.isBlank()) {
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.EMPTY_FILE);
        }
        List<String> productList = Arrays.stream(products.split(DELIMITER)).toList();
        if(!productList.getFirst().equals(PRODUCT_LIST_FORMAT)){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_FILE_FORMAT);
        }
        Validator.validateEachLine(productList, ProductFile.COLUMN_DELIMITER,ProductFile.values().length);
    }

    public static void validatePromotionList(String promotions){
        if(promotions.isBlank()) {
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.EMPTY_FILE);
        }
        List<String> promotionList = Arrays.stream(promotions.split(DELIMITER)).toList();
        if(!promotionList.getFirst().equals(PROMOTION_LIST_FROMAT)){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_FILE_FORMAT);
        }
        Validator.validateEachLine(promotionList,PromotionFile.COLUMN_DELIMITER,PromotionFile.values().length);
    }


}
