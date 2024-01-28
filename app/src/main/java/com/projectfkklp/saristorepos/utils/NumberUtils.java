package com.projectfkklp.saristorepos.utils;

import com.github.mikephil.charting.data.PieData;

import java.util.List;

public class NumberUtils {
    public static float[] convertToFloatArray(List<Double> doubleList) {
        float[] floatArray = new float[doubleList.size()];

        for (int i = 0; i < doubleList.size(); i++) {
            floatArray[i] = doubleList.get(i).floatValue();
        }

        return floatArray;
    }

    public static float convertToFloat(Double doubleValue) {
        return doubleValue != null ? doubleValue.floatValue() : 0f;
    }

}
