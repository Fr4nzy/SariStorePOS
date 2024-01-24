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
    private TextView titleText;
    private EditText detailPriceText, detailProductText, detailStockText;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_product_detail_page);

        product = getIntent().getSerializableExtra("Product", Product.class);

        initializeViews();

    }

    private void initializeViews() {
        titleText = findViewById(R.id.product_detail_title);
        detailPriceText = findViewById(R.id.detailPriceText);
        detailStockText = findViewById(R.id.detailStockText);
        detailProductText = findViewById(R.id.detailProductText);

        titleText.setText(StringUtils.isNullOrEmpty(product.getId()) ? "Create Product" : "Edit Product");
        detailPriceText.setText(String.valueOf(product.getUnitPrice()));
        detailProductText.setText(String.valueOf(product.getName()));
        detailStockText.setText(String.valueOf(product.getStocks()));
    }



}
