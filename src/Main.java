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

        boolean is0 = check0Type(v_t,v_n,s,p);
        boolean is1 = is0&&check1Type(v_t,v_n,s,p);
        boolean is2= is1&&check2Type(v_t,v_n,s,p);
        boolean is3 = is2&&check3Type(v_t,v_n,s,p);


        System.out.printf(Locale.getDefault(),
                "is 0 - %b\nis 1 - %b\nis 2 - %b\nis 3 - %b\n", is0, is1, is2, is3);
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

    public static String readNextNonEmptyTrimmedString(Scanner scanner, String[] bannedSubstrings){
        String read = "";
        while (read.isEmpty()){
            read = scanner.nextLine();
            for (String substr:bannedSubstrings) {
                read = removeAllSubstringsInString(substr, read);
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

    public static String removeAllSubstringsInString(String substr, String string){
        String s = string;
        if(!s.isEmpty()) {
            int pos = s.indexOf(substr);
            while (pos >= 0) {
                if (pos > 0) {
                    String subs1 = s.substring(0,pos);
                    String subs2 = s.substring(pos+substr.length());
                    s = subs1+subs2;
                } else {
                    if(s.length()>=2) {
                        s = s.substring(1);
                    }
                }
                pos = s.indexOf(substr);
            }
        }
        return s;
    }

    public static String trimString(String string){
        String trimmed = string;
        if(!trimmed.isEmpty()) {
            trimmed = removeAllCharsInString(' ', string);
        }
        return trimmed;
    }


    //+ - это без ε
    //* - это c ε

    //Контекстно-зависимая
    public static boolean check0Type(String[] terminals, String[] notTerminals, String initString, ArrayList<Rule> p){
        /* должны быть такие правила вывода в левой части которых есть хоть один не терминал и в правой части любая строка*/

        ArrayList<String> notTerminalAL = new ArrayList<>(Arrays.asList(notTerminals));

        boolean contextDependent = true;

        for (Rule r:p) {
            String leftPart = r.getLeftPart();
            boolean thereIsAtLeastOneLeft = false;
            for (char chr:leftPart.toCharArray()) {
                if(notTerminalAL.contains(String.valueOf(chr))){
                    thereIsAtLeastOneLeft = true;
                    break;
                }
            }
            if(!thereIsAtLeastOneLeft){
                contextDependent = false;
                break;
            }
        }

        return contextDependent;
    }

    //Контекстно-зависимая
    public static boolean check1Type(String[] terminals, String[] notTerminals, String initString, ArrayList<Rule> p){
        /* должны быть такие правила вывода в левой части которых есть хоть один не терминал и в правой части любая непустая строка*/

        ArrayList<String> terminalAL = new ArrayList<>(Arrays.asList(terminals));
        ArrayList<String> notTerminalAL = new ArrayList<>(Arrays.asList(notTerminals));

        boolean contextDependent = true;

        for (Rule r:p) {
            String leftPart = r.getLeftPart();
            String rightPart = r.getRightPart();
            boolean thereIsAtLeastOneLeft = false;
            for (char chr:leftPart.toCharArray()) {
                if(notTerminalAL.contains(String.valueOf(chr))){
                    thereIsAtLeastOneLeft = true;
                    break;
                }
            }
            boolean thereIsAtLeastOneRight = false;
            for (char chr:rightPart.toCharArray()) {
                if(terminalAL.contains(String.valueOf(chr))){
                    thereIsAtLeastOneRight = true;
                    break;
                }
                if(notTerminalAL.contains(String.valueOf(chr))){
                    thereIsAtLeastOneRight = true;
                    break;
                }
            }
            if(!thereIsAtLeastOneLeft&&!thereIsAtLeastOneRight){
                contextDependent = false;
                break;
            }
        }

        return contextDependent;
    }

    //Контекстно-независимая
    public static boolean check2Type(String[] terminals, String[] notTerminals, String initString, ArrayList<Rule> p){
        /*должны быть такие правила вывода в левой части которых располагаются только не терминалы*/

        ArrayList<String> notTerminalAL = new ArrayList<>(Arrays.asList(notTerminals));

        boolean contextIndependent = true;

        for (Rule r:p) {
            String leftPart = r.getLeftPart();
            int pos = notTerminalAL.indexOf(leftPart);
            if(pos<0){
                contextIndependent = false;
                break;
            }
        }

        return contextIndependent;
    }

    //Регулярная
    public static boolean check3Type(String[] terminals, String[] notTerminals, String initString, ArrayList<Rule> p){
        /*должны быть такие правила вывода в правой части которых в конце или в начале располагаются не терминал */

        ArrayList<String> terminalAL = new ArrayList<>(Arrays.asList(terminals));
        ArrayList<String> notTerminalAL = new ArrayList<>(Arrays.asList(notTerminals));

        boolean regular = true;
        boolean sideDetermined = false;
        boolean rightSide = false;


        for (Rule r:p) {
            String[] rightPartArray = r.getRightPartArray();
            for (String rpItem:rightPartArray) {
                char[] chars = rpItem.toCharArray();
                char lastChar = chars[chars.length-1];
                char firstChar = chars[0];

                int type = -1;

                if(!notTerminalAL.contains(String.valueOf(firstChar)) && !notTerminalAL.contains(String.valueOf(lastChar))){
                    //Строка без не терминалов.
                    type = 0;
                } else {
                    if(notTerminalAL.contains(String.valueOf(firstChar)) && !notTerminalAL.contains(String.valueOf(lastChar))){
                        //Строка с не терминалом слева.
                        type = 1;
                    } else {
                        if(!notTerminalAL.contains(String.valueOf(firstChar)) && notTerminalAL.contains(String.valueOf(lastChar))){
                            //Строка с не терминалом справа.
                            type = 2;
                        } else {
                            //Строка с не терминалом с обеих сторон.
                            if(notTerminalAL.contains(String.valueOf(firstChar)) && notTerminalAL.contains(String.valueOf(lastChar))){
                               type = 3;
                            }
                        }
                    }
                }

                switch (type) {
                    case 0 -> {}
                    case 1 -> {
                        if (sideDetermined) {
                            if (rightSide) {
                                regular = false;
                                break;
                            }
                        } else {
                            sideDetermined = true;
                        }

                        for (int i = 1; i < chars.length; i++) {
                            if (!terminalAL.contains(String.valueOf(chars[i]))) {
                                regular = false;
                                break;
                            }
                        }
                    }
                    case 2 -> {
                        if (sideDetermined) {
                            if (!rightSide) {
                                regular = false;
                                break;
                            }
                        } else {
                            sideDetermined = true;
                            rightSide = true;
                        }

                        for (int i = 0; i < chars.length - 1; i++) {
                            if (!terminalAL.contains(String.valueOf(chars[i]))) {
                                regular = false;
                                break;
                            }
                        }
                    }
                    case 3 -> regular = false;
                }

            }
        }

        return regular;
    }

}
