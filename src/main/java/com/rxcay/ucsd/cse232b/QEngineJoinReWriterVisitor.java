package com.rxcay.ucsd.cse232b;

import com.rxcay.ucsd.cse232b.antlr4.XQueryBaseVisitor;
import com.rxcay.ucsd.cse232b.antlr4.XQueryParser;

import java.util.*;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 3/11/22 8:57 AM
 * @description
 */
public class QEngineJoinReWriterVisitor extends XQueryBaseVisitor<String> {
    public static final String NO_CHANGE_MARK = "original";
    @Override
    public String visitFLWR(XQueryParser.FLWRContext ctx) {
        HashMap<String, Integer> varGroup = new HashMap<>(); // store the group of each variable
        HashMap<String, Integer> varIndex = new HashMap<>(); // store the index of each variable
        Integer varNum = ctx.forClause().var().size(); // number of variables
        for (int i = 0; i < varNum; i++) {
            String var = ctx.forClause().var(i).getText();
            String path = ctx.forClause().xq(i).getText();

            // A variable starts with "$".
            if (path.substring(0, 1).equals("$")) { // variable
                // A child is in the same group as its parent.
                String parentVar = path.split("/")[0];
                varGroup.put(var, varGroup.get(parentVar));
            }
            else { // document
                varGroup.put(var, i);
            }
            varIndex.put(var, i);
        }

        HashMap<Integer, LinkedList<String>> groupVar = new HashMap<>(); // store the variables of a given group
        for (int i = 0; i < varNum; i ++) {
            String key = ctx.forClause().var(i).getText();
            Integer val = varGroup.get(key);

            if (!groupVar.containsKey(val)) { // new group
                LinkedList<String> newList = new LinkedList<>();
                groupVar.put(val, newList); // create a new group
            }
            groupVar.get(val).add(key); // add a variable to the group
        }
        if (groupVar.size() == 1) {
            return NO_CHANGE_MARK;
        }

        HashMap<Integer, LinkedList<String[]>> groupConst = new HashMap<>(); // store the constant conditions of a given group
        HashMap<String, LinkedList<String[]>> groupsCond = new HashMap<>(); // store the conditions related to 2 groups
        String[] conditions = ctx.whereClause().getText().substring(5).split("and"); // the conditions of a where clause
        String[] eqCondition; // store the split condition
        for (int i = 0; i < conditions.length; i++) {
            String curCond = conditions[i];

            // We have to split with "=", "eq$", or "eq\"" because "eq" can be a variable name.
            if (curCond.contains("=")) {
                eqCondition = curCond.split("=");
            } else if (curCond.contains("eq$")) {
                eqCondition = curCond.split("eq\\$");
                eqCondition[1] = "$" + eqCondition[1];
            } else {
                eqCondition = curCond.split("eq\"");
                eqCondition[1] = "\"" + eqCondition[1];
            }

            // constant condition => push the condition down
            if (curCond.contains("\"")) {
                boolean isFirstConstant = eqCondition[0].substring(0, 1).equals("\"");
                boolean isSecondConstant = eqCondition[1].substring(0, 1).equals("\"");

                // This case will not be included in the test cases.
                // (1) both constants => push to every group
                if (isFirstConstant && isSecondConstant) {
                    for (Integer key : groupVar.keySet()) {
                        if (!groupConst.containsKey(key)){
                            LinkedList<String[]> newList = new LinkedList<>();
                            groupConst.put(key, newList);
                        }
                        groupConst.get(key).add(eqCondition);
                    }
                }
                // one constant
                else {
                    int group;
                    // (2) first constant
                    if (isFirstConstant) {
                        group = varGroup.get(eqCondition[1]);
                    }
                    // (3) second constant
                    else {
                        group = varGroup.get(eqCondition[0]);
                    }

                    if (!groupConst.containsKey(group)){
                        LinkedList<String[]> newList = new LinkedList<>();
                        groupConst.put(group, newList);
                    }
                    groupConst.get(group).add(eqCondition);
                }
            }
            // no constants => variable conditions
            else {
                int group1 = varGroup.get(eqCondition[0]);
                int group2 = varGroup.get(eqCondition[1]);

                if (group1 == group2) { // similar to constant conditions
                    if (!groupConst.containsKey(group1)) {
                        LinkedList<String[]> newList = new LinkedList<>();
                        groupConst.put(group1, newList);
                    }
                    groupConst.get(group1).add(eqCondition);
                }
                else {
                    if (group1 > group2) {
                        int temp = group1;
                        group1 = group2;
                        group2 = temp;

                        String tempString = eqCondition[0];
                        eqCondition[0] = eqCondition[1];
                        eqCondition[1] = tempString;
                    }

                    String key = group1 + "," + group2 ;
                    if (!groupsCond.containsKey(key)){
                        LinkedList<String[]> val = new LinkedList<>();
                        groupsCond.put(key, val);
                    }
                    groupsCond.get(key).add(eqCondition);
                }
            }
        }

        HashMap<Integer, String> finalForGroup = new HashMap<>();
        // prepare the for blocks
        for (Map.Entry<Integer, LinkedList<String>> set : groupVar.entrySet()) {
            int curGroup = set.getKey();
            String blockString = "for " + ctx.forClause().var(curGroup).getText() + " in " + ctx.forClause().xq(curGroup).getText() + ",\n";
            for (String curVar : set.getValue()) {
                int curIndex =  varIndex.get(curVar);
                if (curGroup != curIndex) { // not the first index of the group
                    blockString += curVar + " in " + ctx.forClause().xq(curIndex).getText() + ",\n";
                }
            }
            blockString = blockString.substring(0, blockString.length() - 2); // strip ", "
            blockString += "\n";
            finalForGroup.put(curGroup, blockString);
        }

        // add the pushed down where clause, ex. constant conditions
        for (Map.Entry<Integer, LinkedList<String[]>> set : groupConst.entrySet()) {
            int curGroup = set.getKey();
            String blockString = finalForGroup.get(curGroup);
            blockString += "where ";
            for (String[] eqCond : set.getValue()) {
                blockString += eqCond[0] + " eq " + eqCond[1] + " and ";
            }
            blockString = blockString.substring(0, blockString.length() - 5); // strip the and part
            blockString += "\n";
            finalForGroup.put(curGroup, blockString);
        }

        // add return clause
        for (Map.Entry<Integer, LinkedList<String>> set : groupVar.entrySet()) {
            int curGroup = set.getKey();
            String blockString = finalForGroup.get(curGroup);
            blockString += "return <tuple>{ ";
            for (String curVar : set.getValue()){
                blockString += String.format("<%s>{%s}</%s>, ", curVar.substring(1), curVar, curVar.substring(1));
            }
            blockString = blockString.substring(0, blockString.length() - 2); // strip ", "
            blockString += " }</tuple>,\n";
            finalForGroup.put(curGroup, blockString);
        }

        // join the results
        HashMap<HashSet<Integer>, String> finalResult = new HashMap<>(); // store the joined groups
        for (Integer group : finalForGroup.keySet()) { // store original results initially
            HashSet<Integer> newList = new HashSet<>();
            newList.add(group);
            finalResult.put(newList, finalForGroup.get(group));
        }

        for (String key : groupsCond.keySet()) {
            String finalString = "join (\n\n";
            int group1 = Integer.valueOf(key.split(",")[0]);
            int group2 = Integer.valueOf(key.split(",")[1]);
            String groupString1 = "";
            String groupString2 = "";
            HashSet<Integer> newList = new HashSet<>();

            // get the result of the first group
            for (HashSet<Integer> listKey : finalResult.keySet()) {
                if (listKey.contains(group1)) {
                    groupString1 = finalResult.get(listKey);
                    finalResult.remove(listKey);
                    newList.addAll(listKey);
                    break;
                }
            }

            // get the result of the second group
            for (HashSet<Integer> listKey : finalResult.keySet()) {
                if (listKey.contains(group2)) {
                    groupString2 = finalResult.get(listKey);
                    finalResult.remove(listKey);
                    newList.addAll(listKey);
                    break;
                }
            }

            finalString += groupString1 + "\n" + groupString2 + "\n";
            String list1 = "[";
            String list2 = "[";
            for (String[] eqCond : groupsCond.get(key)) {
                list1 += eqCond[0].substring(1) + ",";
                list2 += eqCond[1].substring(1) + ",";
            }
            list1 = list1.substring(0, list1.length() - 1) + "], "; // strip the last comma
            list2 = list2.substring(0, list2.length() - 1) + "]";
            finalString += list1 + list2;
            finalString += "),\n";
            finalResult.put(newList, finalString);
        }

        // join all unrelated groups
        List<HashSet<Integer>> keyList = new ArrayList<>();
        keyList.addAll(finalResult.keySet());
        for (int i = keyList.size() - 1; i > 0; i--) {
            HashSet<Integer> key1 = keyList.remove(i) ;
            HashSet<Integer> key2 = keyList.remove(i - 1);
            String groupString1 = finalResult.remove(key1);
            String groupString2 = finalResult.remove(key2);

            String finalString = "join (\n" + groupString1 + "\n" + groupString2 + "\n" + "[], []),\n";

            key1.addAll(key2);
            keyList.add(key1);
            finalResult.put(key1, finalString);
        }

        // get the final query
        String finalQuery = "for $tuple in ";
        String finalString = finalResult.values().toString();
        finalString = finalString.substring(1, finalString.length() - 3); // get rid of the brackets, the last comma and the last \n
        finalQuery += finalString + "\n";

        // return clause
        String returnString = ctx.returnClause().getText();
        boolean isVariable = false;
        for (int i = 0; i < returnString.length(); i++) {
            char curCh = returnString.charAt(i);
            if (!Character.isDigit(curCh) && !Character.isLetter(curCh) && isVariable) {
                // OMG! this replacement is dummy. Caused wrong grammar while evaluating.
                //TODO: bugfix: add bracket for correct grammar of commaXQ. [fixed]
                returnString = returnString.substring(0, i) + "/*)" + returnString.substring(i);
                isVariable = false;
            }
            if (curCh == '$') {
                isVariable = true;
            }
        }
        // bracket linear match
        returnString = returnString.replace("$", "($tuple/");
        finalQuery += returnString;

        return finalQuery;
    }
}
