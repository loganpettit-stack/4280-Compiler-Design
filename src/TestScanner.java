/*
* Author: Logan Pettit
* Description: Runs the Scanner and outputs the returned tokens*/
import DataStructures.Token;
import ScannerFunctions.Scanner;

import java.util.ArrayList;

/*Runs scanner without parser*/
public class TestScanner {
    public void RunScanner(ArrayList<Character> characterArrayList){

        Scanner myScanner = new Scanner();
        Token token;

        System.out.println("\n");

        do {
            /* Call scanner output token*/
            token = myScanner.getToken(characterArrayList);
            System.out.println(token.toString());

        } while(!token.getTokenID().equals("EofTK"));

    }
}
