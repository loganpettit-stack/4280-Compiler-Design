/*
* Author: Logan Pettit
* Description: Scanner that takes an array of characters and returns a token object once one is found. Token objects
* are determined by the FSA table to determine the states */

package ScannerFunctions;

import DataStructures.Token;

import java.util.ArrayList;
import java.util.Hashtable;

public class Scanner {

    private int index = 0;

    private int lineNumber = 1;


    private int FAtable [][] =  {

/*columns { lowercase letter, uppercaseletter, number, +, ws, =, <, >, :, -, *, /, %, ., (, ), ',', {, }, ;, [, ],EOF, \n, invalid char}*/
            {1, -2, 2, 3, 0, 4, 5, 6, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 1025, 0, -2}, /* initial starting state */
            {1, 1, 1, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001, -2}, /*represents possible ids*/
            {1002, 1002, 2, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, 1002, -2}, /*represents possible number token*/
            {1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, 1003, -2}, /* + state */
            {1005, 1005, 1005, 1005, 1005, 9, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, 1005, -2}, /* = state can check == */
            {1006, 1006, 1006, 1006, 1006, 7, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, 1006, -2}, /* < state can check <= */
            {1007, 1007, 1007, 1007, 1007, 8, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, 1007, -2}, // > state can check >=
            {1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, 1008, -2}, /* <= state */
            {1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, 1009, -2}, /* >= state */
            {1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, 1010, -2}, /* == state */
            {1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, 1011, -2}, /* : state */
            {1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, 1012, -2}, /* - state */
            {1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, 1013, -2}, /* * state */
            {1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, 1014, -2}, /* / state */
            {1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, 1015, -2}, /* % state */
            {1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, 1016, -2}, /* . state */
            {1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, 1017, -2}, /* ( state */
            {1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, 1018, -2}, /* ) state */
            {1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, 1019, -2}, /* , state */
            {1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, 1020, -2}, /* { state */
            {1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, 1021, -2}, /* } state */
            {1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, 1022, -2}, /* ; state */
            {1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, 1023, -2}, /* [ state */
            {1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, -2}, /* ] state */
    };

    private Hashtable<Integer, String> h = new Hashtable<>();

    /*Hash Table for tokens*/
    public void fillTable(){
        this.h.put(1001, "idTK");
        this.h.put(1002, "numTK");
        this.h.put(1003, "sumTK");
        this.h.put(1005, "assignTK");
        this.h.put(1006, "lessThanTK");
        this.h.put(1007, "greaterThanTK");
        this.h.put(1008, "lessEqualTK");
        this.h.put(1009, "greaterEqualTK");
        this.h.put(1010, "compareEqualTK");
        this.h.put(1011, "colonTK");
        this.h.put(1012, "minusTK");
        this.h.put(1013, "multiplyTK");
        this.h.put(1014, "divideTK");
        this.h.put(1015, "modulusTK");
        this.h.put(1016, "periodTK");
        this.h.put(1017, "LParenthTK");
        this.h.put(1018, "RParenthTK");
        this.h.put(1019, "commaTK");
        this.h.put(1020, "LBraceTK");
        this.h.put(1021, "RBraceTK");
        this.h.put(1022, "semicolonTK");
        this.h.put(1023, "LBracketTK");
        this.h.put(1024, "RBracketTK");
        this.h.put(1025, "EofTK");
        this.h.put(-2, "Invalid Character");
    }

    public Token getToken(ArrayList<Character> characterArrayList) {
        fillTable(); /* populate hash table */

        KeywordCheck keywordCheck = new KeywordCheck();
        Token tokenObj = new Token();
        StringBuilder builder = new StringBuilder();

        int currentState = 0; /* set initial state */
        int columnNum;
        char character = ' ';
        boolean flag = true;

        while (flag) {

            character = characterArrayList.get(index);

            columnNum = getColumnNumber(character);
            currentState = FAtable[currentState][columnNum];

            /* if current state is final state break
            /* loop and return toke */
            if (currentState > 1000) {
//                index += 1;
                flag = false;
            }
            /* if id error, or invalid character*/
            else  if (currentState == -2){
                System.out.println("SCANNER ERROR: " + h.get(currentState) + ". Line number: " + lineNumber);
                System.exit(-2);
            }
            /* otherwise gather multi character string */
            /* and increment the index */
            else {
                if(character != ' ') {
                    builder.append(character);
                }
                index += 1;
            }
        }



        /* check line for newline characters */
        for (int i = 0; i < builder.length(); i++){

            /* if newline character detected increment line count */
            if (builder.charAt(0) == '\n') {
                builder.delete(0, 1);
                lineNumber += 1;
            }
        }

        tokenObj.setLineNum(lineNumber);

        /* if current character is new line character increment */
        if (character == '\n'){
            index += 1;
            lineNumber += 1;
        }



        tokenObj.setTokenID(h.get(currentState));

        if (tokenObj.getTokenID().equals("EofTK")){
            tokenObj.setTokenStr("EOF");
        }
        else {
            tokenObj.setTokenStr(builder.toString());
        }

        /*Check if the string matches keyword*/
        tokenObj = keywordCheck.checkKeyword(tokenObj);

        return tokenObj;
    }


    private int getColumnNumber(char character){
        int charValue;

        /* If lower case character return 0 state */
        if (Character.isLowerCase(character)){
            charValue = 0;
            return  charValue;
        }
        /* If upper case */
        else if(Character.isUpperCase(character)){
            charValue = 1;
            return charValue;
        }
        else if(Character.isDigit(character)){
            charValue = 2;
            return charValue;
        }
        else {
            switch (character) {
                case '+' :
                    charValue = 3;
                    return charValue;
                case ' ':
                    charValue = 4;
                    return charValue;
                case '=':
                    charValue = 5;
                    return charValue;
                case '<':
                    charValue = 6;
                    return charValue;
                case '>':
                    charValue = 7;
                    return charValue;
                case ':':
                    charValue = 8;
                    return charValue;
                case '-':
                    charValue = 9;
                    return charValue;
                case '*':
                    charValue = 10;
                    return charValue;
                case '/':
                    charValue = 11;
                    return charValue;
                case '%':
                    charValue = 12;
                    return charValue;
                case '.':
                    charValue = 13;
                    return charValue;
                case '(':
                    charValue = 14;
                    return charValue;
                case ')':
                    charValue = 15;
                    return charValue;
                case ',':
                    charValue = 16;
                    return charValue;
                case '{':
                    charValue = 17;
                    return charValue;
                case '}':
                    charValue = 18;
                    return charValue;
                case ';':
                    charValue = 19;
                    return charValue;
                case '[':
                    charValue = 20;
                    return charValue;
                case ']':
                    charValue = 21;
                    return charValue;
                case (char)0x1b:
                    charValue = 22;
                    return charValue;
                case '\n':
                    charValue = 23;
                    return charValue;
                default:
                    charValue = 24;
                    return charValue;
            }
        }
    }
}
