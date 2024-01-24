package com.projectfkklp.saristorepos.activities.pos.checkout;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private final int minValue;
    private final int maxValue;

    public InputFilterMinMax(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        try {
            // Parse the current input string and check if it's within the range
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(minValue, maxValue, input)) {
                return null; // Accept the input
            }
        } catch (NumberFormatException ignored) {
            // Ignore the exception
        }
        // Reject the input if it's outside the range
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
