package com.akraness.akranesswaitlist.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.regex.Pattern;

@Component
public class Utility {
    private static Random random = new Random();
    public String generateEmailVeirificationCode(){
        random = new Random(System.currentTimeMillis());
        long code = ((1 + random.nextInt(2))*100000 + random.nextInt(100000));
        String ref = String.valueOf(code);
        if(ref.startsWith("-")){
            ref = ref.replace("-","");
            return ref;
        }
        return ref;
    }

    /**
     * validates if an input is valid using the specified regular expression
     * @param input the input to be validated
     * @param regex the regular expression to be used in validating the input
     * @return true if input is valid else false
     */
    public boolean isInputValid(String input, String regex){
        return Pattern.compile(regex)
                .matcher(input)
                .matches();
    }
}
