package com.projectfkklp.saristorepos.interfaces;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.projectfkklp.saristorepos.classes.ValidationStatus;

public interface OnSetFirebaseDocument {
    void onInvalid(ValidationStatus validationStatus);
    void onSuccess(Void task);
    void onFailed(Exception exception);
    void onComplete(Task<Void> task);
}
