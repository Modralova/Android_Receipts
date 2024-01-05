package com.example.paragony;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;


public class ReceiptSchema implements Parcelable {

    public String date;
    public String receipt_id;
    public String shop;
    public String address;
    public String nip;
    public List<ItemSchema> itemsList;
    public String currency;
    public Double total;
    public Double total_sum = 0.0;
    public Double total_tax;
    public Double tax_sum = 0.0;
    public Double taxA = 0.0;
    public Double taxB = 0.0;
    public Double taxC = 0.0;
    public Double taxD = 0.0;
    public Double taxE = 0.0;
    public Double taxF = 0.0;
    public Double taxG = 0.0;

    protected ReceiptSchema(Parcel in) {
        date = in.readString();
        receipt_id = in.readString();
        shop = in.readString();
        address = in.readString();
        nip = in.readString();
        currency = in.readString();
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readDouble();
        }
        if (in.readByte() == 0) {
            total_sum = null;
        } else {
            total_sum = in.readDouble();
        }
        if (in.readByte() == 0) {
            total_tax = null;
        } else {
            total_tax = in.readDouble();
        }
        if (in.readByte() == 0) {
            tax_sum = null;
        } else {
            tax_sum = in.readDouble();
        }
        if (in.readByte() == 0) {
            taxA = null;
        } else {
            taxA = in.readDouble();
        }
        if (in.readByte() == 0) {
            taxB = null;
        } else {
            taxB = in.readDouble();
        }
        if (in.readByte() == 0) {
            taxC = null;
        } else {
            taxC = in.readDouble();
        }
        if (in.readByte() == 0) {
            taxD = null;
        } else {
            taxD = in.readDouble();
        }
        if (in.readByte() == 0) {
            taxE = null;
        } else {
            taxE = in.readDouble();
        }
        if (in.readByte() == 0) {
            taxF = null;
        } else {
            taxF = in.readDouble();
        }
        if (in.readByte() == 0) {
            taxG = null;
        } else {
            taxG = in.readDouble();
        }
    }

    public static final Creator<ReceiptSchema> CREATOR = new Creator<ReceiptSchema>() {
        @Override
        public ReceiptSchema createFromParcel(Parcel in) {
            return new ReceiptSchema(in);
        }

        @Override
        public ReceiptSchema[] newArray(int size) {
            return new ReceiptSchema[size];
        }
    };

    public ReceiptSchema() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(receipt_id);
        dest.writeString(shop);
        dest.writeString(address);
        dest.writeString(nip);
        dest.writeString(currency);
        if (total == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(total);
        }
        if (total_sum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(total_sum);
        }
        if (total_tax == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(total_tax);
        }
        if (tax_sum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(tax_sum);
        }
        if (taxA == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(taxA);
        }
        if (taxB == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(taxB);
        }
        if (taxC == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(taxC);
        }
        if (taxD == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(taxD);
        }
        if (taxE == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(taxE);
        }
        if (taxF == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(taxF);
        }
        if (taxG == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(taxG);
        }
    }
}
