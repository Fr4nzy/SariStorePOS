package com.projectfkklp.saristorepos.interfaces;

import com.projectfkklp.saristorepos.models._Transaction;

import java.text.ParseException;

public interface OnTransactionRetrieve {
    void onTransactionRetrieved(_Transaction transaction) throws ParseException;
}
