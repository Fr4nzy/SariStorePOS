package com.projectfkklp.saristorepos.interfaces;

import com.projectfkklp.saristorepos.models._Transaction;

import java.text.ParseException;
import java.util.List;

public interface OnTransactionsRetrieve {
    void onTransactionsRetrieved(List<_Transaction> objects) throws ParseException;
}
