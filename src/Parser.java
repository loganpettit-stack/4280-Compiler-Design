/*
* Author: Logan Pettit
* class: 4280 Compilers
* Description: Parser file takes tokens from the scanner and applies pre determined BNF in order to build a
* parse tree
* UPDATE: commented out uneeded tokens*/

import DataStructures.Token;
import ScannerFunctions.Scanner;

import java.util.ArrayList;

public class Parser {

    private Token token;
    private Scanner scanner = new Scanner();
    private ArrayList<Character> characterArrayList;

    private void error(String errorTk) {
        System.out.println("PARSER ERROR: " + errorTk + " but, received " +
                token.getTokenStr() + " at line: " + token.getLineNum());
        System.exit(-3);
    }

    public testTree starParser(ArrayList<Character> filteredArrayList){
        characterArrayList = filteredArrayList;

        /*Start constructing tree and return root*/
        testTree tree = new testTree();
        tree.root = program();

        return tree;
    }

    private TreeNode program(){
        TreeNode node = new TreeNode("Program");
        token = scanner.getToken(characterArrayList);

        /*if first token is var call vars*/
        if (token.getTokenID().equals("VarKwTK")) {

            node.childNodes.add(vars());
        }
        /*Otherwise if first token is block call block*/
        if(token.getTokenID().equals("StartKwTK")) {

            node.childNodes.add(block());

            /*Check if program block ends with eof*/
            if (token.getTokenID().equals("EofTK")) {
                System.out.println("Program parse success");

            }
            else {
                error("Expected EOF");
            }
        }
        else{
            error("Expected start keyword");
        }

        return node;
    }

    private TreeNode block() {
        TreeNode node = new TreeNode("Block");
        if (token.getTokenID().equals("StartKwTK")) {
            /*Add token to node, get next*/
            /*node.tokenArray.add(token);*/
            token = scanner.getToken(characterArrayList);

            /*next token should be vars which could be empty so call stats too
            * add both nonterminals to child nodes*/
            node.childNodes.add(vars());
            node.childNodes.add(stats());

            /*If we dont end with stop token error*/
            if (token.getTokenID().equals("StopKwTK")) {
               /* node.tokenArray.add(token);*/

                /*Call for eof token, block must end with eof*/
                token = scanner.getToken(characterArrayList);
            }
        }
        else{
            error("Expected start keyword");
        }

        return node;
    }

    private TreeNode vars(){
        TreeNode node = new TreeNode("Vars");

        /*var identifier : Integer <vars> or empty*/
        if(token.getTokenID().equals("VarKwTK")){
            /*Add token to array and get next */
//            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            if(token.getTokenID().equals("idTK")){
                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                if(token.getTokenID().equals("colonTK")){
//                    node.tokenArray.add(token);
                    token = scanner.getToken(characterArrayList);

                    if(token.getTokenID().equals("numTK")){
                        node.tokenArray.add(token);
                        token = scanner.getToken(characterArrayList);

                        /*Add vars as child*/
                        node.childNodes.add(vars());

                        return node;
                    }
                    else {
                        error("Expected Integer");
                    }
                }
                else {
                    error("Expected :");
                }
            }
            else{
                error("Expected Identifier");
            }
        }

        return null; /*could be null or node*/
    }

    private TreeNode stats(){
        TreeNode node = new TreeNode("Stats");

        /*<stat> ; <mStat>*/
        if (token.getTokenID().equals("InKwTK") ||
                token.getTokenID().equals("OutKwTK") ||
                token.getTokenID().equals("StartKwTK") ||
                token.getTokenID().equals("CondKwTK") ||
                token.getTokenID().equals("IterateKwTK") ||
                token.getTokenID().equals("idTK")) {

            node.childNodes.add(stat());

            /*if we get a semicolon call for mStat function for nonTerminal*/
            if(token.getTokenID().equals("semicolonTK")) {

                /*Add token to node and get next*/
//                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                node.childNodes.add(mStat());
            }
            else{
                error("Expected ;");
            }
        }
        else {
            error("Expected Statement Keyword, in, out, start, cond, iterate, or Identifier");
        }

        return node;
    }


    private TreeNode mStat(){
        TreeNode node = new TreeNode("mstat");
        /*No error in mstat since it is nullable*/
        if (token.getTokenID().equals("InKwTK") ||
                token.getTokenID().equals("OutKwTK") ||
                token.getTokenID().equals("StartKwTK") ||
                token.getTokenID().equals("CondKwTK") ||
                token.getTokenID().equals("IterateKwTK") ||
                token.getTokenID().equals("idTK")){

            node.childNodes.add(stat());

            /*successful stat should return semicolon*/
            if(token.getTokenID().equals("semicolonTK")){
                /*Add semicolon token and get next*/
//                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                node.childNodes.add(mStat());

                return node;
            }
            else {
                error("Expected ;");
            }
        }
        return null;
    }

    private TreeNode stat(){
        TreeNode node = new TreeNode("stat");

        /*Call all stat nonterminals*/
        if(token.getTokenID().equals("InKwTK")){
            node.childNodes.add(in());
        }
        else if(token.getTokenID().equals("OutKwTK")){
            node.childNodes.add(out());
        }
        else if(token.getTokenID().equals("StartKwTK")){
            node.childNodes.add(block());
        }
        else if(token.getTokenID().equals("CondKwTK")){
            node.childNodes.add(ifStmt());
        }
        else if(token.getTokenID().equals("IterateKwTK")){
            node.childNodes.add(loop());
        }
        else if(token.getTokenID().equals("idTK")){
            node.childNodes.add(assign());
        }
        else{
            error("Expected Statement Keyword, in, out, start, cond, iterate, Identifier");
        }

        return node;
    }

    private TreeNode in(){
        TreeNode node = new TreeNode("in");

        if(token.getTokenID().equals("InKwTK")){
//            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            if(token.getTokenID().equals("idTK")){

                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);
            }
            else {
                error("Expected Identifier");
            }
        }
        else {
            error("Expected Statement Keyword, in");
        }

        return node;
    }

    private TreeNode out(){
      TreeNode node = new TreeNode("out");

        if(token.getTokenID().equals("OutKwTK")){
            /*add out token and get next*/
//            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            node.childNodes.add(expr());
        }
        else {
            error("Expected Statement Keyword, out");
        }
        return node;
    }

    private TreeNode expr(){
        TreeNode node = new TreeNode("expr");

        /*Call nonterminal A, expr nonterminal becomes nullable*/
        node.childNodes.add(A());

        if (token.getTokenID().equals("sumTK")){
            /*add sum token and get next*/
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);
            node.childNodes.add(expr());
        }
        return node;
    }

    public TreeNode A(){
        TreeNode node = new TreeNode("A");
        /*Call nonterminal N, A becomes nullable*/
        node.childNodes.add(N());
        if(token.getTokenID().equals("minusTK")){
            /*Add minus token and get next*/
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);
            node.childNodes.add(A());
        }
        return node;
    }

    private TreeNode N(){
        TreeNode node = new TreeNode("N");

        /*Call nonterminal M, N becomes nullable*/
        node.childNodes.add(M());

        if(token.getTokenID().equals("divideTK")){
            /*add / and get next token*/
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);
            node.childNodes.add(N());
        }
        else if(token.getTokenID().equals("multiplyTK")) {
            /*add multiply token and get next*/
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);
            node.childNodes.add(N());
        }
        return node;
    }


    private TreeNode M(){
        TreeNode node = new TreeNode("M");

        if (token.getTokenID().equals("minusTK")){
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);
            node.childNodes.add(M());
        }
        else if(token.getTokenID().equals("LBracketTK")){
            node.childNodes.add(R());
        }
        else if(token.getTokenID().equals("idTK")){
            node.childNodes.add(R());
        }
        else if(token.getTokenID().equals("numTK")){
            node.childNodes.add(R());
        }
        else {
            error("Expected -, [, Identifier, or Integer");
        }
        return node;
    }

    private TreeNode R(){
        TreeNode node = new TreeNode("R");

        if(token.getTokenID().equals("LBracketTK")){
            /*add [ get next token*/
//            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);
            node.childNodes.add(expr());
             if(token.getTokenID().equals("RBracketTK")){
                 /*add [ get next token*/
                 node.tokenArray.add(token);
                 token = scanner.getToken(characterArrayList);

             }
             else {
                 error("Expected ]");
             }
        }
        else if(token.getTokenID().equals("idTK")){
            /*add id token get next*/
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

        }
        else if(token.getTokenID().equals("numTK")){
            /*add num token get next*/
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);
        }
        else {
            error("Expected [, Identifier, or Integer");
        }

        return node;
    }

    private TreeNode ifStmt(){
        TreeNode node = new TreeNode("ifstmt");

        if(token.getTokenID().equals("CondKwTK")){
//            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            if(token.getTokenID().equals("LParenthTK")){
//                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                if(token.getTokenID().equals("LParenthTK")){
//                    node.tokenArray.add(token);
                    token = scanner.getToken(characterArrayList);

                    node.childNodes.add(expr());
                    node.childNodes.add(RO());
                    node.childNodes.add(expr());

                    if(token.getTokenID().equals("RParenthTK")){
//                        node.tokenArray.add(token);
                        token = scanner.getToken(characterArrayList);

                        if(token.getTokenID().equals("RParenthTK")){
//                            node.tokenArray.add(token);
                            token = scanner.getToken(characterArrayList);
                            node.childNodes.add(stat());

                        }
                        else {
                            error("Expected )");
                        }
                    }
                    else {
                        error("Expected )");
                    }
                }
                else {
                    error("Expected (");
                }
            }
            else {
                error("Expected (");
            }
        }
        else {
            error("Expected Statement Keyword, cond");
        }

        return node;
    }

    private TreeNode loop(){
        TreeNode node = new TreeNode("loop");

        if(token.getTokenID().equals("IterateKwTK")){
//            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            if(token.getTokenID().equals("LParenthTK")) {
//                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                if (token.getTokenID().equals("LParenthTK")) {
//                    node.tokenArray.add(token);
                    token = scanner.getToken(characterArrayList);

                    node.childNodes.add(expr());
                    node.childNodes.add(RO());
                    node.childNodes.add(expr());

                    if(token.getTokenID().equals("RParenthTK")){
//                        node.tokenArray.add(token);
                        token = scanner.getToken(characterArrayList);

                        if(token.getTokenID().equals("RParenthTK")){
//                            node.tokenArray.add(token);
                            token = scanner.getToken(characterArrayList);
                            node.childNodes.add(stat());
                        }
                        else {
                            error("Expected )");
                        }
                    }
                    else {
                        error("Expected )");
                    }
                }
                else {
                    error("Expected (");
                }
            }
            else {
                error("Expected (");
            }
        }
        else {
            error("Expected Statement Keyword, iterate");
        }
        return node;
    }

    private TreeNode assign(){
        TreeNode node = new TreeNode("assign");
        if(token.getTokenID().equals("idTK")){
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            if(token.getTokenID().equals("lessThanTK")){
//                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                if(token.getTokenID().equals("lessThanTK")){
//                    node.tokenArray.add(token);
                    token = scanner.getToken(characterArrayList);
                    node.childNodes.add(expr());
                }
                else {
                    error("Expected <");
                }
            }
            else {
                error("Expected <");
            }
        }
        else {
            error("Expected Identifier");
        }

        return node;
    }

    private TreeNode RO(){
        TreeNode node = new TreeNode("RO");
        /*if less than token consume*/
        if(token.getTokenID().equals("lessThanTK")){
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            /*if second less than token comes in consume */
            if(token.getTokenID().equals("lessThanTK")){
                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                return node;
            }
            /*if next token is greater than consume*/
            else if(token.getTokenID().equals("greaterThankTK")){
                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                return node;
            }
            else {
                return node;
            }
        }
        else if(token.getTokenID().equals("greaterThanTK")){
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            if(token.getTokenID().equals("greaterThanTK")){
                node.tokenArray.add(token);
                token = scanner.getToken(characterArrayList);

                return node;
            }
            else {
                return node;
            }

        }
        else if(token.getTokenID().equals("assignTK")) {
            node.tokenArray.add(token);
            token = scanner.getToken(characterArrayList);

            return node;
        }
        else {
            error("Expected < or >");
        }
        return null;
    }
}
