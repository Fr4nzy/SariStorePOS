package com.projectfkklp.saristorepos.repositories;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.projectfkklp.saristorepos.enums.Status;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models._Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ProductRepository {

    public ProductRepository() {
    }

    public static Task<List<Product>> getActiveProducts(Context context){
        return StoreRepository
            .getStoreById(SessionRepository.getCurrentStore(context).getId())
            .continueWithTask(task->{
                Store store = task.getResult().toObject(Store.class);
                assert store != null;
                List<Product> activeProducts = store.getProducts()
                    .stream()
                    .filter(p->p.getStatus()== Status.ACTIVE)
                    .collect(Collectors.toList());

                return Tasks.forResult(activeProducts);
            });
    }

    public static List<Product> getDummyProducts(){
        List<Product> dummyProducts = new ArrayList<>();

        dummyProducts.add(new Product(
            "2VRgnU2kZ53a2gzmYPJI",
            "Candy",
            1000,
            1,
            "",
            ""
        ));

        dummyProducts.add(new Product(
                "rSYFiGWe0H3uKz9O5qaz",
                "Minute Maid",
                1000,
                5,
                "",
                ""
        ));

        dummyProducts.add(new Product(
                "1W5qiEZ7JUAZXgBT2OpE",
                "Coffee",
                1000,
                10,
                "",
                ""
        ));

        dummyProducts.add(new Product(
                "9xXxr8Px7RIXm4kIQdHa",
                "Canned Tuna",
                1000,
                20,
                "",
                ""
        ));

        dummyProducts.add(new Product(
                "9xXxr8Px7RIXm4kIQdHc",
                "Chocolate",
                1000,
                100,
                "",
                ""
        ));

        dummyProducts.add(new Product(
                "9xXxr8Px7RIXm4kIQdHd",
                "Wine",
                1000,
                1000,
                "",
                ""
        ));

        return dummyProducts;
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
