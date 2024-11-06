package com.example.cse476.calculatorhw1.Calculator;

public class Calculator implements ICalculator {
    private StringBuilder _currentFormula;

    @Override
    public void SolveFormula(StringBuilder formula) throws ArithmeticException, NumberFormatException {
        this._currentFormula = formula;
        if (this._currentFormula.length() == 0)
            return;

        this.SolveExtraOperators();
        this.SolveMultiplicationAndDivision();
        this.SolveAdditionAndSubtraction();
    }

    private void SolveExtraOperators() {
        this.SolveCosOperator();
        this.SolveSinOperator();
        this.SolveLogOperator();
        this.SolveSqrtOperator();
    }

    private void SolveCosOperator() {
        var indexOfCosOperator = this._currentFormula.indexOf("cos");

        while (indexOfCosOperator >= 0) {
            var cosNumber = this.FindCosSinNumber(indexOfCosOperator + 3);

            var result = Math.cos(cosNumber.Number);
            this.ValidateAndWriteResult(
                    result,
                    new MathOperationConstraints(
                            indexOfCosOperator, cosNumber.AfterOperatorIndex));

            indexOfCosOperator = this._currentFormula.indexOf("cos");
        }
    }

    private void SolveSinOperator() {
        var indexOfSinOperator = this._currentFormula.indexOf("sin");

        while (indexOfSinOperator >= 0) {
            var sinNumber = this.FindCosSinNumber(indexOfSinOperator + 3);

            var result = Math.sin(sinNumber.Number);
            this.ValidateAndWriteResult(
                    result,
                    new MathOperationConstraints(
                            indexOfSinOperator, sinNumber.AfterOperatorIndex));

            indexOfSinOperator = this._currentFormula.indexOf("sin");
        }
    }

    private ExtraOperatorNumber FindCosSinNumber(int numberIndex) {
        int afterOperatorIndex;
        int numberToAdd;
        if (this._currentFormula.charAt(numberIndex) == '-') {
            afterOperatorIndex = ICalculator.FindIndexOfFirstOperation(
                    this._currentFormula.substring(numberIndex + 1));
            numberToAdd = numberIndex + 1;
        }
        else {
            afterOperatorIndex = ICalculator.FindIndexOfFirstOperation(
                    this._currentFormula.substring(numberIndex));
            numberToAdd = numberIndex;
        }

        if (afterOperatorIndex == -1)
            afterOperatorIndex = this._currentFormula.length();
        else
            afterOperatorIndex += numberToAdd;

        var number = Double.parseDouble(
                this._currentFormula.substring(
                        numberIndex, afterOperatorIndex));

        return new ExtraOperatorNumber(number, afterOperatorIndex);
    }

    private void SolveLogOperator() {
        var indexOfLogOperator = this._currentFormula.indexOf("log");

        while (indexOfLogOperator >= 0) {
            var sqrtNumber = this.FindLogSqrtNumber(indexOfLogOperator + 3);

            var result = Math.log10(sqrtNumber.Number);
            this.ValidateAndWriteResult(
                    result,
                    new MathOperationConstraints(
                            indexOfLogOperator, sqrtNumber.AfterOperatorIndex));

            indexOfLogOperator = this._currentFormula.indexOf("log");
        }
    }

    private void SolveSqrtOperator() {
        var indexOfSqrtOperator = this._currentFormula.indexOf("√");

        while (indexOfSqrtOperator >= 0) {
            var sqrtNumber = this.FindLogSqrtNumber(indexOfSqrtOperator + 1);

            var result = Math.sqrt(sqrtNumber.Number);
            this.ValidateAndWriteResult(
                    result,
                    new MathOperationConstraints(
                            indexOfSqrtOperator, sqrtNumber.AfterOperatorIndex));

            indexOfSqrtOperator = this._currentFormula.indexOf("√");
        }
    }

    private ExtraOperatorNumber FindLogSqrtNumber(int numberIndex) {
        var afterOperatorIndex = ICalculator.FindIndexOfFirstOperation(
                this._currentFormula.substring(numberIndex));

        if (afterOperatorIndex == -1)
            afterOperatorIndex = this._currentFormula.length();
        else
            afterOperatorIndex += numberIndex;

        var number = Double.parseDouble(
                this._currentFormula.substring(
                        numberIndex, afterOperatorIndex));

        return new ExtraOperatorNumber(number, afterOperatorIndex);
    }

    private void SolveMultiplicationAndDivision() {
        var indexOfMultiplicationOperation = this._currentFormula.indexOf("x");
        var indexOfDivisionOperation = this._currentFormula.indexOf("/");

        while (indexOfMultiplicationOperation >= 0 || indexOfDivisionOperation >= 0) {
            var operationIndexToProcess = DetermineNextOperation(
                    indexOfMultiplicationOperation, indexOfDivisionOperation);
            var operationIsMultiply = operationIndexToProcess == indexOfMultiplicationOperation;

            var constraints = this.CreateConstraintsForBasicOperators(operationIndexToProcess);

            var leftNumber = Double.parseDouble(
                    this._currentFormula.substring(
                            constraints.LeftNumberStartIndex, operationIndexToProcess));
            var rightNumber = Double.parseDouble(
                    this._currentFormula.substring(
                            operationIndexToProcess + 1, constraints.RightNumberEndIndex));

            double result;
            if (operationIsMultiply)
                result = leftNumber * rightNumber;
            else
                result = leftNumber / rightNumber;

            this.ValidateAndWriteResult(result, constraints);

            indexOfMultiplicationOperation = this._currentFormula.indexOf("x");
            indexOfDivisionOperation = this._currentFormula.indexOf("/");
        }
    }

    private void SolveAdditionAndSubtraction() {
        var indexOfAdditionOperation = this._currentFormula.indexOf("+");

        int indexOfSubtractionOperation;
        if (this._currentFormula.charAt(0) == '-')
            indexOfSubtractionOperation = this._currentFormula.indexOf("-", 1);
        else
            indexOfSubtractionOperation = this._currentFormula.indexOf("-");

        while (indexOfAdditionOperation >= 0 || indexOfSubtractionOperation >= 0) {
            var operationIndexToProcess = DetermineNextOperation(
                    indexOfAdditionOperation, indexOfSubtractionOperation);
            var operationIsAddition = operationIndexToProcess == indexOfAdditionOperation;

            var constraints = this.CreateConstraintsForBasicOperators(operationIndexToProcess);

            var leftNumber = Double.parseDouble(
                    this._currentFormula.substring(
                            constraints.LeftNumberStartIndex, operationIndexToProcess));
            var rightNumber = Double.parseDouble(
                    this._currentFormula.substring(
                            operationIndexToProcess + 1, constraints.RightNumberEndIndex));

            double result;
            if (operationIsAddition)
                result = leftNumber + rightNumber;
            else
                result = leftNumber - rightNumber;

            this.ValidateAndWriteResult(result, constraints);

            indexOfAdditionOperation = this._currentFormula.indexOf("+");
            if (this._currentFormula.charAt(0) == '-')
                indexOfSubtractionOperation = this._currentFormula.substring(1).indexOf("-");
            else
                indexOfSubtractionOperation = this._currentFormula.indexOf("-");
        }
    }

    private MathOperationConstraints CreateConstraintsForBasicOperators(int operationIndex) {
        var leftNumberStartIndex = ICalculator.FindIndexOfLastOperation(
                _currentFormula.substring(0, operationIndex)) + 1;
        if (
                leftNumberStartIndex != 0 &&
                        _currentFormula.charAt(leftNumberStartIndex - 1) == '-'
        )
            leftNumberStartIndex--;

        var rightNumberEndIndex = ICalculator.FindIndexOfFirstOperation(
                _currentFormula.substring(operationIndex + 1));
        if (rightNumberEndIndex == -1) {
            rightNumberEndIndex = _currentFormula.length();
        }
        else {
            rightNumberEndIndex += operationIndex + 1;
            if (
                    _currentFormula.charAt(rightNumberEndIndex) == '-' &&
                            (rightNumberEndIndex - 1) == operationIndex
            ) {
                var newIndex = ICalculator.FindIndexOfFirstOperation(
                        _currentFormula.substring(rightNumberEndIndex + 1));
                if (newIndex == -1)
                    rightNumberEndIndex = _currentFormula.length();
                else
                    rightNumberEndIndex += newIndex + 1;
            }
        }

        return new MathOperationConstraints(leftNumberStartIndex, rightNumberEndIndex);
    }

    private void ValidateAndWriteResult(double result, MathOperationConstraints constraints) {
        // Throw ArithmeticException they are both treated as NaN
        if (Double.isNaN(result) || Double.isInfinite(result))
            throw new ArithmeticException();

        Object resultAsObject;
        if (Math.floor(result) == result)
            resultAsObject = (long) result;
        else
            resultAsObject = result;

        this._currentFormula.replace(
                constraints.LeftNumberStartIndex,
                constraints.RightNumberEndIndex,
                resultAsObject.toString());

        // Fix double minus operator
        if (constraints.LeftNumberStartIndex == 0)
            return;

        if (this._currentFormula.charAt(constraints.LeftNumberStartIndex) != '-')
            return;

        if (this._currentFormula.charAt(constraints.LeftNumberStartIndex - 1) != '-')
            return;

        var replace = "+";
        if (constraints.LeftNumberStartIndex - 2 >= 0 &&
                ICalculator.IsOperator(this._currentFormula.charAt(constraints.LeftNumberStartIndex)))
            replace = "";

        this._currentFormula.replace(
                constraints.LeftNumberStartIndex - 1,
                constraints.LeftNumberStartIndex + 1,
                replace);
    }

    private static int DetermineNextOperation(
            int indexOfOperation0,
            int indexOfOperation1) {
        if (indexOfOperation0 >= 0 && indexOfOperation1 < 0) {
            return indexOfOperation0;
        }

        if (indexOfOperation0 < 0) {
            return indexOfOperation1;
        }

        return Math.min(indexOfOperation0, indexOfOperation1);
    }

    private static class ExtraOperatorNumber {
        public double Number;
        public int AfterOperatorIndex;

        public ExtraOperatorNumber(double number, int after) {
            this.Number = number;
            this.AfterOperatorIndex = after;
        }
    }

    private static class MathOperationConstraints {
        public int LeftNumberStartIndex;
        public int RightNumberEndIndex;

        public MathOperationConstraints(int left, int right) {
            this.LeftNumberStartIndex = left;
            this.RightNumberEndIndex = right;
        }
    }
}
