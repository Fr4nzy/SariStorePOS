package com.projectfkklp.saristorepos.utils;

import java.util.List;

public class NumberUtils {
    public static float[] convertToFloatArray(List<Double> doubleList) {
        float[] floatArray = new float[doubleList.size()];

        for (int i = 0; i < doubleList.size(); i++) {
            floatArray[i] = doubleList.get(i).floatValue();
        }

        return floatArray;
    }

}
