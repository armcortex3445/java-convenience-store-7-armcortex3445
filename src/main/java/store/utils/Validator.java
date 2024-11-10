package store.utils;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Validator {


    //related to numeric string
    public static void validateNumericString(String numericString){

        try{
            Integer.parseInt(numericString);
        }catch (Exception e){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_NUMERIC_STRING);
        }
    }

    public static void validateIntRange(String numericString){

        BigInteger bigIntStage = new BigInteger(numericString);
        BigInteger maxInt = BigInteger.valueOf(Integer.MAX_VALUE);
        BigInteger minInt = BigInteger.valueOf(Integer.MIN_VALUE);
        if(bigIntStage.compareTo(maxInt)>0
                || bigIntStage.compareTo(minInt) < 0){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.OUT_OF_RANGE_INT);
        }
    }

    public static void validatePositiveNumber(int number){
        if(number <= 0){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.NON_POSITIVE_NUMBER);
        }
    }

    // related to string
    public static void validateBlankString(String string){
        if(string.isBlank()){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.EMPTY_STRING);
        }

    }

    public static void validateStringEncoded(String string){
        try {
            byte[] utf8Bytes = string.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.DISABLE_ENCODED_TO_UTF8);
        }
    }

   public static void validateDivisible(int source, int divider){
        int rest = 0;
        if(source % divider != rest){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.NON_DIVISIBLE);
        }
   }

   public static void validateSpecificRange(int number , int startRange,int endRange){

        if(number > endRange || number < startRange){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.OUT_OF_SPECIFIC_RANGE);
        }
   }

   public static void validateListSize(List<Integer> list, int size){
        if(list.size() != size){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.NOT_PROPER_SIZE);
        }
   }

   public static void validateDuplicate(List<Integer> list){
           Set<Integer> set = new HashSet<>(list);

           if(set.size() != list.size()){
               ExceptionFactory.throwIllegalArgumentException(ExceptionType.DUPLICATED_ELEMENTS);
           }

   }
   public static void validatePositiveNumericString(String rawNumber){
        Validator.validateBlankString(rawNumber);
        Validator.validateNumericString(rawNumber);
        Validator.validateIntRange(rawNumber);
        int number = Integer.parseInt(rawNumber);
        Validator.validatePositiveNumber(number);
    }

    public static void validateFile(String filePath){
        try {
            File file = new File(filePath);
        }catch (Exception e){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.READ_FILE_FAIL);
        }
    }

    public static void validateFileSize(String filePath){
        final int maxSize = 1*1024*1024; // 1MB
        validateFile(filePath);

        File file = new File(filePath);
        if(file.length() > maxSize){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.TOO_LARGE_FILE);
        }
    }
    public static void validateEachLine(List<String> lines,String columnDelimiter,int columnCount){
        for(String line : lines){
            validateColumn(line,columnDelimiter,columnCount);
        }
    }

    public static void validateColumn(String promotion, String delimiter,int columnCount){
        List<String> columns = List.of(promotion.split(delimiter));
        if(columns.size() != columnCount){
            ExceptionFactory.throwIllegalArgumentException(ExceptionType.INVALID_FILE_FORMAT);
        }
        for(String column : columns){
            Validator.validateBlankString(column);
            Validator.validateStringEncoded(column);
        }
    }
}
