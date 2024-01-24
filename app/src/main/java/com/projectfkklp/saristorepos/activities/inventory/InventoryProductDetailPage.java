package com.projectfkklp.saristorepos.activities.inventory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.utils.StringUtils;

public class InventoryProductDetailPage extends AppCompatActivity {
    private Product product;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_product_detail_page);

        product = getIntent().getSerializableExtra("Product", Product.class);

        initializeViews();

    }

    private void initializeViews() {
        TextView titleText = findViewById(R.id.product_detail_title);
        EditText detailPriceText = findViewById(R.id.detailPriceText);
        EditText detailStockText = findViewById(R.id.detailStockText);
        EditText detailProductText = findViewById(R.id.detailProductText);

        titleText.setText(StringUtils.isNullOrEmpty(product.getId()) ? "Create Product" : "Edit Product");
        detailPriceText.setText(String.valueOf(product.getUnitPrice()));
        detailProductText.setText(String.valueOf(product.getName()));
        detailStockText.setText(String.valueOf(product.getStocks()));
    }



}
