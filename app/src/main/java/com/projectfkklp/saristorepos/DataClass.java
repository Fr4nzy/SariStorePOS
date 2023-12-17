package com.projectfkklp.saristorepos;

import android.os.Parcel;
import android.os.Parcelable;

public class DataClass implements Parcelable {

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

    public DataClass(String product, double price, int stock, String imageURL) { // Changed to int
        this.product = product;
        this.price = price;
        this.stock = stock;
        this.imageURL = imageURL;
        this.quantity = 1; // Default quantity is 1
    }

    public DataClass() {

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

    public static final Parcelable.Creator<DataClass> CREATOR = new Parcelable.Creator<DataClass>() {
        public DataClass createFromParcel(Parcel in) {
            return new DataClass(in);
        }

        public DataClass[] newArray(int size) {
            return new DataClass[size];
        }
    };

    private DataClass(Parcel in) {
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