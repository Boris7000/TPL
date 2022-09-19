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
}
