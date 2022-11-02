package lab5;

import lab1.Rule;

public class MFunction {

    public static final int state_q = 0;
    public static final int state_r = 1;

    private final Rule rule;
    private final int state;

    public Rule getRule() {
        return rule;
    }

    public int getState() {
        return state;
    }

    public MFunction(Rule rule, int state){
        this.rule = rule;
        this.state = state;
    }

}
