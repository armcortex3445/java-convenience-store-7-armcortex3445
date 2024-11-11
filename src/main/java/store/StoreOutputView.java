package store;

import java.util.List;
import store.product.Product;
import store.product.Receipt;

public class StoreOutputView {

    public void welcomeConsumer(List<Product> products){
        StoreViewMessage.WELCOME.printMessage();
        for(Product product : products){
            printSellingProduct(product);
        }
    }

    public void printAccount(List<Receipt> receipts, int disCountPrice){
        StoreViewMessage.RECEIPT_HEAD.printMessage();
        printPurchaseProductList(receipts);
        printPromotionReturnProductList(receipts);
        printBill(receipts,disCountPrice);
    }

    public void printSellingProduct(Product product){
        String promotionName = "";
        if(product.isPromotionExist()){
            promotionName = product.getPromotionName();
        }

        if(product.isInsufficient()){
            StoreViewMessage.printInsufficientProduct(product.getName(),product.getPrice(),promotionName);
            return;
        }
        StoreViewMessage.printProduct(product.getName(),product.getPrice(),product.getCount(),promotionName);
    }


    public void printPurchaseProductList(List<Receipt> receipts){

        StoreViewMessage.printReceiptProductListHead();
        for(Receipt receipt : receipts){
            StoreViewMessage.printReceiptFormat(receipt.getProductName(), Integer.toString(receipt.getActualCount()),Integer.toString(receipt.getActualPrice()));
        }

    }

    public void printPromotionReturnProductList(List<Receipt> receipts){
        StoreViewMessage.RECEIPT_HEAD_PROMOTION.printMessage();
        String emtpy = "";
        List<Receipt> promotionReceipts = receipts.stream().filter((Receipt::isPromotionApplied)).toList();
        for(Receipt receipt : promotionReceipts){
            StoreViewMessage.printReceiptFormat(receipt.getProductName(),Integer.toString(receipt.getActualCount()),emtpy);
        }
    }

    public void printBill(List<Receipt> receipts, int disCountPrice){
        StoreViewMessage.RECEIPT_HEAD_ACCOUNT.printMessage();
        List<Integer> accounts = calculateTotalReceipt(receipts);
        int totalCountColumn = 0;
        int promotedPriceColumn = 1;
        int totalActualPrice = 2;

        printBillMessage(accounts.get(totalCountColumn),accounts.get(promotedPriceColumn),accounts.get(totalActualPrice),disCountPrice);
    }

    public void printBillMessage(int totalCount, int promotedPrice, int totalActualPrice, int disCountPrice){
        String empty = "";
        String totalActualPriceRow = "총구매액";
        String promotedDisCountRow = "행사할인";
        String membershipRow = "멤버십할인";
        String totalPriceRow = "내실돈";
        StoreViewMessage.printReceiptFormat(totalActualPriceRow,Integer.toString(totalCount),Integer.toString(totalActualPrice));
        StoreViewMessage.printReceiptFormat(promotedDisCountRow,empty,"-"+ promotedPrice);
        StoreViewMessage.printReceiptFormat(membershipRow,empty,"-"+ disCountPrice);
        StoreViewMessage.printReceiptFormat(totalPriceRow,empty,Integer.toString(totalActualPrice - promotedPrice - disCountPrice));
    }

    public List<Integer> calculateTotalReceipt(List<Receipt> receipts){
        int totalCount = 0;
        int promotedPrice = 0;
        int totalActualPrice = 0;
        for(Receipt receipt : receipts){
            totalCount += receipt.getActualCount();
            promotedPrice += receipt.getDisCountPrice();
            totalActualPrice += receipt.getActualPrice();
        }

        return List.of(totalCount,promotedPrice,totalActualPrice) ;
    }

}
