package store.utils;

import java.util.ArrayList;
import java.util.List;

public class Transformer {

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

}
