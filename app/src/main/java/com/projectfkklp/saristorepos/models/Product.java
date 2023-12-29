package com.projectfkklp.saristorepos.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String product;
    private double price;
    private int stock; // Changed to int
    private String imageURL;
    private String key;
    private int quantity; // New field for quantity
    private String barcode; // Added barcode field
    private boolean selected;

    // Constructors, getters, setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProduct() {
        return product;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Product(String key, double price, String product, String imgUrl) {
        this.key = key;
        this.price=price;
        this.product=product;
        this.imageURL = imgUrl;
    }

    public Product(String key, double price, String product, String imgUrl, int stock) {
        this.key = key;
        this.price=price;
        this.product=product;
        this.imageURL = imgUrl;
        this.stock=stock;
    }

    public Product(String key, double price, String product, int quantity) {
        this.key = key;
        this.price=price;
        this.product=product;
        this.quantity=quantity;
    }

    public Product(String product, double price, int stock, String imageURL) { // Changed to int
        this.product = product;
        this.price = price;
        this.stock = stock;
        this.imageURL = imageURL;
        this.quantity = 1; // Default quantity is 1
    }

    public Product() {

    }
    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product);
        dest.writeDouble(price);
        dest.writeInt(stock); // Changed to writeInt for int
        dest.writeString(imageURL);
        dest.writeString(key);
        dest.writeString(barcode);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private Product(Parcel in) {
        product = in.readString();
        price = in.readDouble();
        stock = in.readInt(); // Changed to readInt for int
        imageURL = in.readString();
        key = in.readString();
        barcode = in.readString();
    }

    // Helper method to calculate the total price
    public double getTotalPrice() {
        return price * quantity;
    }
}