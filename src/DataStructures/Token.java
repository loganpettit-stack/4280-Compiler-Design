/*
* Author: Logan Pettit
* Description: Structure that stores the attributes of the token*/
package DataStructures;

public class Token {

    private String tokenStr = "";
    private String tokenID = "";
    private int lineNum;


    public void setTokenStr(String tokenStr){
        this.tokenStr = tokenStr;
    }

    public void setTokenID(String tokenID){
        this.tokenID = tokenID;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getTokenID() { return tokenID; }

    public int getLineNum() {return  lineNum; }

    public String getTokenStr(){ return tokenStr; }

    public String toString(){
        return tokenID + ", " + tokenStr + ", Line = " + lineNum;
    }
}
