package com.example.cse476.calculatorhw1.Controller;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.example.cse476.calculatorhw1.R;

public class CalculatorControllerFactory {
    private final Activity _activity;
    private CalculatorController _calculatorController;
    
    public CalculatorControllerFactory(Activity activity) {
        this._activity = activity;
    }
    
    public void DefaultCalculatorController() {
        TextView formulaView = this._activity.findViewById(R.id.textView);
        this._calculatorController = new CalculatorController(formulaView);

        HorizontalScrollView scrollView = this._activity.findViewById(R.id.formula_scroller);
        formulaView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> scrollView.fullScroll(View.FOCUS_RIGHT));

        this.InitializeDigitButtons();
        this.InitializeOperatorButtons();
    }

    private void InitializeDigitButtons() {
        Button button;
        button = this._activity.findViewById(R.id.button_number_7);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(7));

        button = this._activity.findViewById(R.id.button_number_8);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(8));

        button = this._activity.findViewById(R.id.button_number_9);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(9));

        button = this._activity.findViewById(R.id.button_number_4);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(4));

        button = this._activity.findViewById(R.id.button_number_5);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(5));

        button = this._activity.findViewById(R.id.button_number_6);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(6));

        button = this._activity.findViewById(R.id.button_number_1);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(1));

        button = this._activity.findViewById(R.id.button_number_2);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(2));

        button = this._activity.findViewById(R.id.button_number_3);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(3));

        button = this._activity.findViewById(R.id.button_number_dot);
        button.setOnClickListener(v -> this._calculatorController.EnterDot());

        button = this._activity.findViewById(R.id.button_number_0);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(0));
    }
    
    private void InitializeOperatorButtons() {
        Button button;
        button = this._activity.findViewById(R.id.button_operation_add);
        button.setOnClickListener(v -> this._calculatorController.EnterOperation('+'));

        button = this._activity.findViewById(R.id.button_operation_divide);
        button.setOnClickListener(v -> this._calculatorController.EnterOperation('/'));

        button = this._activity.findViewById(R.id.button_operation_multiply);
        button.setOnClickListener(v -> this._calculatorController.EnterOperation('x'));

        button = this._activity.findViewById(R.id.button_operation_substract);
        button.setOnClickListener(v -> this._calculatorController.EnterOperation('-'));

        button = this._activity.findViewById(R.id.button_operation_sum);
        button.setOnClickListener(v -> this._calculatorController.Calculate());

        button = this._activity.findViewById(R.id.button_operation_delete);
        button.setOnClickListener(v -> this._calculatorController.Delete());
        button.setOnLongClickListener(v -> {
            this._calculatorController.DeleteAll();
            return true;
        });
    }
}
