package store.utils;

public class ExceptionFactory {

    static final String OUTPUT_MESSAGE_HEAD = "[ERROR] ";

    public static void throwIllegalArgumentException(ExceptionType exceptionType, Exception e) throws IllegalArgumentException{
        printExceptionMessage(exceptionType);
        throw new IllegalArgumentException( exceptionType.getMessage(), e);
    }
    public static void throwIllegalArgumentException(ExceptionType exceptionType) throws IllegalArgumentException{
        printExceptionMessage(exceptionType);
        throw new IllegalArgumentException( exceptionType.getMessage());
    }

    public static void throwIllegalStateException(ExceptionType exceptionType,Exception e) throws IllegalArgumentException{
        printExceptionMessage(exceptionType);
        throw new IllegalStateException( exceptionType.getMessage(),e);
    }

    public static void throwIllegalStateException(ExceptionType exceptionType) throws IllegalArgumentException{
        printExceptionMessage(exceptionType);
        throw new IllegalStateException( exceptionType.getMessage());
    }

    protected static void printExceptionMessage(ExceptionType exceptionType){
        String outputMessage = ExceptionFactory.OUTPUT_MESSAGE_HEAD + exceptionType.getMessage();
        System.out.println(outputMessage);
    }
}