package com.projectfkklp.saristorepos.models;

public class _TransactionItem {
    // region Fields

    private Product product;
    private String productId;
    private int quantity;

    // endregion

    // region Getters

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    // endregion

    // region Setters

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product.getId();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // endregion
}
