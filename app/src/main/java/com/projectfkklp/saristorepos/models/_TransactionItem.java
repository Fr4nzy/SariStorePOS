package com.projectfkklp.saristorepos.models;

public class _TransactionItem {
    // region Fields

    private _Product product;
    private String productId;
    private int quantity;

    // endregion

    // region Getters

    public _Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    // endregion

    // region Setters

    public void setProduct(_Product product) {
        this.product = product;
        this.productId = product.getId();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // endregion
}
