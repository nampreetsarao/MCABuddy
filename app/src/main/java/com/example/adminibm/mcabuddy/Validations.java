package com.example.adminibm.mcabuddy;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ADMINIBM on 4/16/2016.
 */
public class Validations {

    // Regular Expression
    // you can change the expression based on your need
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{3}-\\d{7}";
    private static final String ONLY_TEXT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";


    
    // Error Messages
    private static final String REQUIRED_MSG = "Required";
    private static final String COMPARE_MSG = "Password mismatch";
    private static final String EMAIL_MSG = "Invalid email";
    private static final String PHONE_MSG = "###-#######";
    private static final String ONLY_TEXT_MSG = "Only text allowed";


    //call this method when you need to check only text validation
    public static boolean isOnlyText(EditText edittext, boolean required){
        edittext.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        if(source.equals("")){
                            return source;
                        }
                        if(source.toString().matches("[a-zA-Z ]+")){
                            return source;
                        }
                        return "";
                    }
                }
        });

        return true;
    }

    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
    }

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        };

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }
        return true;
    }

    public static boolean compareText(EditText pwdtext1, EditText conpwdtext2){
        String text1 = pwdtext1.getText().toString().trim();
        String text2 = conpwdtext2.getText().toString().trim();
        //pwdtext1.setError(null);
        conpwdtext2.setError(null);

        //If text1 == text2 means confirm password and password match
        if(!text2.equals(text1)){
            conpwdtext2.setError(COMPARE_MSG);
            return false;
        }
        return true;

    }
}
