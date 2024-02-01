package com.projectfkklp.saristorepos.classes;

public class ProductSalesSummaryData {
    public String productId;
    public String productName;
    public int soldItems;
    public float sales;

    public ProductSalesSummaryData(String productId, String productName, int soldItems, float sales){
        this.productId = productId;
        this.productName = productName;
        this.soldItems = soldItems;
        this.sales = sales;
    }
}
