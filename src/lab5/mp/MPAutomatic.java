package lab5.mp;

import lab1.Grammar;
import lab1.GrammarTypes;
import lab1.Rule;
import lab5.MFunction;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import static lab5.MFunction.state_q;

public class MPAutomatic {

    public static void main(String[] args) {

        //Ввод грамматики
        Grammar grammar = GrammarTypes.enterGrammar();
        //Grammar grammar = new Grammar(new ArrayList<>() {{add("+");add("(");add(")");add("a");}},
                //new ArrayList<>(){{add("S");add("A");}},
                //new ArrayList<>(){{add(new Rule("S","S+A|A"));add(new Rule("A","(S)|a"));}});
        int type = GrammarTypes.resolveGrammarType(grammar);

        //Проверка на контекстно-свободную
        if(type==2){
            System.out.println("Грамматика контекстно-свободная.");

            //Терминалы
            ArrayList<String> terminals = grammar.getTerminalAL();

            //Не терминалы
            ArrayList<String> notTerminals = grammar.getNotTerminalAL();

            //Правила
            ArrayList<Rule> p = grammar.getP();

            //Магазинные функции
            ArrayList<MFunction> mFunctions =  new ArrayList<>();
            for (Rule rule: p) {
                for(String string:rule.getRightPartArray()){
                    Rule rule1 = new Rule(rule.getLeftPart(),string);
                    MFunction mFunction = new MFunction(rule1, state_q);
                    mFunctions.add(mFunction);

                    System.out.printf(Locale.getDefault(), "%d) F(%s,%s,%s)=(%s,%s)%n",
                            mFunctions.indexOf(mFunction)+1,"q","ε",rule1.getLeftPart(),
                            "q",rule1.getRightPart());
                }
            }

            p.add(new Rule("ε",""));
            MFunction mFunction = new MFunction(p.get(p.size()-1), state_q);
            mFunctions.add(mFunction);

            //Магазин (стек)
            ArrayList<String> stack = new ArrayList<>();
            stack.add(p.get(0).getLeftPart());

            //Входная строка
            ArrayList<String> input = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите входную строку.");
            String inputString = scanner.nextLine();
            for (int i=0; i<inputString.length();i++){
                input.add(String.valueOf(inputString.charAt(i)));
            }

            ArrayList<State> states = new ArrayList<>();
            ArrayList<ArrayList<Integer>> trys = new ArrayList<>();

            states.add(new State(input,stack, state_q));
            trys.add(new ArrayList<>());

            State currentState = states.get(0);

            //Программа строит граф
            while (!currentState.getStack().isEmpty()){

                //System.out.println("Попытки "+trys);

                //System.out.println("Символ стека = "+currentState.getFirstStack()
                       // +", символ входной строки = " + currentState.getFirstInput());
                //System.out.println("Стек = "+currentState.getStack()
                        //+", входная строка = " + currentState.getInput());

                if (terminals.contains(currentState.getFirstStack())){
                    if(currentState.getFirstStack().equals(currentState.getFirstInput())) {
                        currentState.getStack().remove(currentState.getFirstStack());
                        currentState.getInput().remove(currentState.getFirstInput());
                    } else {
                        //System.out.println("Терминалы не совпадают 1");
                        //Откат на несколько состояний назад
                        trys.add(backTrys(trys.get(trys.size()-1)));
                        states.remove(states.size()-1);
                        currentState = states.get(states.size()-1);
                    }
                } else {
                    ArrayList<MFunction> findFunctions = new ArrayList<>();
                    for (MFunction mfunction:mFunctions) {
                        if(mfunction.getRule().getLeftPart().equals(currentState.getFirstStack())){
                            findFunctions.add(mfunction);
                        }
                    }
                    if(!findFunctions.isEmpty()){
                        MFunction rightFunction = null;
                        ArrayList<Integer> trye = trys.get(trys.size()-1);
                        //System.out.println("Найдено функций для "+currentState.getFirstStack()+" "+findFunctions.size());
                        //Выбор в соответствии с предыдущими попытками
                        ArrayList<ArrayList<Integer>> sameTrys = new ArrayList<>();
                        for (ArrayList<Integer> tr:trys) {
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

                        //System.out.println("Похожие попытки "+sameTrys);

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
                            //System.out.println("Выбрана функция "+mFunctions.indexOf(rightFunction));
                            trye.add(mFunctions.indexOf(rightFunction));

                            Rule rule = rightFunction.getRule();
                            String rightPart = rule.getRightPart();

                            states.add(new State(currentState.getInput(),currentState.getStack(), rightFunction.getState()));
                            currentState = states.get(states.size()-1);

                            currentState.getStack().remove(currentState.getFirstStack());

                            for (int i=0;i<rightPart.length();i++){
                                currentState.getStack().add(i,String.valueOf(rightPart.charAt(i)));
                            }

                            //System.out.println("Стек = "+currentState.getStack()
                                    //+", входная строка = " + currentState.getInput());

                            if(!currentState.checkRight(terminals, notTerminals)){
                                //System.out.println("Терминалы не совпадают 2");
                                //Откат на несколько состояний назад
                                trys.add(backTrys(trys.get(trys.size()-1)));
                                states.remove(states.size()-1);
                                currentState = states.get(states.size()-1);
                            }
                        } else {
                            //System.out.println("Функций не найдено");
                            //Откат на несколько состояний назад
                            if(states.size()>1) {
                                trys.add(backTrys(trys.get(trys.size()-1)));
                                states.remove(states.size()-1);
                                currentState = states.get(states.size() - 1);
                            } else {
                                System.out.println("Ошибка. Невозможно построить данную строку.");
                                trys.clear();
                                break;
                            }
                        }
                    } else {
                        System.out.println("Ошибка. Несоответствие алфавиту.");
                        trys.clear();
                        break;
                    }
                }
                //scanner.nextLine();
            }

            //Программа показывает что ей удалось найти
            //Проверка на наличие графа
            if (!trys.isEmpty()) {
                ArrayList<Integer> lastTry = trys.get(trys.size() - 1);

                //System.out.println("Последняя последовательность "+lastTry);

                int currentIndex = lastTry.get(0);

                currentState = new State(input, stack, state_q);

                System.out.println("Стек = " + currentState.getStack()
                        + ", входная строка = " + currentState.getInput());

                while (!currentState.getStack().isEmpty()) {
                    if (terminals.contains(currentState.getFirstStack())) {
                        currentState.getStack().remove(currentState.getFirstStack());
                        currentState.getInput().remove(currentState.getFirstInput());
                    } else {
                        int selected = lastTry.get(currentIndex++);
                        MFunction rightFunction = mFunctions.get(selected);

                        Rule rule = rightFunction.getRule();
                        String rightPart = rule.getRightPart();

                        currentState.getStack().remove(currentState.getFirstStack());

                        for (int i = 0; i < rightPart.length(); i++) {
                            currentState.getStack().add(i, String.valueOf(rightPart.charAt(i)));
                        }
                    }
                    System.out.println("Стек = " + currentState.getStack()
                            + ", входная строка = " + currentState.getInput());
                    //scanner.nextLine();
                }
                System.out.println("Строка принята");
            } else {
                System.out.println("Строка не принята");
            }

        } else {
            switch (type){
                default -> System.out.println("Допущена ошибка при вводе.");
                case 0 -> System.out.println("0 тип.");
                case 1 -> System.out.println("Грамматика контекстно-зависимая.");
                case 3 -> System.out.println("Регулярная грамматика.");
            }
        }
    }

    public static ArrayList<Integer> backTrys(ArrayList<Integer>trys){
        ArrayList<Integer> trysBack = new ArrayList<>(trys);
        trysBack.remove(trysBack.size() - 1);
        return trysBack;

        /*
        if(!trys.isEmpty()) {
            ArrayList<Integer> trysBack = new ArrayList<>(trys);
            trysBack.remove(trysBack.size() - 1);
            return trysBack;
        } else {
            return new ArrayList<>();
        }
        */
    }

}
