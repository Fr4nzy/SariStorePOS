package com.projectfkklp.saristorepos.interfaces;

import com.projectfkklp.saristorepos.models.Transaction;

import java.text.ParseException;
import java.util.List;

public interface OnTransactionRetrieve {
    void onTransactionRetrieved(Transaction transaction) throws ParseException;
}
