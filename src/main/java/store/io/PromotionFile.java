package store.io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum PromotionFile {
    NAME(0,"name",String.class),
    BUY(1,"buy",Integer.class ),
    GET(2,"get", Integer.class),
    START_DATE(3,"start_date",LocalDateTime.class),
    END_DATE(4,"end_date",LocalDateTime.class);

    private int columnIdx;
    private String columName;
    private Class<?> type;

    static final public DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private PromotionFile(int colunmIdx, String columName, Class<?> type){
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
