package com.projectfkklp.saristorepos.activities.inventory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.managers.ProductManager;
import com.projectfkklp.saristorepos.managers.StorageManager;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.utils.ModelUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.projectfkklp.saristorepos.utils.ToastUtils;
import com.projectfkklp.saristorepos.validators.ProductValidator;

import java.util.HashMap;
import java.util.Objects;

public class InventoryProductDetailPage extends AppCompatActivity {
    private Uri uri;
    private String tempBarcode;
    private Product product;

    TextView titleText;
    ImageView productImgView;
    EditText productNameText;
    EditText productUnitPriceText;
    EditText productStockText;
    Button detailBarcodeButton;


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
        productImgView = findViewById(R.id.product_detail_image_view);
        detailBarcodeButton = findViewById(R.id.product_detail_barcode_button);
        productUnitPriceText = findViewById(R.id.product_detail_price_text);
        productStockText = findViewById(R.id.product_detail_stock_text);
        productNameText = findViewById(R.id.product_detail_product_text);

        titleText.setText(StringUtils.isNullOrEmpty(product.getId()) ? "Create Product" : "Edit Product");
        productNameText.setText(Objects.toString(product.getName(), ""));
        productUnitPriceText.setText(Objects.toString(product.getUnitPrice() == 0 ? "" : product.getUnitPrice()));
        productStockText.setText(String.valueOf(product.getStocks() == 0 ? "" : product.getStocks()));

        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    uri = Objects.requireNonNull(data).getData();
                    productImgView.setImageURI(uri);
                } else {
                    Toast.makeText(InventoryProductDetailPage.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }
            }
        );

        productImgView.setOnClickListener(v -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            imagePickerLauncher.launch(photoPicker);
        });

        detailBarcodeButton.setOnClickListener(v -> {
            // Launch ZXing barcode scanner
            new IntentIntegrator(InventoryProductDetailPage.this).initiateScan();
        });
    }

    // Add the following method to handle the result of the barcode scanner
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                tempBarcode = result.getContents();
                // Update the button text to show the scanned barcode temporarily
                detailBarcodeButton.setText(tempBarcode);
                product.setBarcode(tempBarcode);
                Toast.makeText(this, "Scanned Barcode: " + tempBarcode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveProduct(View view) {
        // Initialize Data
        String unitPriceStr = productUnitPriceText.getText().toString();
        String stocksStr = productStockText.getText().toString();
        product.setName(productNameText.getText().toString());
        product.setUnitPrice(Float.parseFloat(
            StringUtils.isNullOrEmpty(unitPriceStr)
                ? "0"
                : unitPriceStr
        ));
        product.setStocks(Integer.parseInt(
            StringUtils.isNullOrEmpty(stocksStr)
                ? "0"
                : stocksStr
        ));
        product.setBarcode(tempBarcode);

        // Validation
        ValidationStatus validationStatus = ProductValidator.validate(product);
        if (!validationStatus.isValid()) {
            HashMap<String, String> errors = validationStatus.getErrors();

            if (errors.containsKey("name")){
                productNameText.setError(errors.get("name"));
            }

            return;
        }

        // Start Saving
        ProgressUtils.showDialog(this, "Saving...");
        StorageManager
            // Upload File
            .uploadFile(uri, ModelUtils.createUUID())
            // get Uploaded Image URL
            .continueWithTask(taskSnapshot -> {
                if (!taskSnapshot.isCanceled()){
                    UploadTask uploadSnapshot = (UploadTask) taskSnapshot;
                    StorageReference fileRef = uploadSnapshot.getResult().getStorage();
                    return fileRef.getDownloadUrl();
                }

                return Tasks.forCanceled();
            })
            // Set product imgUrl, and save product
            .continueWithTask(downloadUrl-> {
                if (downloadUrl.isSuccessful()){
                    String imgUrl = downloadUrl.getResult().toString();
                    product.setImgUrl(imgUrl);
                }

                return ProductManager.save(this, product);
            })
            .addOnSuccessListener(task-> ToastUtils.show(this, "Product saved successfully"))
            .addOnFailureListener(e->ToastUtils.show(this, e.getMessage()))
            .addOnCompleteListener(task -> ProgressUtils.dismissDialog())
        ;
    }

    public void navigateBack(View view){finish();}
}
