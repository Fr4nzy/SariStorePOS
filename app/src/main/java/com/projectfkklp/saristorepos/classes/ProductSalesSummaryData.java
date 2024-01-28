package com.projectfkklp.saristorepos.classes;

public class ProductSalesSummaryData {
    public String productId;
    public int soldItems;
    public float sales;

    public ProductSalesSummaryData(String productId, int soldItems, float sales){
        this.productId = productId;
        this.soldItems = soldItems;
        this.sales = sales;
    }
}
