package store.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Transformer {

    static public String concatenateList(List<String> stringList, String delimiter){

        return String.join(delimiter,stringList);
    }

    static public String joinToString(List<Integer> numberList, String delimiter){


        List<String> stringNumbers = new ArrayList<>();

        for(int number : numberList){
            stringNumbers.add(Integer.toString(number));
        }

        return String.join(delimiter,stringNumbers);
    }


    static public int parsePositiveInt(String rawNumber){

        Validator.validatePositiveNumericString(rawNumber);

        return Integer.parseInt(rawNumber);
    }

    static public int parseNonNegativeNumber(String rawNumber){

        Validator.validateNonNegativeNumericString(rawNumber);

        return Integer.parseInt(rawNumber);
    }

    static public LocalDate parseLocalDate(String rawDate, DateTimeFormatter formatter){
        try {
            return LocalDate.parse(rawDate, formatter);
        }catch (DateTimeParseException exception){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_DATE_STRING, exception);
            throw new RuntimeException();
        }
    }

    static public LocalDateTime parseStartDate(String rawDate, DateTimeFormatter formatter){

        LocalDate date = Transformer.parseLocalDate(rawDate,formatter);

        try {
            return date.atStartOfDay();
        }catch (Exception exception){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_DATE_STRING, exception);
            throw new RuntimeException();
        }
    }

    static public LocalDateTime parseEndDate(String rawDate,DateTimeFormatter formatter){
        LocalDate date = Transformer.parseLocalDate(rawDate,formatter);
        int h = 23;
        int m = 59;
        int s = 59;
        try {
            return date.atTime(h,m,s);
        }catch (Exception exception){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_DATE_STRING, exception);
            throw new RuntimeException();
        }
    }

    static public String transformToThousandSeparated(int number){
        return String.format("%,d", number);
    }
}
