package com.projectfkklp.saristorepos.interfaces;

import com.google.android.gms.tasks.Task;
import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.models.Store;

public interface OnStoreSave {
    public void onStoreSave(Store store, ValidationStatus validationStatus, Task<Void> task);
}

