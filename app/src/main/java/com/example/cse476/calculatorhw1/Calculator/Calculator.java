package com.example.cse476.calculatorhw1.Calculator;

public class Calculator implements ICalculator {
    @Override
    public void SolveFormula(StringBuilder formula) throws ArithmeticException, NumberFormatException {
        SolveMultiplicationAndDivision(formula);
        SolveAdditionAndSubtraction(formula);
    }

    private static void SolveMultiplicationAndDivision(StringBuilder formula) {
        var indexOfMultiplicationOperation = formula.indexOf("x");
        var indexOfDivisionOperation = formula.indexOf("/");

        while (indexOfMultiplicationOperation >= 0 || indexOfDivisionOperation >= 0) {
            int operationIndexToProcess;
            boolean operationIsMultiply;

            operationIndexToProcess = DetermineNextOperationIsMultiplyOrDivision(
                    indexOfMultiplicationOperation, indexOfDivisionOperation);
            operationIsMultiply = operationIndexToProcess == indexOfMultiplicationOperation;

            var leftNumberStart = ICalculator.FindIndexOfLastOperation(
                    formula.substring(0, operationIndexToProcess)) + 1;
            if (leftNumberStart != 0 && formula.charAt(leftNumberStart - 1) == '-')
                leftNumberStart--;

            var rightNumberEnd = ICalculator.FindIndexOfFirstOperation(formula.substring(operationIndexToProcess + 1));
            if (rightNumberEnd == -1) {
                rightNumberEnd = formula.length();
            }
            else {
                rightNumberEnd = rightNumberEnd + operationIndexToProcess + 1;
                if (formula.charAt(rightNumberEnd) == '-' && (rightNumberEnd - 1) == operationIndexToProcess) {
                    var newIndex = ICalculator.FindIndexOfFirstOperation(formula.substring(rightNumberEnd + 1));
                    if (newIndex == -1)
                        rightNumberEnd = formula.length();
                    else
                        rightNumberEnd = newIndex + rightNumberEnd + 1;
                }
            }

            var leftNumber = Double.parseDouble(
                    formula.substring(leftNumberStart, operationIndexToProcess));
            var rightNumber = Double.parseDouble(
                    formula.substring(operationIndexToProcess + 1, rightNumberEnd));

            double result;
            if (operationIsMultiply)
                result = leftNumber * rightNumber;
            else
                result = leftNumber / rightNumber;

            // throw ArithmeticException they are both treated as NaN
            if (Double.isNaN(result) || Double.isInfinite(result))
                throw new ArithmeticException();

            Object resultAsObject;
            if (Math.floor(result) == result)
                resultAsObject = (int) result;
            else
                resultAsObject = result;

            formula.replace(leftNumberStart, rightNumberEnd, resultAsObject.toString());

            indexOfMultiplicationOperation = formula.indexOf("x");
            indexOfDivisionOperation = formula.indexOf("/");
        }
    }

    private static void SolveAdditionAndSubtraction(StringBuilder formula) {

    }

    private static int DetermineNextOperationIsMultiplyOrDivision(
            int indexOfMultiplicationOperation,
            int indexOfDivisionOperation
    ) {
        if (indexOfMultiplicationOperation >= 0 && indexOfDivisionOperation < 0) {
            return indexOfMultiplicationOperation;
        }

        if (indexOfMultiplicationOperation < 0) {
            return indexOfDivisionOperation;
        }

        return Math.min(indexOfMultiplicationOperation, indexOfDivisionOperation);
    }
}
