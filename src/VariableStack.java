import DataStructures.Token;

import java.util.Stack;

public class VariableStack {

    public Stack<Token> varStack = new Stack<>();

    /*Look for variable in whole stack
    * return stack position*/
    public int Find(String var) {
        for (int i = varStack.size() - 1; i >= 0; i--) {

            if (varStack.get(i).getTokenStr().equals(var)) {
                return i;
            }
        }
        return -1;
    }

    /*Look for variable existance in current scope*/
    public boolean VerifyVarExistance(String var, int scope) {
        for (int i = varStack.size() - 1; i >= scope; i--) {

            if(!varStack.isEmpty()) {
                if (varStack.get(i).getTokenStr().equals(var)) {
                    return true;
                }
            }
        }

          return false;
    }

    public void pop() {
        varStack.pop();
    }

    public void push(Token token) {
        varStack.push(token);
    }

}
