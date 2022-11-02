package lab1;

import java.util.ArrayList;

public class Grammar {

    private final ArrayList<String> terminalAL;
    private final ArrayList<String> notTerminalAL;
    private final ArrayList<Rule> p;

    public Grammar(ArrayList<String> terminalAL, ArrayList<String> notTerminalAL, ArrayList<Rule> p) {
        this.terminalAL = terminalAL;
        this.notTerminalAL = notTerminalAL;
        this.p = p;
    }

    public ArrayList<String> getTerminalAL() {
        return terminalAL;
    }

    public ArrayList<String> getNotTerminalAL() {
        return notTerminalAL;
    }

    public ArrayList<Rule> getP() {
        return p;
    }

}
