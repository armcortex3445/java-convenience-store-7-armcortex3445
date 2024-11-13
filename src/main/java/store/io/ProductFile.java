package store.io;

public enum ProductFile {
    NAME(0,"name",String.class),
    PRICE(1,"price",Integer.class),
    QUANTITY(2,"quantitiy",Integer.class),
    PROMOTION(3,"promotion",String.class);

    private int columnIdx;
    private String columName;
    private Class<?> type;

    static final public String COLUMN_DELIMITER = ",";

    private ProductFile(int colunmIdx, String columName, Class<?> type){
        this.columName = columName;
        this.columnIdx = colunmIdx;
        this.type = type;
    }

    public int getColumnIdx(){
        return this.columnIdx;
    }

    public String getColumName(){
        return this.columName;
    }

    public Class<?> getType(){
        return this.type;
    }
}
