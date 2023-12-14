package com.example.saristorepos;

import android.os.Parcel;
import android.os.Parcelable;

public class DataClass implements Parcelable {

    private String dataProduct;
    private double dataPrice;
    private int dataStock; // Changed to int
    private String dataImage;
    private String key;
    private int quantity; // New field for quantity

    // Constructors, getters, setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataProduct() {
        return dataProduct;
    }

    public double getDataPrice() {
        return dataPrice;
    }

    public int getDataStock() { // Changed to int
        return dataStock;
    }

    public String getDataImage() {
        return dataImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public DataClass(String dataProduct, double dataPrice, int dataStock, String dataImage) { // Changed to int
        this.dataProduct = dataProduct;
        this.dataPrice = dataPrice;
        this.dataStock = dataStock;
        this.dataImage = dataImage;
        this.quantity = 1; // Default quantity is 1
    }

    public DataClass(){

    }

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dataProduct);
        dest.writeDouble(dataPrice);
        dest.writeInt(dataStock); // Changed to writeInt for int
        dest.writeString(dataImage);
        dest.writeString(key);
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
        dataProduct = in.readString();
        dataPrice = in.readDouble();
        dataStock = in.readInt(); // Changed to readInt for int
        dataImage = in.readString();
        key = in.readString();
    }

    // Helper method to calculate the total price
    public double getTotalPrice() {
        return dataPrice * quantity;
    }
}
