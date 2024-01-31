package com.projectfkklp.saristorepos.activities.analytics;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.classes.ProductSalesSummaryData;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class ProductsSalesTableDataAdapter extends TableDataAdapter<ProductSalesSummaryData> {

    public ProductsSalesTableDataAdapter(Context context, List<ProductSalesSummaryData> data) {
        super(context, data);
    }
    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        ProductSalesSummaryData summary = getRowData(rowIndex);
        TextView renderedView = new TextView(getContext());

        int cellBackgroundColor = getResources().getColor(R.color.cellBackground);
        renderedView.setBackgroundColor(cellBackgroundColor);

        renderedView.setGravity(Gravity.CENTER);

        switch (columnIndex) {
            case 0:
                renderedView.setText(summary.productName);
                break;
            case 1:
                renderedView.setText(String.valueOf(summary.soldItems));
                break;
            case 2:
                renderedView.setText(StringUtils.formatToPeso(summary.sales));
                break;
        }

        return renderedView;
    }
}
