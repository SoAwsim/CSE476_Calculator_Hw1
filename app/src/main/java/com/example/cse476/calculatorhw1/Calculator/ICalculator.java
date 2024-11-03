package com.example.cse476.calculatorhw1.Calculator;

public interface ICalculator {
    char[] OPERATORS = {'+', '-', 'x', '/'};
    void SolveFormula(StringBuilder formula);

    static boolean IsOperator(char character) {
        for (var c : OPERATORS) {
            if (c == character)
                return true;
        }

        return false;
    }

    static int FindIndexOfLastOperation(String formula) {
        if (formula.isEmpty())
            return -1;

        var indexOfLastOperator = Integer.MIN_VALUE;
        for (var c : OPERATORS) {
            var foundIndex = formula.lastIndexOf(c);
            if (foundIndex > indexOfLastOperator)
                indexOfLastOperator = foundIndex;
        }
        return indexOfLastOperator;
    }

    static int FindIndexOfFirstOperation(String formula) {
        if (formula.isEmpty())
            return -1;

        var indexOfFirstOperator = -1;
        for (var c : OPERATORS) {
            var foundIndex = formula.indexOf(c);
            if (foundIndex == -1)
                continue;

            if (indexOfFirstOperator == -1 || foundIndex < indexOfFirstOperator)
                indexOfFirstOperator = foundIndex;
        }
        return indexOfFirstOperator;
    }
}
