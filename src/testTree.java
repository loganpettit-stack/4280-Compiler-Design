/*
* Author: Logan Pettit
* Class: 4280 Compilers, Professor Jankiow
* Description: This file tests the tree built having the ability to display the tree in preorder with node
* depth spaces added to each line and, check the static semantics of the tree. This file assures the tree is
* built properly and all the variables initialized and called correctly. The compiler supports global and local
* variables, where local variables can use predefined global variables, or local variables defined directly
* outside of its scope, it can also overwrite variables directly outside of its scope*/

import DataStructures.Token;

import java.util.Stack;

public class testTree {

    public TreeNode root;
    private VariableStack variableStack = new VariableStack();
    private int varCounter;
    private Stack<Integer> scopeCountStack = new Stack<>();
    private int scopeCount;

    public void printPreorder() {
        int depth = 0;
        printPreorder(root, depth);
    }

    public void checkTree() {
        TraverseTreePreorder(root);

        System.out.println("Static semantic check successful.");
    }

    /* Given a binary tree, print its nodes in preorder with depth spaces added*/
    private void printPreorder(TreeNode node, int depth) {
        String str = addSpaces(depth);
        depth = depth + 1;

        if (node == null)
            return;

        /* first print data of node */
        System.out.println(str + node.toString() + " " + (depth - 1));

        /*print subtrees*/
        for (int i = 0; i < node.childNodes.size(); i++) {
            printPreorder(node.childNodes.get(i), depth);
        }

    }

    private void TraverseTreePreorder(TreeNode node){

        if (node == null) {
            return;
        }
        /*Program block handles global vars*/
        else if (node.nonterminal.equals("Program")) {
            scopeCount = 0;
            scopeCountStack.add(scopeCount);

            /*Traverse nodes to find global vars*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                TraverseTreePreorder(node.childNodes.get(i));
            }
        }
        /*Came to block*/
        else if (node.nonterminal.equals("Block")) {

            /*Save endpoint of last scope by adding the
            * amount of vars in last scope to stack
            * reset the var counter */
            scopeCount += varCounter;
            scopeCountStack.add(scopeCount);
            int prevVars = varCounter;
            varCounter = 0;

            /*Traverse nodes to find block vars*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                TraverseTreePreorder(node.childNodes.get(i));
            }

            /*pop local block vars befoe exiting block*/
            for (int i = 0; i < varCounter; i++) {
                variableStack.pop();
            }

            /*Pop scope from scope counting stack*/
            scopeCountStack.pop();

            varCounter = prevVars;
        }

        /* if node name is vars find token in it that has idTk and
         * increment the variable counter */
        else if (node.nonterminal.equals("Vars")) {

            /*Get nodes idtk and and find id variable*/
            Token tk = idTkFind(node);

            if(tk == null){
                return;
            }

            String var = tk.getTokenStr();

            if (!variableStack.VerifyVarExistance(var, scopeCountStack.get(scopeCountStack.size() - 1))) {
                variableStack.push(tk);
                varCounter += 1;

                /*Look for more var nodes*/
                for (int i = 0; i < node.childNodes.size(); i++) {
                    TraverseTreePreorder(node.childNodes.get(i));
                }
            }
            else {
                System.out.println("Error: Variable " + var + " at line " + tk.getLineNum() + " already defined in this scope");
                System.exit(-10);
            }

            /* System.out.println("Initializing variable: " + var + " at line: " + tk.getLineNum()); */
        }
        else if (node.nonterminal.equals("in")) {

            /*Get id token and variable from token node
            * find variable stack position */
            Token tk = idTkFind(node);

            if(tk == null){
                return;
            }

            String var = tk.getTokenStr();

            int stackpos = variableStack.Find(var);

            if (stackpos < 0) {
                System.out.println("Error: Variable " + var + " not defined in this scope at line: " +  tk.getLineNum());
                System.exit(-10);
            }
            else {
               /* System.out.println(var + " found at line: " + tk.getLineNum()) */
                for (int i = 0; i < node.childNodes.size(); i++) {
                    TraverseTreePreorder(node.childNodes.get(i));
                }
            }
        }
        else if (node.nonterminal.equals("R")){

            /*Get id token and variable from token node
             * find variable stack position */
            Token tk = idTkFind(node);

            if(tk == null){
                return;
            }

            String var = tk.getTokenStr();
            int stackpos = variableStack.Find(var);

            if(stackpos < 0 && !node.nonterminal.equals("expr") && !node.nonterminal.equals("Integer")) {
                System.out.println("Error: Variable " + var + " not defined in this scope at line: " + tk.getLineNum());
                System.exit(-10);
            }
            else {
               /*  System.out.println("out var found: " + var + " at line: " + tk.getLineNum()); */
                for (int i = 0; i < node.childNodes.size(); i++) {
                    TraverseTreePreorder(node.childNodes.get(i));
                }
            }
        }
        else if (node.nonterminal.equals("assign")) {

            /*Get id token and variable from token node
             * find variable stack position */
            Token tk = idTkFind(node);
            if(tk == null){
                return;
            }

            String var = tk.getTokenStr();
            int stackpos = variableStack.Find(var);

            /*if the stack poistion is less than zer var was not found*/
            if (stackpos < 0) {
                System.out.println("Error: Variable " + var + " not defined in this scope at line: " + tk.getLineNum());
                System.exit(-10);
            }
            else {
               /* System.out.println(var + " found assign at line: " + tk.getLineNum()); */
                for (int i = 0; i < node.childNodes.size(); i++) {
                    TraverseTreePreorder(node.childNodes.get(i));
                }
            }
        }
        else {
            /*Traverse childNodes if not at one of the specified nodes*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                TraverseTreePreorder(node.childNodes.get(i));
            }
        }
    }

    /*Find token in with Id in tokens tokenArray*/
    private Token idTkFind(TreeNode node) {
        for (int i = 0; i < node.tokenArray.size(); i++) {
            String tokenID = node.tokenArray.get(i).getTokenID();
            if (tokenID.equals("idTK")) {
                return node.tokenArray.get(i);
            }
        }
        return null;
    }

    private String addSpaces(int depth) {

        String str = "";
        depth = depth + 1;
        if (depth != 0) {
            for (int i = 1; i < depth; i++) {
                str = "  " + str;
            }
        }

        return str;
    }

}

