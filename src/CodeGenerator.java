/* Author: Logan Pettit
* Class: 4280 Compiler Design
* Description: This is the code generation part of the compiler. The program begins a preorder traversal
* of the nodes and generates code based on the type of node that it comes to. The output is redirected into
* [input filename].asm
* */




import DataStructures.Token;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class CodeGenerator {

    private ArrayList<String> tempVarList = new ArrayList<>();
    public String fileName;
    private VariableStack variableStack = new VariableStack();
    private int varCounter;
    private Stack<Integer> scopeCountStack = new Stack<>();
    private int scopeCount;
    private int tempCount = 0;
    private int loopLabelCount = 0;
    private int outLabelCount = 0;
    private int ifCounter = 0;

    public void startCodeGeneration(TreeNode node) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
            out.write("");
            out.close();
        } catch (IOException e){
            System.out.println(e);
            System.exit(-1);
        }

        PreorderTraverseTreeGenerateCode(node);
        Print2Target("STOP");
        addTempVars();
        System.out.println("Input file compiled to " + fileName);
    }

    private void PreorderTraverseTreeGenerateCode(TreeNode node) {

        if (node == null) {
            return;
        }
        /*Generate no code for program*/
        else if (node.nonterminal.equals("Program")) {
            scopeCount = 0;
            scopeCountStack.add(scopeCount);

            /*Traverse to find global vars first then blocks*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(i));
            }
        }

        /*Block node, no code generation except for system stack poping*/
        else if (node.nonterminal.equals("Block")) {
            scopeCount += varCounter;
            scopeCountStack.add(scopeCount);
            int prevVars = varCounter;
            varCounter = 0;

            /*Traverse the rest of the block first vars and stats*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(i));
            }

            /*pop local block vars befoe exiting block*/
            for (int i = 0; i < varCounter; i++) {
                variableStack.pop();
                Print2Target("POP");
            }

            /*Pop scope from scope counting stack*/
            scopeCountStack.pop();

            varCounter = prevVars;
        }
        /*Came to var initilization add to bottom of the .asm file*/
        else if (node.nonterminal.equals("Vars")) {

            Token idTk = idTkFind(node);
            Token tk = NumTkFind(node);

            if (tk == null || idTk == null) {
                return;
            }

            String num = tk.getTokenStr();

            variableStack.push(idTk);
            varCounter += 1;

            /*allocate room on vm stack*/
            Print2Target("PUSH");

            /*add var value to vm stack*/
            Print2Target("LOAD " + num);

            /*Write to top of stack*/
            Print2Target("STACKW 0");

            /*Visit other var nodes if they exist*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(i));
            }
        }

        /*Assign node store number */
        else if (node.nonterminal.equals("assign")) {

            /*Run to evaluate expression on right hand side of*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(i));
            }

            Token tk = idTkFind(node);
            if (tk == null) {
                return;
            }

            /*Initialize find function to get position in stack*/
            String var = tk.getTokenStr();

            int position = (variableStack.varStack.size() - 1) - variableStack.Find(var);


            /*Need position of number in stack to overwrite*/
            Print2Target("STACKW " + position);

        }
        else if (node.nonterminal.equals("expr")) {

            /*Check for single child first*/
            if (node.childNodes.size() == 1) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(0));
            }
            /*Otherwise evaluate the right child first*/
            else {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(1));
                String temp = genTemp();
                Print2Target("STORE " + temp);
                PreorderTraverseTreeGenerateCode(node.childNodes.get(0));
                Print2Target("ADD " + temp);
            }
        }
        else if (node.nonterminal.equals("in")) {
            String temp = genTemp();

            /*Read input from keyboard*/
            Print2Target("READ " + temp);

            /*Load previous input into ACC*/
            Print2Target("LOAD " + temp);

            /*Get Id portion of node*/
            Token tk = idTkFind(node);
            if (tk == null) {
                return;
            }

            /*Initialize find function to get position in stack*/
            String var = tk.getTokenStr();

            /*Get distance from top of stack*/
            int position = (variableStack.varStack.size() - 1) - variableStack.Find(var);

            /*Write to virtual machine stack*/
            Print2Target("STACKW " + position);
        }

        else if (node.nonterminal.equals("out")) {
            String temp = genTemp();

            /*Evaluate children first*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(i));
            }

            Print2Target("STORE " + temp);
            Print2Target("WRITE " + temp);
        }

        else if (node.nonterminal.equals("R")) {

            Token idTk = idTkFind(node);
            Token numTK = NumTkFind(node);

            /*If the node has an id token*/
            if (idTk != null) {
                /*Initialize find function to get position in stack*/
                String var = idTk.getTokenStr();

                /*Get distance from top of stack*/
                int position = (variableStack.varStack.size() - 1) - variableStack.Find(var);

                Print2Target("STACKR " + position);
            }
            /*if the node has a num token*/
            else if (numTK != null) {
                String num = numTK.getTokenStr();
                Print2Target("LOAD " + num);
            }

            /*If expression keep traversing*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(i));
            }
        }
        else if (node.nonterminal.equals("A")) {
            int Anode = -1;
            int Nnode = -1;

            /*Determine if there is an A node and N node child*/
            for (int i = 0; i < node.childNodes.size(); i++) {
                if (node.childNodes.get(i).nonterminal.equals("A")) {
                    Anode = i;
                }
                else if (node.childNodes.get(i).nonterminal.equals("N")) {
                    Nnode = i;
                }
            }

            /*If there is an A node then process otherwise traverse to N node*/
            if (Anode > -1) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(Anode));
                String temp = genTemp();
                Print2Target("STORE " + temp);
                PreorderTraverseTreeGenerateCode(node.childNodes.get(Nnode));
                Print2Target("SUB " + temp);
            }
            else {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(Nnode));
            }
        }
        else if (node.nonterminal.equals("N")) {
            int Mnode = -1;
            int Nnode = -1;
            String MathType = "";
            String temp = "";

                /*Determine if there is an M and N node or just M */
                for (int i = 0; i < node.childNodes.size(); i++) {
                    /*Find left child*/
                    if (node.childNodes.get(i).nonterminal.equals("M")) {
                        Mnode = i;
                    }
                    /*Find right child*/
                    else if (node.childNodes.get(i).nonterminal.equals("N")) {
                        Nnode = i;
                    }
                }

                /*If there is a right child process it*/
                if (Nnode > -1) {
                    PreorderTraverseTreeGenerateCode(node.childNodes.get(Nnode));

                    /*Figure out math sign used*/
                    for (int i = 0; i < node.tokenArray.size(); i++) {
                        if (node.tokenArray.get(i) != null) {
                            MathType = node.tokenArray.get(i).getTokenID();
                        }
                    }
                }

                temp = genTemp();
                Print2Target("STORE " + temp);

                /*Proccess left child now*/
                if (Mnode > -1) {
                    PreorderTraverseTreeGenerateCode(node.childNodes.get(Mnode));
                }
                if (!MathType.equals("")) {
                    if (MathType.equals("multiplyTK")) {
                        Print2Target("MULT " + temp);
                    } else {
                        Print2Target("DIV " + temp);
                    }
                }

        }
        else if(node.nonterminal.equals("M")){
            int Mnode = -1;
            int Rnode = -1;

            /*Check where M and R nodes are if they exist*/
            for(int i = 0; i < node.childNodes.size(); i++){
                if(node.childNodes.get(i).nonterminal.equals("M")){
                    Mnode = i;
                }
                else if (node.childNodes.get(i).nonterminal.equals("R")){
                    Rnode = i;
                }
            }

            /*If there is an M proccess it
             then make it negative */
            if(Mnode > -1){
                PreorderTraverseTreeGenerateCode(node.childNodes.get(Mnode));
                Print2Target("MULT -1");
            }
            else if (Rnode > -1){
                PreorderTraverseTreeGenerateCode(node.childNodes.get(Rnode));
            }

        }

        else if(node.nonterminal.equals("ifstmt")){
            int expr1 = -1;
            int ro = -1;
            int expr2 = -1;
            int stat = -1;

            for (int i = 0; i < node.childNodes.size(); i++){
                if (node.childNodes.get(i).nonterminal.equals("expr")){
                    expr1 = i;

                    for(int j = i; j < node.childNodes.size(); j++){
                        if(node.childNodes.get(j).nonterminal.equals("RO")) {
                            ro = j;

                            for (int k = j; k < node.childNodes.size(); k++) {
                                if(node.childNodes.get(k).nonterminal.equals("expr")) {
                                    expr2 = k;

                                    for(int m = k; m < node.childNodes.size(); m++){
                                        if(node.childNodes.get(m).nonterminal.equals("stat")){
                                            stat = m;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            PreorderTraverseTreeGenerateCode(node.childNodes.get(expr2));
            String temp1 = genTemp();
            Print2Target("STORE " + temp1);

            PreorderTraverseTreeGenerateCode(node.childNodes.get(expr1));
            Print2Target("SUB " + temp1);

            String label = genIfLabel();
            generateRO(node.childNodes.get(ro), label);
            PreorderTraverseTreeGenerateCode(node.childNodes.get(stat));

            Print2Target(label + ": NOOP");

        }

        else if(node.nonterminal.equals("loop")){
            int expr1 = -1;
            int ro = -1;
            int expr2 = -1;
            int stat = -1;

            for (int i = 0; i < node.childNodes.size(); i++){
                if (node.childNodes.get(i).nonterminal.equals("expr")){
                    expr1 = i;

                    for(int j = i; j < node.childNodes.size(); j++){
                        if(node.childNodes.get(j).nonterminal.equals("RO")) {
                            ro = j;

                            for (int k = j; k < node.childNodes.size(); k++) {
                                if(node.childNodes.get(k).nonterminal.equals("expr")) {
                                    expr2 = k;

                                    for(int m = k; m < node.childNodes.size(); m++){
                                        if(node.childNodes.get(m).nonterminal.equals("stat")){
                                            stat = m;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            String loopLabel = genLoopLabel();
            String outLabel = genOutLabel();

            Print2Target(loopLabel + ": NOOP");
            PreorderTraverseTreeGenerateCode(node.childNodes.get(expr2));
            String temp1 = genTemp();
            Print2Target("STORE " + temp1);

            PreorderTraverseTreeGenerateCode(node.childNodes.get(expr1));
            Print2Target("SUB " + temp1);

            generateRO(node.childNodes.get(ro), outLabel);
            PreorderTraverseTreeGenerateCode(node.childNodes.get(stat));

            Print2Target("BR " + loopLabel);
            Print2Target(outLabel + ": NOOP");
        }

        /*Keep traversing through the tree even if node was not came to*/
        else {
            for (int i = 0; i < node.childNodes.size(); i++) {
                PreorderTraverseTreeGenerateCode(node.childNodes.get(i));
            }
        }

    }

    private void generateRO(TreeNode node, String outLabel){
        StringBuilder relationalOperator = new StringBuilder();

        for(int i = 0; i < node.tokenArray.size(); i++){
            relationalOperator.append(node.tokenArray.get(i).getTokenStr());
        }

        switch(relationalOperator.toString()){
            case ">":
                Print2Target("BRZNEG " + outLabel);
                break;

            case "<":
                Print2Target("BRZPOS " + outLabel);
                break;

            case ">>":
                Print2Target("BRNEG " + outLabel);
                break;

            case "<<":
                Print2Target("BRPOS " + outLabel);
                break;

            case "=":
                Print2Target("BRPOS " + outLabel);
                Print2Target("BRNEG " + outLabel);
                break;

            case "<>":
                Print2Target("BRZERO " + outLabel);
                break;
        }

    }

    /*Find token in with Id in tokens tokenArray*/
    private Token NumTkFind(TreeNode node) {
        for (int i = 0; i < node.tokenArray.size(); i++) {
            String tokenID = node.tokenArray.get(i).getTokenID();
            if (tokenID.equals("numTK")) {
                return node.tokenArray.get(i);
            }
        }
        return null;
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

    private void Print2Target(String code) {
        try {
            code = code + "\n";
            /*Open given file in append mode.*/
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write(code);
            out.close();
        } catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }

    private String genIfLabel(){
        String label = "cond" + ifCounter;
        ifCounter += 1;
        return label;
    }

    private String genTemp() {
        String temp = "Temp" + tempCount;
        tempCount += 1;
        tempVarList.add(temp);
        return temp;
    }

    private String genLoopLabel(){
        String label = "loop" + loopLabelCount;
        loopLabelCount += 1;
        return label;
    }

    private String genOutLabel(){
        String outLabel = "out" + outLabelCount;
        outLabelCount += 1;
        return outLabel;
    }

    private void addTempVars() {
        StringBuilder initializedVarString = new StringBuilder();
        for (int i = 0; i < tempVarList.size(); i++) {
            initializedVarString.append(tempVarList.get(i) + " 0\n");
        }

        Print2Target(initializedVarString.toString());
    }

}
