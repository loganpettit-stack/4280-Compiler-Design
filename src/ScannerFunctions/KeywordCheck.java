/*Author: Logan Pettit
* Description: Checks if the stored token string is a keyword and replaces the token Id if it is*/

package ScannerFunctions;

import DataStructures.Token;

import java.util.Hashtable;

public class KeywordCheck {

    private Hashtable<String, String> keyWordHash = new Hashtable<>();

    private void fillTable(){
        this.keyWordHash.put("start", "StartKwTK");
        this.keyWordHash.put("stop", "StopKwTK");
        this.keyWordHash.put("iterate", "IterateKwTK");
        this.keyWordHash.put("void", "VoidKwTK");
        this.keyWordHash.put("var", "VarKwTK");
        this.keyWordHash.put("return", "ReturnKwTK");
        this.keyWordHash.put("in", "InKwTK");
        this.keyWordHash.put("out", "OutKwTK");
        this.keyWordHash.put("program", "ProgramKwTK");
        this.keyWordHash.put("cond", "CondKwTK");
        this.keyWordHash.put("then", "ThenKwTK");
        this.keyWordHash.put("let", "LetKwTK");
    }


    public Token checkKeyword(Token token){
        fillTable();

        String tokenString = token.getTokenStr();

        if (keyWordHash.containsKey(tokenString)){
            String tokenId = keyWordHash.get(tokenString);
            token.setTokenID(tokenId);
        }

        return token;
    }

}
