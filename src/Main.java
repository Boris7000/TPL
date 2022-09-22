import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("okaaay less goooooo");

        System.out.println("Вспомогательные символы : \"→\" (символ вывода), \"ε\" (пустой символ).");

        Scanner scanner = new Scanner(System.in);

        ///////////////////////////////////////////////////////////////////

        System.out.println("Вводите терминальные символы через запятую.");

        String read = readNextNonEmptyTrimmedString(scanner, new char[]{'→','ε',' '}).toLowerCase();

        String[] v_t = read.split(",");
        String v_tS = String.join(", ", v_t);

        System.out.printf(Locale.getDefault(), "Vᵀ = { %s }%n", v_tS);

        ///////////////////////////////////////////////////////////////////

        System.out.println("Вводите нетерминальные символы через запятую.");

        read = readNextNonEmptyTrimmedString(scanner, new char[]{'→','ε',' '}).toUpperCase();

        String[] v_n = read.split(",");
        String v_nS = String.join(", ", v_n);

        System.out.printf(Locale.getDefault(), "Vᴺ = { %s }%n", v_nS);

        ///////////////////////////////////////////////////////////////////

        System.out.println("Введите начальный символ.");

        read = readNextNonEmptyString(scanner);

        String s = read;

        System.out.printf(Locale.getDefault(), "G = ({ %s }, { %s }, { P }, { %s })%n",v_tS,v_nS,s);

        ///////////////////////////////////////////////////////////////////

        System.out.println("Вводите по одному правилу на строку.\n(правая и левая часть правил отделяются символом \"→\")");

        ArrayList<Rule> p = new ArrayList<>();

        while (!read.isEmpty()){
            read = scanner.nextLine();
            if(!read.isEmpty()){
                int pos = read.indexOf("→");
                if (pos > 0) {
                    String subs1 = read.substring(0,pos);
                    String subs2 = read.substring(pos+1);
                    if(!subs2.isEmpty()){
                        p.add(new Rule(subs1, subs2));
                    }
                }
            }
        }

        ///////////////////////////////////////////////////////////////////

        System.out.println("P = {");
        System.out.printf(Locale.getDefault(),
                "   %s -> %s%n","S", s);
        for (Rule r: p) {
            System.out.printf(Locale.getDefault(),
                    "   %s -> %s%n",r.getLeftPart(), r.getRightPart());
        }
        System.out.println("}");

        ArrayList<String> terminalAL = new ArrayList<>(Arrays.asList(v_t));
        ArrayList<String> notTerminalAL = new ArrayList<>(Arrays.asList(v_n));

        boolean is0 = check0Type(terminalAL, notTerminalAL, p);
        boolean is1 = is0&&check1Type(terminalAL, notTerminalAL, p);
        boolean is2 = is1&&check2Type(terminalAL, notTerminalAL, p);
        boolean is3 = is2&&check3Type(terminalAL, notTerminalAL, p);

        int type_counter = -1;

        if(is0){
            type_counter++;
        }

        if(is1){
            type_counter++;
        }
        if(is2){
            type_counter++;
        }
        if(is3){
            type_counter++;
        }

        switch (type_counter){
            default -> System.out.println("Допущена ошибка при вводе");
            case 0 -> System.out.println("0 тип");
            case 1 -> System.out.println("1 тип (Контекстно-зависимая)");
            case 2 -> System.out.println("2 тип (Контекстно-свободная)");
            case 3 -> System.out.println("3 тип (Регулярная)");
        }
    }

    public static String readNextNonEmptyString(Scanner scanner){
        String read = "";
        while (read.isEmpty()){
            read = scanner.nextLine();
        }
        return read;
    }

    public static String readNextNonEmptyTrimmedString(Scanner scanner, char[] bannedChars){
        String read = "";
        while (read.isEmpty()){
            read = scanner.nextLine();
            for (char chr:bannedChars) {
                read = removeAllCharsInString(chr, read);
            }
        }
        return read;
    }

    public static String removeAllCharsInString(char chr, String string){
        String s = string;
        if(!s.isEmpty()) {
            int pos = s.indexOf(chr);
            while (pos >= 0) {
                if (pos > 0) {
                    String subs1 = s.substring(0,pos);
                    String subs2 = s.substring(pos+1);
                    s = subs1+subs2;
                } else {
                    if(s.length()>=2) {
                        s = s.substring(1);
                    }
                }
                pos = s.indexOf(chr);
            }
        }
        return s;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean notEmptyRuleRightPart(Rule rule, ArrayList<String> terminalAL, ArrayList<String> notTerminalAL){
        String[] rightPart = rule.getRightPartArray();
        if(rightPart.length>0){
            for (String s:rightPart) {
                for (char chr:s.toCharArray()) {
                    String chrS = String.valueOf(chr);
                    if(terminalAL.contains(chrS)||notTerminalAL.contains(chrS)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasTerminalInRuleLeftPart(Rule rule, ArrayList<String> notTerminalAL){
        String leftPart = rule.getLeftPart();
        for (char chr:leftPart.toCharArray()) {
            String chrS = String.valueOf(chr);
            if(notTerminalAL.contains(chrS)){
                return true;
            }
        }
        return false;
    }

    public static boolean hasOnlyTerminalsInRuleLeftPart(Rule rule, ArrayList<String> notTerminalAL){
        String leftPart = rule.getLeftPart();
        for (char chr:leftPart.toCharArray()) {
            String chrS = String.valueOf(chr);
            if(!notTerminalAL.contains(chrS)){
                return false;
            }
        }
        return true;
    }

    public static  int hasTerminalOnTheSideInString(String s, ArrayList<String> notTerminalAL){
        int currentDetectedSide = 0; // в строке нет не терминалов
        if (s.length() == 1) {
            if (notTerminalAL.contains(s)) {
                currentDetectedSide = 4; //в строке только один символ и это не терминал
            } // иначе в строке только один символ и это терминал (в строке нет не терминалов)
        } else {
            String chr1 = String.valueOf(s.charAt(0));
            String chr2 = String.valueOf(s.charAt(s.length() - 1));
            boolean containsLeft = notTerminalAL.contains(chr1);
            boolean containsRight = notTerminalAL.contains(chr2);
            if (containsLeft && containsRight) {
                currentDetectedSide = 3; //не терминал с двух сторон
            } else {
                if (containsLeft) {
                    currentDetectedSide = 1; //не терминал слева
                } else {
                    if (containsRight) {
                        currentDetectedSide = 2; //не терминал справа
                    }
                }
            }
        }
        return currentDetectedSide;
    }

    public static int hasTerminalOnTheSideInRuleRightPart(Rule rule, ArrayList<String> notTerminalAL){
        int currentDetectedSide = 0;
        String[] rightPart = rule.getRightPartArray();
        if(rightPart.length>0){
            for (String s:rightPart) {
                if(currentDetectedSide==0 || currentDetectedSide==4) {
                    currentDetectedSide = hasTerminalOnTheSideInString(s, notTerminalAL);
                } else {
                    int newDetectedSide = hasTerminalOnTheSideInString(s, notTerminalAL);
                    if(newDetectedSide!=0 && newDetectedSide !=4) {
                        if (newDetectedSide != currentDetectedSide) {
                            return 5; // в строках не терминалы с разных сторон
                        }
                    }
                }
            }
        }
        return currentDetectedSide;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Контекстно-зависимая
    public static boolean check0Type(ArrayList<String> terminalAL, ArrayList<String> notTerminalAL, ArrayList<Rule> p){
        /* должны быть такие правила вывода в левой части которых есть хотя бы один не терминал и в правой части любая строка*/
        for (Rule r:p) {
            if (!hasTerminalInRuleLeftPart(r, notTerminalAL)) {
                return false;
            }
        }
        return true;
    }

    //Контекстно-зависимая
    public static boolean check1Type(ArrayList<String> terminalAL, ArrayList<String> notTerminalAL, ArrayList<Rule> p){
        /* должны быть такие правила вывода в левой части которых есть хотя бы один не терминал и в правой части любая непустая строка*/
        for (Rule r:p) {
            if(!notEmptyRuleRightPart(r, terminalAL, notTerminalAL)){
                return false;
            }
        }
        return true;
    }

    //Контекстно-независимая
    public static boolean check2Type(ArrayList<String> terminalAL, ArrayList<String> notTerminalAL, ArrayList<Rule> p){
        /*должны быть такие правила вывода в левой части которых располагаются только не терминалы*/
        for (Rule r:p) {
            if(!hasOnlyTerminalsInRuleLeftPart(r, notTerminalAL)){
                return false;
            }
        }
        return true;
    }

    //Регулярная
    public static boolean check3Type(ArrayList<String> terminalAL, ArrayList<String> notTerminalAL, ArrayList<Rule> p){
        /*должны быть такие правила вывода в правой части которых в конце или в начале располагаются не терминал */

        int currentDetectedSide = 0;

        for (Rule r:p) {
            if(currentDetectedSide==0||currentDetectedSide==4){
                currentDetectedSide = hasTerminalOnTheSideInRuleRightPart(r, notTerminalAL);
                if(currentDetectedSide == 0 || currentDetectedSide == 3 || currentDetectedSide == 5){
                    // в первом же правиле не терминалов вообще нет либо, либо они с двух сторон, либо они с разных сторон
                    return false;
                }
            } else {
                int newDetectedSide = hasTerminalOnTheSideInRuleRightPart(r, notTerminalAL);
                if (newDetectedSide == 0 || newDetectedSide == 3 || newDetectedSide == 5) {
                    // в последующем правиле не терминалов вообще нет либо, либо они с двух сторон, либо они с разных сторон
                    return false;
                }
            }
        }

        return true;
    }

}
