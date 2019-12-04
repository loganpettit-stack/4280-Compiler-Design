import DataStructures.Token;

import java.util.ArrayList;

public class TreeNode {
     String nonterminal;
     ArrayList<Token> tokenArray = new ArrayList<>();
     ArrayList<TreeNode> childNodes = new ArrayList<>();

    TreeNode(String nonterminal) {
        this.nonterminal = nonterminal;
    }

    public String toString(){
        String nodeString = nonterminal + " " + tokenArray;
        return nodeString;
    }


}
