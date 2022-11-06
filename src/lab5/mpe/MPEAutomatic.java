package lab5.mpe;

import lab1.Grammar;
import lab1.GrammarTypes;
import lab1.Rule;
import lab5.MFunction;

import java.util.ArrayList;
import java.util.Scanner;

import static lab5.MFunction.state_q;
import static lab5.MFunction.state_r;
import static lab5.mp.MPAutomatic.backTrys;

public class MPEAutomatic {

    public static void main(String[] args) {

        //Ввод грамматики
        Grammar grammar = GrammarTypes.enterGrammar();//Grammar grammar = GrammarTypes.enterGrammar();
        //Grammar grammar = new Grammar(new ArrayList<>() {{add("+");add("(");add(")");add("a");}},
        //new ArrayList<>(){{add("S");add("A");}},
        //new ArrayList<>(){{add(new Rule("S","S+A|A"));add(new Rule("A","(S)|a"));}});
        int type = GrammarTypes.resolveGrammarType(grammar);

        //Проверка на контекстно-свободную
        if (type == 2) {
            System.out.println("Грамматика контекстно-свободная.");

            //Правила
            ArrayList<Rule> p = grammar.getP();
            //p.add(new Rule("ε", ""));

            //Магазинные функции
            ArrayList<MFunction> mFunctions = new ArrayList<>();
            for (Rule rule : p) {
                for (String string : rule.getRightPartArray()) {
                    Rule rule1 = new Rule(rule.getLeftPart(), string);
                    MFunction mFunction = new MFunction(rule1, MFunction.state_q);
                    mFunctions.add(mFunction);
                    System.out.println(mFunctions.indexOf(mFunction) + ") " + rule1.getLeftPart() + "->" + rule1.getRightPart());
                }
            }
            {
                Rule rule = new Rule("", "#" + p.get(0).getLeftPart());
                MFunction mFunction = new MFunction(rule, MFunction.state_r);
                mFunctions.add(mFunction);
                System.out.println(mFunctions.indexOf(mFunction) + ") " + rule.getLeftPart() + "->" + rule.getRightPart());
            }

            //Магазин (стек)
            ArrayList<String> stack = new ArrayList<>();
            stack.add("#");

            //Входная строка
            ArrayList<String> input = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите входную строку.");
            String inputString = scanner.nextLine();
            for (int i = 0; i < inputString.length(); i++) {
                input.add(String.valueOf(inputString.charAt(i)));
            }

            ArrayList<State> states = new ArrayList<>();
            states.add(new State(input, stack, new ArrayList<>(){{add(new ArrayList<>());}}, state_q));
            State currentState = states.get(0);

            System.out.println("Стек = " + currentState.getStack()
                    + ", входная строка = " + currentState.getInput());


            while (currentState.getState()!=state_r){

                //Ищем правые стороны правил в стеке
                ArrayList<MFunction> findFunctions = new ArrayList<>();
                StringBuilder stackSB = new StringBuilder();
                for(int i = 0; i<currentState.getStack().size(); i++){
                    stackSB.append(currentState.getStack().get(i));
                }
                String stackString = stackSB.toString();

                for (MFunction mFunction : mFunctions) {
                    Rule rule = mFunction.getRule();
                    String rightPart = rule.getRightPart();
                    if(stackString.lastIndexOf(rightPart)>-1){
                        findFunctions.add(mFunction);
                    }
                }
                //

                if(!findFunctions.isEmpty()) {
                    MFunction rightFunction = null;

                    ArrayList<Integer> trye = currentState.getLastTry();

                    //Поиск похожих попыток
                    ArrayList<ArrayList<Integer>> sameTrys = new ArrayList<>();
                    for (ArrayList<Integer> tr:currentState.getTrys()) {
                        if(tr.size()-trye.size()==1){
                            boolean same = true;
                            for (int i =0; i<trye.size();i++){
                                int index = tr.get(i);
                                int currentIndex = trye.get(i);
                                if(currentIndex!=index){
                                    same = false;
                                    break;
                                }
                            }
                            if(same){
                                sameTrys.add(tr);
                            }
                        }
                    }

                    //Выбор функции в соответствии с предыдущими попытками
                    if(sameTrys.isEmpty()){
                        rightFunction = findFunctions.get(0);
                    } else {
                        for (MFunction mfunction:findFunctions) {
                            boolean notUsed = true;
                            for (ArrayList<Integer> tr:sameTrys) {
                                if(tr.get(tr.size()-1)==mFunctions.indexOf(mfunction)){
                                    notUsed = false;
                                    break;
                                }
                            }
                            if(notUsed){
                                rightFunction = mfunction;
                                break;
                            }
                        }
                    }

                    if (rightFunction!=null){

                        trye.add(mFunctions.indexOf(rightFunction));

                        Rule rule = rightFunction.getRule();
                        String leftPart = rule.getLeftPart();
                        String rightPart = rule.getRightPart();

                        int pos = stackString.lastIndexOf(rightPart);

                        states.add(new State(currentState.getInput(), currentState.getStack(), currentState.getTrys(), rightFunction.getState()));
                        currentState = states.get(states.size() - 1);
                        for (int j = 0;j < rightPart.length();j++){
                            currentState.getStack().remove(pos);
                        }
                        for (int j = 0; j < leftPart.length(); j++) {
                            currentState.getStack().add(pos+j,String.valueOf(leftPart.charAt(j)));
                        }

                        if(currentState.getState()==state_r&&!currentState.getInput().isEmpty()){
                            //Программа пошла не по правильному пути, откат на состояние назад
                            if (states.size() > 1) {
                                states.remove(states.size() - 1);
                                currentState = states.get(states.size() - 1);
                                currentState.getTrys().add(backTrys(currentState.getLastTry()));
                            } else {
                                System.out.println("Ошибка. Невозможно построить данную строку.");
                                currentState.getTrys().clear();
                                break;
                            }
                        }
                    } else {
                        //Все возможные функции для этой попытки использованы
                        if (!currentState.getInput().isEmpty()) {
                            //Переносим следующий терминал если еще этого не делали
                            boolean hasMove = false;
                            for (ArrayList<Integer> tr: currentState.getTrys()) {
                                if (tr.contains(-1)){
                                    hasMove = true;
                                    break;
                                }
                            }
                            if(!hasMove) {
                                System.out.println("Переносим следующий терминал");
                                currentState.getLastTry().add(-1);
                                states.add(new State(currentState.getInput(), currentState.getStack(),
                                        new ArrayList<>() {{
                                            add(new ArrayList<>());
                                        }}, currentState.getState()));
                                currentState = states.get(states.size() - 1);
                                currentState.getStack().add(currentState.getFirstInput());
                                currentState.getInput().remove(currentState.getFirstInput());

                                System.out.println("Стек = "+currentState.getStack()
                                        +", входная строка = " + currentState.getInput());
                            } else {
                                //Терминал уже переносили, откат на состояние назад
                                if (states.size() > 1) {
                                    System.out.println("Откат на состояние назад");
                                    states.remove(states.size() - 1);
                                    currentState = states.get(states.size() - 1);
                                    currentState.getTrys().add(backTrys(currentState.getLastTry()));
                                } else {
                                    System.out.println("Ошибка. Невозможно построить данную строку.");
                                    currentState.getTrys().clear();
                                    break;
                                }
                            }
                        } else {
                            //Терминалов для переноса нет, откат на состояние назад
                            if (states.size() > 1) {
                                System.out.println("Откат на состояние назад");
                                states.remove(states.size() - 1);
                                currentState = states.get(states.size() - 1);
                                currentState.getTrys().add(backTrys(currentState.getLastTry()));
                            } else {
                                System.out.println("Ошибка. Невозможно построить данную строку.");
                                currentState.getTrys().clear();
                                break;
                            }
                        }
                    }
                } else {
                    if (!currentState.getInput().isEmpty()) {
                        //Переносим следующий терминал если еще этого не делали
                        boolean hasMove = false;
                        for (ArrayList<Integer> tr: currentState.getTrys()) {
                            if (tr.contains(-1)){
                                hasMove = true;
                                break;
                            }
                        }
                        if(!hasMove) {
                            System.out.println("Переносим следующий терминал");
                            currentState.getLastTry().add(-1);
                            states.add(new State(currentState.getInput(), currentState.getStack(),
                                    new ArrayList<>() {{
                                        add(new ArrayList<>());
                                    }}, currentState.getState()));
                            currentState = states.get(states.size() - 1);
                            currentState.getStack().add(currentState.getFirstInput());
                            currentState.getInput().remove(currentState.getFirstInput());

                            System.out.println("Стек = "+currentState.getStack()
                                    +", входная строка = " + currentState.getInput());
                        } else {
                            //Терминал уже переносили, откат на состояние назад
                            if (states.size() > 1) {
                                System.out.println("Откат на состояние назад");
                                states.remove(states.size() - 1);
                                currentState = states.get(states.size() - 1);
                                currentState.getTrys().add(backTrys(currentState.getLastTry()));
                            } else {
                                System.out.println("Ошибка. Невозможно построить данную строку.");
                                currentState.getTrys().clear();
                                break;
                            }
                        }
                    } else {
                        //Терминалов для переноса нет, откат на состояние назад
                        if (states.size() > 1) {
                            System.out.println("Откат на состояние назад");
                            states.remove(states.size() - 1);
                            currentState = states.get(states.size() - 1);
                            currentState.getTrys().add(backTrys(currentState.getLastTry()));
                        } else {
                            System.out.println("Ошибка. Невозможно построить данную строку.");
                            currentState.getTrys().clear();
                            break;
                        }
                    }

                }
                System.out.println("Стек = " + currentState.getStack()
                        + ", входная строка = " + currentState.getInput()
                        + ", попытки = " + currentState.getTrys());
                //scanner.nextLine();
            }

            if(currentState.getInput().isEmpty()&&currentState.getStack().isEmpty()){
                System.out.println("Строка принята.");
            }

            /*
            while (currentState.getState()!=state_r) {

                //Ищем правые стороны правил в стеке
                ArrayList<MFunction> findFunctions = new ArrayList<>();
                StringBuilder stackSB = new StringBuilder();
                for(int i = 0; i<currentState.getStack().size(); i++){
                    stackSB.append(currentState.getStack().get(i));
                }
                String stackString = stackSB.toString();
                System.out.println("Ищем правую часть для: "+stackString);

                for(int i = 0; i<stackString.length(); i++){
                    String searchString = stackString.substring(i);
                    for (MFunction mFunction : mFunctions) {
                        Rule rule = mFunction.getRule();
                        String rightPart = rule.getRightPart();
                        if(searchString.equals(rightPart)){
                            System.out.println(searchString+" содержит " + rightPart);
                            findFunctions.add(mFunction);
                        }
                    }
                }

                if(!findFunctions.isEmpty()){
                    MFunction rightFunction = null;

                    ArrayList<Integer> trye = currentState.getLastTry();
                    System.out.println("Найдено функций для "+stackString+" "+findFunctions.size());

                    //Выбор в соответствии с предыдущими попытками
                    ArrayList<ArrayList<Integer>> sameTrys = new ArrayList<>();
                    for (ArrayList<Integer> tr:currentState.getTrys()) {
                        if(tr.size()-trye.size()==1){
                            boolean same = true;
                            for (int i =0; i<trye.size();i++){
                                int index = tr.get(i);
                                int currentIndex = trye.get(i);
                                if(currentIndex!=index){
                                    same = false;
                                    break;
                                }
                            }
                            if(same){
                                sameTrys.add(tr);
                            }
                        }
                    }

                    System.out.println("Похожие попытки "+sameTrys);

                    if(sameTrys.isEmpty()){
                        System.out.println("Похожих попыток нет");
                        rightFunction = findFunctions.get(0);
                    } else {
                        System.out.println("Похожие попытки есть");
                        for (MFunction mfunction:findFunctions) {
                            boolean notUsed = true;
                            for (ArrayList<Integer> tr:sameTrys) {
                                if(tr.get(tr.size()-1)==mFunctions.indexOf(mfunction)){
                                    notUsed = false;
                                    break;
                                }
                            }
                            if(notUsed){
                                rightFunction = mfunction;
                                break;
                            }
                        }
                    }

                    if (rightFunction!=null){
                        System.out.println("Выбрана функция "+mFunctions.indexOf(rightFunction));
                        trye.add(mFunctions.indexOf(rightFunction));

                        Rule rule = rightFunction.getRule();
                        String leftPart = rule.getLeftPart();
                        String rightPart = rule.getRightPart();

                        int pos = stackString.indexOf(rightPart);

                        states.add(new State(currentState.getInput(), currentState.getStack(), currentState.getTrys(), rightFunction.getState()));
                        currentState = states.get(states.size() - 1);
                        for (int j = 0;j < rightPart.length();j++){
                            currentState.getStack().remove(pos);
                        }
                        for (int j = 0; j < leftPart.length(); j++) {
                            currentState.getStack().add(pos+j,String.valueOf(leftPart.charAt(j)));
                        }

                        System.out.println("Стек = "+currentState.getStack()
                                +", входная строка = " + currentState.getInput());

                        if(currentState.getState()==state_r&&!currentState.getInput().isEmpty()){
                            System.out.println("Терминалы не совпадают");
                            if (states.size() > 1) {
                                //Откат на несколько состояний назад
                                System.out.println("Откат на несколько состояний назад");
                                //currentState.getTrys().add(backTrys(currentState.getLastTry()));
                                states.remove(states.size()-1);
                                currentState = states.get(states.size() - 1);
                                currentState.getTrys().add(backTrys(currentState.getLastTry()));
                            } else {
                                System.out.println("Ошибка. Невозможно построить данную строку.");
                                currentState.getTrys().clear();
                                break;
                            }
                        }
                    } else {
                        System.out.println("Функций не найдено");
                        if (!currentState.getInput().isEmpty()) {
                            System.out.println("Переносим следующий терминал");
                            states.add(new State(currentState.getInput(), currentState.getStack(),
                                    new ArrayList<>(){{add(new ArrayList<>());}}, currentState.getState()));
                            currentState = states.get(states.size() - 1);
                            currentState.getStack().add(currentState.getFirstInput());
                            currentState.getInput().remove(currentState.getFirstInput());
                        } else {
                            if (states.size() > 1) {
                                //Откат на несколько состояний назад
                                System.out.println("Откат на несколько состояний назад");
                                //currentState.getTrys().add(backTrys(currentState.getLastTry()));
                                states.remove(states.size() - 1);
                                currentState = states.get(states.size() - 1);
                                currentState.getTrys().add(backTrys(currentState.getLastTry()));
                            } else {
                                System.out.println("Ошибка. Невозможно построить данную строку.");
                                currentState.getTrys().clear();
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("Функций не найдено");
                    if (!currentState.getInput().isEmpty()) {
                        System.out.println("Переносим следующий терминал");
                        states.add(new State(currentState.getInput(), currentState.getStack(),
                                new ArrayList<>(){{add(new ArrayList<>());}}, currentState.getState()));
                        currentState = states.get(states.size() - 1);
                        currentState.getStack().add(currentState.getFirstInput());
                        currentState.getInput().remove(currentState.getFirstInput());
                    } else {
                        //Откат на несколько состояний назад
                        if (states.size() > 1) {
                            //currentState.getTrys().add(backTrys(currentState.getLastTry()));
                            states.remove(states.size() - 1);
                            currentState = states.get(states.size() - 1);
                            currentState.getTrys().add(backTrys(currentState.getLastTry()));
                        } else {
                            System.out.println("Ошибка. Невозможно построить данную строку.");
                            currentState.getTrys().clear();
                            break;
                        }
                    }
                }

                System.out.println("Стек = " + currentState.getStack()
                        + ", входная строка = " + currentState.getInput()
                        + ", попытки = " + currentState.getTrys());
                scanner.nextLine();
            }

             */

        } else {
            switch (type){
                default -> System.out.println("Допущена ошибка при вводе.");
                case 0 -> System.out.println("0 тип.");
                case 1 -> System.out.println("Грамматика контекстно-зависимая.");
                case 3 -> System.out.println("Регулярная грамматика.");
            }
        }
    }

}
