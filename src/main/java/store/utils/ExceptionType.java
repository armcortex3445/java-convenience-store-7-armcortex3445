package store.utils;

public enum ExceptionType {
    LIST_OVER_MAX_LENGTH("list is over mat length."),
    INVALID_NUMERIC_STRING("String is not numeric."),
    OUT_OF_RANGE_INT("number is out of int type rage"),
    NON_POSITIVE_NUMBER("number is not positive number"),
    NON_DIVISIBLE("values are not divisible relation"),
    OUT_OF_SPECIFIC_RANGE("out of specific range"),

    //Number list  related
    NOT_PROPER_SIZE("list size is not proper"),
    DUPLICATED_ELEMENTS("list includes duplicated elements"),

    //STRING related
    EMPTY_STRING("string is empty"),
    DISABLE_ENCODED_TO_UTF8("string is not able to be encoded by utf 8"),
    INVALID_INPUT_STRING_FORMAT("input string format is invalid"),
    INVALID_NAME_FORMAT("name should be composed by english, korean and number"),

    //Model
    INTERNAL_ERROR("Internal logic error"),

    //Promotion
    INVALID_RETURN_PROMOTION("return should be 1"),

    //File or I/O
    READ_FILE_FAIL("can't read the file"),
    TOO_LARGE_FILE("File is too large to process"),
    EMPTY_FILE("File is empty"),
    INVALID_FILE_FORMAT("File format is invalid"),

    //LocalDate
    INVALID_DATE_STRING("String is not able to pared to LocalDate/LocalDateTime");

    private final String message;

    private ExceptionType(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

}
