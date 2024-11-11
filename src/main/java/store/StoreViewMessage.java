package store;

public enum StoreViewMessage {

    WELCOME("안녕하세요. W편의점입니다.\n"
            + "현재 보유하고 있는 상품입니다."),
    PURCHASE_GUIDE("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])"),
    MEMBERSHIP_GUIDE("멤버십 할인을 받으시겠습니까? (Y/N)"),

    RECEIPT_HEAD("==============W 편의점================"),
    RECEIPT_HEAD_PROMOTION("=============증\t정==============="),
    RECEIPT_HEAD_ACCOUNT("===================================="),
    RETRY_PURCHASE("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)"),

    ERROR_INVALID_FORMAT("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");



    private final String message;

    static public final String PURCHASE_PATTERN = "\\[([a-zA-Z가-힣0-9]+)-(\\d+)]";
    static public final String PURCHASE_LIST_DELIMITER = ",";
    static public final String ANSWER_NO = "N";
    static public final String ANSWER_YES = "Y";

    private StoreViewMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void printMessage(){
        System.out.println(this.message);
    }

    public static void printReceiptFormat(String first, String second, String third){
        final int FIRST_WIDTH = 15;
        final int SECOND_WIDTH = 8;
        final int THIRD_WIDTH = 10;
        System.out.printf("%-" + FIRST_WIDTH + "s%-" + SECOND_WIDTH + "s%-" + THIRD_WIDTH + "s\n", first, second, third);
    }

    public static void printReceiptProductListHead(){
        final String productNameColumn = "상품명";
        final String countColumn = "수량";
        final String priceColumn = "금액";
        printReceiptFormat(productNameColumn,countColumn,priceColumn);
    }

    public static void printProduct(String ProductName, int price, int count, String promotionName){
        final String format = "%s %,d원 %d개 %s\n";
        System.out.printf(format,ProductName,price,count,promotionName);
    }

    public static void printInsufficientProduct(String ProductName, int price, String promotionName){
        final String format = "%s %,d원 재고 없음 %s\n";
        System.out.printf(format,ProductName,price,promotionName);
    }

    public static void printPromotionReturnAvailable(String productName){
        String format = "현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n";
        System.out.printf(format,productName);
    }

    public static void printPromotionWarning(String productName,int count) {
        String format = "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n";
        System.out.printf(format, productName, count);
    }
}
