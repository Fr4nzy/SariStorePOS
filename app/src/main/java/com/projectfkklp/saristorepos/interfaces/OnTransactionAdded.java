package com.projectfkklp.saristorepos.interfaces;

import com.projectfkklp.saristorepos.models.Transaction;

public interface OnTransactionAdded {
    void onTransactionAdded(Transaction transaction);
}
