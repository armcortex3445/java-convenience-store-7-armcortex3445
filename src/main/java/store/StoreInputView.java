package store;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import camp.nextstep.edu.missionutils.Console;
import store.product.PurchaseRequest;
import store.product.promotion.PromotionResult;
import store.product.promotion.PromotionState;
import store.utils.ExceptionFactory;
import store.utils.ExceptionType;
import store.utils.Transformer;
import store.utils.Validator;

public class StoreInputView {
    static final Pattern PURCHASE_PATTERN = Pattern.compile(StoreViewMessage.PURCHASE_PATTERN);

    public List<PurchaseRequest> readPurchaseProduct(){
        StoreViewMessage.PURCHASE_GUIDE.printMessage();
        String purchaseList = Console.readLine();
        try {
            return createPurchaseRequests(purchaseList);
        }catch (IllegalArgumentException exception){
            StoreViewMessage.ERROR_INVALID_FORMAT.printMessage();
            return readPurchaseProduct();
        }
    }

    public List<PurchaseRequest> retryReadPurchaseProduct(List<StoreViewMessage> errorMessages){
        for(StoreViewMessage message : errorMessages){
            message.printMessage();
        }
        return readPurchaseProduct();
    }

    public List<PurchaseRequest> readAnswerPurchaseChange(List<PromotionResult> promotionResults){
        List<PurchaseRequest> newPurchaseRequest = new ArrayList<>();
        for(PromotionResult promotionResult : promotionResults){
            newPurchaseRequest.add(handlePromotionResults(promotionResult));
        }

        return newPurchaseRequest;
    }

    public PurchaseRequest handlePromotionResults(PromotionResult promotionResult){
        if(promotionResult.getState().equals(PromotionState.OMISSION)) {
            return readPromotionEnableProduct(promotionResult);
        }
        if(promotionResult.getState().equals(PromotionState.INSUFFICIENT)){
            return readPromotionInsufficient(promotionResult);
        }
        return new PurchaseRequest(promotionResult.getProductName(),promotionResult.getTotalItemCount());
    }

    public PurchaseRequest readPromotionInsufficient(PromotionResult promotionResult){
        PurchaseRequest newRequest = new PurchaseRequest(promotionResult.getProductName(),promotionResult.getTotalItemCount());
        String answer =readAnswerPromotionInSufficient(promotionResult);

        if(answer.equals(StoreViewMessage.ANSWER_NO)){
            newRequest.decreaseCount(promotionResult.getNonPromotedCount());
        }

        return newRequest;
    }

    public boolean readAnswerContinuePurchase(){
        StoreViewMessage.RETRY_PURCHASE.printMessage();
        String answer = Console.readLine();
        try{
            validateAnswer(answer);
            return answer.equals(StoreViewMessage.ANSWER_YES);
        }catch (IllegalArgumentException exception){
            StoreViewMessage.ERROR_INVALID_FORMAT.printMessage();
            return readAnswerContinuePurchase();
        }
    }

    public boolean readAnswerDiscountApply(){
        StoreViewMessage.MEMBERSHIP_GUIDE.printMessage();
        String answer = Console.readLine();
        try{
            validateAnswer(answer);
            return answer.equals(StoreViewMessage.ANSWER_YES);
        }catch (IllegalArgumentException exception){
            StoreViewMessage.ERROR_INVALID_FORMAT.printMessage();
            return readAnswerDiscountApply();
        }
    }


    public PurchaseRequest readPromotionEnableProduct(PromotionResult promotionResult){
        PurchaseRequest newRequest = new PurchaseRequest(promotionResult.getProductName(),promotionResult.getTotalItemCount());
        for(int tryCount = 0; tryCount < promotionResult.getOmittedItemCount(); tryCount++){
            String answer = readAnswerPromotionEnable(promotionResult);
            newRequest.tryToIncrease(answer.equals(StoreViewMessage.ANSWER_YES));
        }

        return newRequest;
    }

    public String readAnswerPromotionInSufficient(PromotionResult promotionResult){
        StoreViewMessage.printPromotionWarning(promotionResult.getProductName(),promotionResult.getNonPromotedCount());
        String answer = Console.readLine();
        try{
            validateAnswer(answer);
            return answer;
        }catch (IllegalArgumentException exception){
            StoreViewMessage.ERROR_INVALID_FORMAT.printMessage();
            return readAnswerPromotionInSufficient(promotionResult);
        }
    }

    public String readAnswerPromotionEnable(PromotionResult promotionResult){
        StoreViewMessage.printPromotionReturnAvailable(promotionResult.getProductName());
        String answer = Console.readLine();
        try{
            validateAnswer(answer);
            return answer;
        }catch (IllegalArgumentException exception){
            StoreViewMessage.ERROR_INVALID_FORMAT.printMessage();
            return readAnswerPromotionEnable(promotionResult);
        }
    }

    public List<PurchaseRequest> createPurchaseRequests(String purchaseList){
        validatePurchaseList(purchaseList);
        List<String> purchases = List.of(purchaseList.split(StoreViewMessage.PURCHASE_LIST_DELIMITER));
        return purchases.stream().map((purchase)->{
            List<String> elements = extractElementsFromPurchase(purchase);
            int nameIdx = 0;
            int countIdx = 1;
            return new PurchaseRequest(elements.get(nameIdx), Transformer.parsePositiveInt(elements.get(countIdx)));
        }).toList();
    }

    public PurchaseRequest createPurchaseRequest(String answer , String productName, int count){
        validateAnswer(answer);
        if(answer.equals(StoreViewMessage.ANSWER_NO)){
            int noBuy = 0;
            return new PurchaseRequest(productName,noBuy);
        }
        return new PurchaseRequest(productName,count);
    }

    static public void validatePurchaseList(String purchaseList){
        Validator.validateBlankString(purchaseList);
        List<String> purchases = List.of(purchaseList.split(StoreViewMessage.PURCHASE_LIST_DELIMITER));

        for(String purchase : purchases){
            validatePurchase(purchase);
        }

    }

    static public void validatePurchase(String purchase){
        /*TODO
        *  - 입력된 제품 이름이 한글인지 검증 필요. 
        *    - 해당 부분은 다른 클래스의 로직에서도 필요하므로, Validator에 로직을 추가하기*/
        if(!purchase.matches(StoreViewMessage.PURCHASE_PATTERN)){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_INPUT_STRING_FORMAT);
        }
        try {
            validatePurchaseElement(purchase);
        }catch (Exception e){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_INPUT_STRING_FORMAT);
        }

    }

    static public void validatePurchaseElement(String purchase){
        Matcher matcher = PURCHASE_PATTERN.matcher(purchase);
        if(matcher.matches()) {
            int productNameGroup = 1;
            int countGroup = 2;
            Validator.validateBlankString(matcher.group(productNameGroup));
            Validator.validatePositiveNumericString(matcher.group(countGroup));
            return;
        }
        ExceptionFactory.throwIllegalStateException(ExceptionType.INVALID_INPUT_STRING_FORMAT);


    }

    static public List<String> extractElementsFromPurchase(String purchase){
        Matcher matcher = PURCHASE_PATTERN.matcher(purchase);
        if(matcher.matches()){
            int nameGroup = 1;
            int countGroup = 2;
            return List.of(matcher.group(nameGroup), matcher.group(countGroup));
        }
        ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_INPUT_STRING_FORMAT);
        throw new RuntimeException();
    }

    static public void validateAnswer(String answer){
        Validator.validateBlankString(answer);
        if(!(answer.equals(StoreViewMessage.ANSWER_NO)
            || answer.equals(StoreViewMessage.ANSWER_YES))) {
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_INPUT_STRING_FORMAT);
        }
    }

}
