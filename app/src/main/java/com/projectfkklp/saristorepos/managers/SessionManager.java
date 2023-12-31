package com.projectfkklp.saristorepos.managers;

import android.content.Context;

import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.utils.CacheUtils;

public class SessionManager {
    public static void reset(Context context) {
        CacheUtils.dump(context);
    }

    public static void setUser(Context context, User user){
        CacheUtils.saveObject(context, "current_user", user);
    }
}
