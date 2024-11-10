package store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import store.io.ProductFile;
import store.io.PromotionFile;
import store.product.Product;
import store.product.PurchaseRequest;
import store.product.Receipt;
import store.product.promotion.Promotion;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;
import store.utils.Transformer;

public class StoreModel {
    static private final  Product NOT_FOUND = null;
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

    /*TODO
       - 접근지시자, static 키워드 기준으로 메소드 정렬하기
    * */
    public Product findOneProduct(String name, boolean isPromoted){
        return this.findOneOriginProduct(name,isPromoted).clone();
    }

    public List<Product> findProduct(String name){
        return this.findProductOrigin(name).stream()
                .map(Product::clone)
                .toList();
    }

    public Product findOneOriginProduct(String name, boolean isPromoted){
        if(isPromoted){
            return findProductPromoted(name);
        }
        return findProductNonPromoted(name);
    }

    public List<Product> findProductOrigin(String name){
        return this.productRepository.stream()
                .filter((product)->product.getName().equals(name))
                .collect(Collectors.toList());
    }

    private Product findProductPromoted(String name){
        List<Product> finds = findProductOrigin(name).stream()
                .filter((product -> product.getPromotionName()!=null))
                .toList();
        validateFindList(finds);
        if(finds.isEmpty()){
            return NOT_FOUND;
        }
        return finds.getFirst();
    }

    private Product findProductNonPromoted(String name){
        List<Product> finds = findProductOrigin(name).stream()
                .filter(product -> product.getPromotionName() == Product.NO_PROMOTION)
                .toList();
        validateFindList(finds);
        if(finds.isEmpty()){
            return NOT_FOUND;
        }
        return finds.getFirst();
    }

    private void validateFindList(List<?> finds){
        int limit = 1;
        if(finds.size() > limit){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
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

    public List<Receipt> buyProducts(List<PurchaseRequest> purchaseRequests){
        List<Receipt> receipts = new ArrayList<>();
        for(PurchaseRequest request : purchaseRequests){
            receipts.add(buyProduct(request));
        }
        return receipts;
    }

    public Receipt buyProduct(PurchaseRequest request){
        Receipt resultPromoted = tryBuyProductPromoted(request);
        Receipt resultNonPromoted = tryBuyProductNonPromoted(request);

        return combineReceipt(resultPromoted,resultNonPromoted);
    }

    private Receipt combineReceipt(Receipt promoted, Receipt nonPromoted) {
        final Receipt nullReceipt = null;
        if (promoted == nullReceipt && nonPromoted == nullReceipt) {
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        if (promoted == nullReceipt){
            return nonPromoted;
        }
        if(nonPromoted == nullReceipt){
            return promoted;
            }
        return promoted.combine(nonPromoted);
    }

    private Receipt tryBuyProductPromoted(PurchaseRequest request){
        boolean isPromoted = true;
        Product product = this.findOneOriginProduct(request.getProductName(),isPromoted);
        if(product == NOT_FOUND){
            return Receipt.createEmptyRecipt(request.getProductName());
        }
        if(product.getCount() < request.getCountPurchased()){
            int buyCount = product.getCount();
            return buyProduct(product,buyCount,request);
        }
        return buyProduct(product,request.getCountPurchased(),request);
    }


    private Receipt tryBuyProductNonPromoted(PurchaseRequest request){
        boolean isPromoted = false;
        Product product = this.findOneOriginProduct(request.getProductName(),isPromoted);
        if(product == NOT_FOUND){
            return Receipt.createEmptyRecipt(request.getProductName());
        }
        if(product.getCount() < request.getCountPurchased()){
            ExceptionFactory.throwIllegalStateException(ExceptionType.INTERNAL_ERROR);
        }
        return buyProduct(product,request.getCountPurchased(),request);
    }

    private Receipt buyProduct(Product product, int buyCount, PurchaseRequest request){
        request.decreaseCount(buyCount);
        return product.buy(buyCount);
    }



}
