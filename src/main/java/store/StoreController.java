package store;

import java.util.ArrayList;
import java.util.List;
import store.product.Product;
import store.product.PurchaseRequest;
import store.product.Receipt;
import store.product.promotion.Promotion;
import store.product.promotion.PromotionResult;

public class StoreController {
    private StoreModel storeModel;
    private LoadModel loadModel;
    private StoreInputView inputView;
    private StoreOutputView outputView;

    public StoreController(){
        storeModel = new StoreModel();
        loadModel = new LoadModel();
        inputView = new StoreInputView();
        outputView = new StoreOutputView();
    }

    public void run(){
        init();
        do {
            List<Receipt> receipts = purchase();
            account(receipts);
        }while(checkRetry());

    }

    private void init(){
        List<Product> products = StoreModel.createProducts(loadModel.loadProductList());
        List<Promotion> promotions = StoreModel.createPromotions(loadModel.loadPromotionList());
        storeModel.initStore(products,promotions);
    }

    private List<Receipt> purchase(){
        outputView.welcomeConsumer(storeModel.getProducts());
        List<PurchaseRequest> purchaseRequests = inputView.readPurchaseProduct();
        checkPurchase(purchaseRequests);

        List<PromotionResult> promotionResults = storeModel.checkPromotionAvailable(purchaseRequests);
        purchaseRequests = inputView.readAnswerPurchaseChange(promotionResults);
        return storeModel.buyProducts(purchaseRequests);
    }

    private void checkPurchase(List<PurchaseRequest> purchaseRequests){
        List<StoreViewMessage> messageBox = storeModel.checkPurchaseRequests(purchaseRequests);
        while(!messageBox.isEmpty()){
            inputView.retryReadPurchaseProduct(messageBox);
            messageBox = storeModel.checkPurchaseRequests(purchaseRequests);
        }
    }

    private void account(List<Receipt> receipts){
        boolean isDisCount = inputView.readAnswerDiscountApply();
        int disCountPrice = 0;
        if(isDisCount){
            disCountPrice = storeModel.calculateDiscount(receipts);
        }

        outputView.printAccount(receipts,disCountPrice);
    }

    private boolean checkRetry(){

        return inputView.readAnswerContinuePurchase();
    }

}
