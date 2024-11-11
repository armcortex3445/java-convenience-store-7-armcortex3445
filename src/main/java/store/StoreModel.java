package store;

import camp.nextstep.edu.missionutils.DateTimes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import store.io.ProductFile;
import store.io.PromotionFile;
import store.product.Product;
import store.product.PurchaseRequest;
import store.product.Receipt;
import store.product.promotion.Promotion;
import store.product.promotion.PromotionResult;
import store.product.promotion.PromotionState;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;
import store.utils.Transformer;

public class StoreModel {
    static private final  Product NOT_FOUND = null;
    private final List<Product> productRepository;
    private final HashMap<String,Promotion> promotionHashMap;
    final static String NAME_REGULAR_EXPRESSION = "^[a-zA-Z가-힣0-9]+$";
    final static Pattern NAME_PATTERN = Pattern.compile(StoreModel.NAME_REGULAR_EXPRESSION);

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
    *   - promotion과 product 생성시 예외처리 고려
    *       - 잘못된 타입 입력
    *       - 잘못된 형식 입력
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
        validateNameFormat(name);
        if(promotion.equals("null")){
            promotion = null;
        }
        return new Product(name, Transformer.parsePositiveInt(price),Transformer.parseNonNegativeNumber(quantity),promotion);
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
    static public void validateNameFormat(String name){
        Matcher matcher = NAME_PATTERN.matcher(name);
        if(!matcher.matches()){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_NAME_FORMAT);
        }
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

    public List<StoreViewMessage> checkPurchaseRequests(List<PurchaseRequest> requests) {
        List<StoreViewMessage> messageBox = new ArrayList<>();

        for(PurchaseRequest request : requests){
            checkPurchaseRequest(request,messageBox);
        }

        return messageBox;
    }

    public void checkPurchaseRequest(PurchaseRequest request, List<StoreViewMessage> messageBox){
        checkPurchaseProductExist(request,messageBox);
        checkPurchaseProductCount(request,messageBox);

    }

    public void checkPurchaseProductExist(PurchaseRequest request, List<StoreViewMessage> messageBox){
        List<Product> finds = findProduct(request.getProductName());
        if(finds.isEmpty()){
            messageBox.add(StoreViewMessage.ERROR_NOT_EXIST_PRODUCT);
        }
    }

    public void checkPurchaseProductCount(PurchaseRequest request, List<StoreViewMessage> messageBox){
        List<Product> finds = findProduct(request.getProductName());
        int zero = 0;
        int totalCount = finds.stream().map(Product::getCount).reduce(zero,Integer::sum);
        if(totalCount == zero){
            messageBox.add(StoreViewMessage.ERROR_NO_COUNT_PRODUCT);
        }
        if(totalCount != zero && totalCount < request.getCountPurchased()){
            messageBox.add(StoreViewMessage.ERROR_OVERFLOW_PURCHASE);
        }
    }

    public List<PromotionResult> checkPromotionAvailable(List<PurchaseRequest> purchaseRequests){
        List<PromotionResult> promotionResults = new ArrayList<>();
        for(PurchaseRequest request : purchaseRequests){
            promotionResults.add(checkProductPromotionAvailable(request.getProductName(),request.getCountPurchased()));
        }

        return promotionResults;
    }

    public PromotionResult checkProductPromotionAvailable(String productName, int buyCount){
        Product product = findProductPromoted(productName);
        if(findProductPromoted(productName) == NOT_FOUND){
            return PromotionResult.createNoPromotion(productName,buyCount);
        }
        if(product.isEnoughToBuy(buyCount)){
            return product.estimatePromotionResult(buyCount, DateTimes.now());
        }
        PromotionResult result = product.estimatePromotionResult(product.calculateMaxCountToBuy(buyCount),DateTimes.now());
        return result.transitState(PromotionState.INSUFFICIENT);
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

    public int calculateDiscount(List<Receipt> receipts){
        /*TODO
        *   - 합계산 도중 오버플로우 점검 필요*/
        final int maxDisCount = 8000;
        final double disCountRate = 0.3;
        int totalNonPromotedPrice = 0;
        for(Receipt receipt : receipts){
            totalNonPromotedPrice += receipt.getNonPromotedPrice();
        }
        int disCountPrice = (int)Math.round(disCountRate * totalNonPromotedPrice);
        return Math.min(maxDisCount, disCountPrice);
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
            return Receipt.createEmptyReceipt(request.getProductName());
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
            return Receipt.createEmptyReceipt(request.getProductName());
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
