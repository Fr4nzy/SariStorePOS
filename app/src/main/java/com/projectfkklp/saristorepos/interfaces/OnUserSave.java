package com.projectfkklp.saristorepos.interfaces;

import com.google.android.gms.tasks.Task;
import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.models.User;

public interface OnUserSave {
    public void onUserSave(User user, ValidationStatus validationStatus, Task<Void> task);
}
