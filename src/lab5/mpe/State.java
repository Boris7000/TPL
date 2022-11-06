package lab5.mpe;

import java.util.ArrayList;

public class State {

    private final ArrayList<String> input;
    private final ArrayList<String> stack;
    private final ArrayList<ArrayList<Integer>> trys;
    private final int state;

    public ArrayList<String> getInput() {
        return input;
    }

    public ArrayList<String> getStack() {
        return stack;
    }

    public ArrayList<ArrayList<Integer>> getTrys() {
        return trys;
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

    public String getLastInput(){
        return input.get(input.size()-1);
    }

    public String getLastStack(){
        return stack.get(stack.size()-1);
    }

    public ArrayList<Integer> getLastTry(){
        return trys.get(trys.size()-1);
    }

    public State(ArrayList<String> input, ArrayList<String> stack, ArrayList<ArrayList<Integer>> trys, int state) {
        this.input = new ArrayList<>(input);
        this.stack = new ArrayList<>(stack);
        this.trys = new ArrayList<>();
        for (ArrayList<Integer> steps: trys) {
            this.trys.add(new ArrayList<>(steps));
        }
        this.state = state;
    }

}
