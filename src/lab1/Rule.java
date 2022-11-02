package lab1;

import java.util.Arrays;

public class Rule {

    private final String leftPart;
    private final String rightPart;
    private final String[] rightPartArray;

    public Rule(String leftPart, String rightPart) {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.rightPartArray = rightPart.split("\\|");
    }

    public String getLeftPart() {
        return leftPart;
    }

    public String getRightPart() {
        return rightPart;
    }

    public String[] getRightPartArray() {
        return rightPartArray;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Rule rule){
            return leftPart.equals(rule.leftPart)&&
                    rightPart.equals(rule.rightPart)&&
                    Arrays.equals(rightPartArray, rule.rightPartArray);
        }
        return super.equals(obj);
    }
}
