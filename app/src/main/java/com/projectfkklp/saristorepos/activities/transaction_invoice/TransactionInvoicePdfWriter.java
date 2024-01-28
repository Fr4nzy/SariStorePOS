package com.projectfkklp.saristorepos.activities.transaction_invoice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.PdfWriter;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionInvoicePdfWriter extends PdfWriter {
    private Context context;
    private String invoiceTitle;
    private String invoiceDate;
    private Store store;

    private final List<String> transactedProductIds;
    private List<Product> products;
    private List<TransactionItem> transactionItems;

    public TransactionInvoicePdfWriter(
        Context context,
        String filename,
        String invoiceTitle,
        String invoiceDate ,
        List<String> transactedProductIds,
        List<Transaction> transactions
    ){
        super(filename);
        this.context = context;
        store = SessionRepository.getCurrentStore(context);
        this.invoiceTitle = invoiceTitle;
        this.invoiceDate = invoiceDate;

        this.transactedProductIds = transactedProductIds;

        loadTransactionItems(transactions);
    }

    @Override
    public void start() {
        loadProducts();
    }

    private void loadTransactionItems(List<Transaction> transactions){
        transactionItems = new ArrayList<>();

        for (Transaction transaction:transactions){
            transactionItems.addAll(transaction.getItems());
        }
    }

    private void loadProducts(){
        ProgressUtils.showDialog(context, "Generating PDF...");
        StoreRepository
            .getStoreById(SessionRepository.getCurrentStore(context).getId())
            .addOnSuccessListener(successTask->{
                Store store = successTask.toObject(Store.class);
                assert store != null;
                products = store.getProducts();

                // Start writing and save to downloads
                write();
                try {
                    save();
                    ToastUtils.show(context, "Receipt downloaded");
                } catch (IOException e) {
                    ToastUtils.show(context, e.getMessage());
                }
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(context, failedTask.getMessage()))
            .addOnCompleteListener(task-> ProgressUtils.dismissDialog())
        ;
    }

    @Override
    protected void write() {
        setTextSize(35);
        setIsBold(true);

        writeText(invoiceTitle);
        writeText(store.getName());
        writeText(store.getAddress());
        writeText(invoiceDate);

        writeLine(30);
        writeLine(35);

        // Write Table
        writeCustom(writer->{
            Canvas canvas = writer.getCanvas();
            Paint paint = getPaint();
            int pageWidth = writer.getPageWidth();

            // Write Table Header
            int x = writer.getX();
            int y = writer.getY();
            canvas.drawText("QTY", x, y, paint);
            canvas.drawText("Item (Price)", x+100, y, paint);
            canvas.drawText("Amount", pageWidth-250, y, paint);
            writer.setY((int) (y+paint.getTextSize())+ (writer.isBold()? 5:0));
            writeLine(30);

            // Write Table Content
            setIsBold(false);
            int totalQuantity=0;
            float totalAmount=0;
            for (String transactedProductId: transactedProductIds){
                Product product = products.stream()
                    .filter(p->p.getId().equals(transactedProductId))
                    .findFirst()
                    .get();
                int quantity = 0;
                float amount = 0;
                for (TransactionItem transactionItem:transactionItems){
                    if (transactionItem.getProductId().equals(transactedProductId)){
                        quantity += transactionItem.getQuantity();
                        amount += transactionItem.calculateAmount();

                        totalQuantity+=quantity;
                        totalAmount+=amount;
                    }
                }

                x = writer.getX();
                y = writer.getY();
                canvas.drawText(Objects.toString(quantity), x, y, paint);
                canvas.drawText(product.getName(), x+100, y, paint);
                canvas.drawText(StringUtils.formatToPeso(amount), pageWidth-250, y, paint);
                writer.setY((int) (y+paint.getTextSize()));
            }

            // Write Summary
            writeLine(20);
            x = writer.getX();
            y = writer.getY();
            canvas.drawText(Objects.toString(totalQuantity), x, y, paint);
            canvas.drawText(StringUtils.formatToPeso(totalAmount), pageWidth-250, y, paint);
        });
    }
}
