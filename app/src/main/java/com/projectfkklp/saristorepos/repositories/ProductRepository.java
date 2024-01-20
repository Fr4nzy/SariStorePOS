package com.projectfkklp.saristorepos.repositories;

import com.projectfkklp.saristorepos.models._Product;

import java.util.HashMap;

public class ProductRepository {

    public ProductRepository() {
    }

    public static HashMap<Integer, _Product> getDummyProductHashMap(){
        HashMap<Integer, _Product> dummyProductsHashmap = new HashMap<>();

        dummyProductsHashmap.put(1,new _Product(
            "2VRgnU2kZ53a2gzmYPJI",
            1,
            "sky flakes",
            "https://firebasestorage.googleapis.com/v0/b/sari-store-pos.appspot.com/o/Android%20Images%2Fstorage%2Femulated%2F0%2FDCIM%2FCamera%2FIMG_20231220_234607.jpg?alt=media&token=f2553f35-d12a-4dec-81f9-35f5d37861a8",
            1000
        ));

        dummyProductsHashmap.put(5,new _Product(
                "rSYFiGWe0H3uKz9O5qaz",
                5,
                "Minute Maid",
                "https://firebasestorage.googleapis.com/v0/b/sari-store-pos.appspot.com/o/Android%20Images%2F1000006946?alt=media&token=13019a6f-ddfb-4a5f-9fb7-afba9c280625",
                1000
        ));

        dummyProductsHashmap.put(10,new _Product(
                "1W5qiEZ7JUAZXgBT2OpE",
                10,
                "Great Taste White",
                "https://firebasestorage.googleapis.com/v0/b/sari-store-pos.appspot.com/o/Android%20Images%2F1000006946?alt=media&token=13019a6f-ddfb-4a5f-9fb7-afba9c280625",
                1000
        ));

        dummyProductsHashmap.put(20,new _Product(
                "9xXxr8Px7RIXm4kIQdHa",
                20,
                "555 Tuna Caldereta 155g",
                "https://firebasestorage.googleapis.com/v0/b/sari-store-pos.appspot.com/o/Android%20Images%2F1000006944?alt=media&token=f079c3ff-8b28-4b60-a968-058a960d77c7",
                1000
        ));

        dummyProductsHashmap.put(1000,new _Product(
                "9xXxr8Px7RIXm4kIQdHc",
                1000,
                "Kitkat",
                "https://firebasestorage.googleapis.com/v0/b/sari-store-pos.appspot.com/o/Android%20Images%2F1000006944?alt=media&token=f079c3ff-8b28-4b60-a968-058a960d77c7",
                1000
        ));

        dummyProductsHashmap.put(100,new _Product(
                "9xXxr8Px7RIXm4kIQdHd",
                100,
                "Eden Cheese",
                "https://firebasestorage.googleapis.com/v0/b/sari-store-pos.appspot.com/o/Android%20Images%2F1000006944?alt=media&token=f079c3ff-8b28-4b60-a968-058a960d77c7",
                1000
        ));



        return dummyProductsHashmap;
    }
}
