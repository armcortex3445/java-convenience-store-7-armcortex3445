package store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import store.io.ProductFile;
import store.io.PromotionFile;
import store.product.Product;
import store.product.promotion.Promotion;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;
import store.utils.Transformer;

public class StoreModel {
    private final List<Product> productRepository;
    private final HashMap<String,Promotion> promotionHashMap;

    StoreModel(){
        this.productRepository = new ArrayList<>();
        this.promotionHashMap = new HashMap<>();
    }

    public void initStore(List<Product> products, List<Promotion> promotions){
        addProducts(products);
        putPromotion(promotions);
        applyPromotions();
    }

    private void addProducts(List<Product> products){
        for(Product origin : products){
            this.productRepository.add(origin.clone());
        }
    }
    private void putPromotion(List<Promotion> promotions){
        /*TODO
           - 프로모션 이름 중복이 없도록 해야함.
               - 날짜가 다르더라도, 이름 중복이 없도록 해야함.
               - 프로모션 중복이 있는 경우, promotions.md 파일이 문제이므로 해당 파일에서 확인 필요.
        * */
        for(Promotion promotion : promotions){
            this.promotionHashMap.put(promotion.getName(),promotion.clone());
        }
    }

    private void applyPromotions(){
        for(Product product : this.productRepository){
            product.setPromotion(this.promotionHashMap.get(product.getPromotionName()));
        }
    }

    public List<Product> getProducts(){
        List<Product> copied = new ArrayList<>();
        for(Product origin : this.productRepository){
            copied.add(origin.clone());
        }

        return copied;
    }

    public Product findOneProduct(String name, boolean isPromoted){

        if(isPromoted){
            return findProductPromoted(name);
        }

        return findProductNonPromoted(name);


    }
    public List<Product> findProduct(String name){
        return this.productRepository.stream()
                .filter((product)->product.getName().equals(name))
                .map(Product::clone)
                .collect(Collectors.toList());
    }

    private Product findProductPromoted(String name){
        List<Product> finds = findProduct(name)
                .stream()
                .filter((product -> product.getPromotionName()!=null))
                .toList();
        final int limit = 1;
        if(finds.size() > limit){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }

        return finds.getFirst();
    }

    private Product findProductNonPromoted(String name){
        List<Product> finds = findProduct(name)
                .stream()
                .filter(product -> product.getPromotionName() == Product.NO_PROMOTION)
                .toList();
        final int limit = 1;
        if(finds.size() > limit){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }

        return finds.getFirst();
    }

    /*TODO
    *   - promotion과 product 생성 관련 static 함수를 다른 클래스에 책임 맡길 것으 고려하기
    * */

    static public List<String> parseString(String string){
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException("List is empty");
        }
        final String delimiter = ",";
        String[] parsed = string.split(delimiter);

        return List.of(parsed);
    }

    static public Product createProduct(String rawProduct){
        List<String> parsedRawProduct = StoreModel.parseString(rawProduct);
        String name = parsedRawProduct.get(ProductFile.NAME.getColumnIdx());
        String price = parsedRawProduct.get(ProductFile.PRICE.getColumnIdx());
        String quantity = parsedRawProduct.get(ProductFile.QUANTITY.getColumnIdx());
        String promotion = parsedRawProduct.get(ProductFile.PROMOTION.getColumnIdx());

        if(promotion.equals("null")){
            promotion = null;
        }

        return new Product(name, Transformer.parsePositiveInt(price),Transformer.parsePositiveInt(quantity),promotion);
    }

    static public List<Product> createProducts(String rawProductList){
        List<Product> products = new ArrayList<>();
        String[] rawProducts  = rawProductList.split("\n");

        int idx = 1;

        for( ; idx < rawProducts.length; idx++){
            String rawProduct = rawProducts[idx];
            products.add(StoreModel.createProduct(rawProduct));
        }
        return products;
    }

    static public Promotion createPromotion(String rawPromotion){
        List<String> parsedRawPromotion = StoreModel.parseString(rawPromotion);
        String name = parsedRawPromotion.get(PromotionFile.NAME.getColumnIdx());
        String buyCount = parsedRawPromotion.get(PromotionFile.BUY.getColumnIdx());
        String returnCount = parsedRawPromotion.get(PromotionFile.GET.getColumnIdx());
        String startDate = parsedRawPromotion.get(PromotionFile.START_DATE.getColumnIdx());
        String endDate = parsedRawPromotion.get(PromotionFile.END_DATE.getColumnIdx());

        Promotion.validateReturnCount(returnCount);
        return new Promotion(name, Transformer.parseStartDate(startDate,PromotionFile.DATE_TIME_FORMATTER),Transformer.parseEndDate(endDate,PromotionFile.DATE_TIME_FORMATTER),Transformer.parsePositiveInt(buyCount));
    }

    static public List<Promotion> createPromotions(String rawPromotionList){
        List<Promotion> promotions = new ArrayList<>();
        String[] rawPromotions  = rawPromotionList.split("\n");

        int idx = 1;

        for( ; idx < rawPromotions.length; idx++){
            String rawPromotion = rawPromotions[idx];
            promotions.add(StoreModel.createPromotion(rawPromotion));
        }
        return promotions;
    }


}