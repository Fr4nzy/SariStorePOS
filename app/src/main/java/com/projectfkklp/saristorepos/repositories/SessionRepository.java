package com.projectfkklp.saristorepos.repositories;

import android.content.Context;

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
}
