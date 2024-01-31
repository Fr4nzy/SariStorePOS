package com.projectfkklp.saristorepos.repositories;

import android.content.Context;

import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.utils.CacheUtils;

public class SessionRepository {
    public static User getCurrentUser(Context context) {
        return CacheUtils.getObject(context, "current_user", User.class);
    }

    public static Store getCurrentStore(Context context) {
        return CacheUtils.getObject(context, "current_store", Store.class);
    }

    public static UserRole getCurrentUserRole(Context context){
        return CacheUtils.getObject(context, "current_user_role", UserRole.class);
    }
}
