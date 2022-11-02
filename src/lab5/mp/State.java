package lab5.mp;

import java.util.ArrayList;

public class State {

    private final ArrayList<String> input;
    private final ArrayList<String> stack;
    private final int state;

    public ArrayList<String> getInput() {
        return input;
    }

    public ArrayList<String> getStack() {
        return stack;
    }

    public int getState() {
        return state;
    }

    public String getFirstInput(){
        return input.get(0);
    }

    public String getFirstStack(){
        return stack.get(0);
    }

    public boolean checkRight(ArrayList<String> terminals, ArrayList<String> notTerminals){
        boolean allGood = true;

        if(stack.isEmpty()&&!input.isEmpty()){
            allGood = false;
        }

        if(allGood){
            boolean onlyTerminals = true;
            for (String string : stack) {
                if (!terminals.contains(string)) {
                    onlyTerminals = false;
                    break;
                }
            }
            if(onlyTerminals){
                if(stack.size()<input.size()){
                    allGood = false;
                }
            }
        }

        if(allGood) {
            ArrayList<String> inputN = extractInputN();
            for (String string : stack) {
                if (!inputN.contains(string)) {
                    if (!notTerminals.contains(string)) {
                        allGood = false;
                        break;
                    }
                }
            }
        }
        if(allGood){
            int stackTerminalsCount = 0;
            for (String string:stack) {
                if(terminals.contains(string)){
                    stackTerminalsCount++;
                }
            }
            if(stackTerminalsCount>input.size()){
                allGood = false;
            }
        }

        return allGood;
    }

    public ArrayList<String> extractInputN(){
        ArrayList<String> inputN = new ArrayList<>();
        for (String s:input) {
            if(!inputN.contains(s)){
                inputN.add(s);
            }
        }
        inputN.add("ε");
        return inputN;
    }

    public State(ArrayList<String> input, ArrayList<String> stack, int state) {
        this.input = new ArrayList<>(input);
        this.stack = new ArrayList<>(stack);
        this.state = state;
    }
}
