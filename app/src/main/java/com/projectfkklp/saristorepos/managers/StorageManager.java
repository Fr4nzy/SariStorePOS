package com.projectfkklp.saristorepos.managers;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class StorageManager {
    public static StorageReference getStorageReference() {
        return  FirebaseStorage.getInstance().getReference().child("uploads");
    }

    public static Task<?> uploadFile(Uri uri, String fileId){
        if (uri==null){
            return Tasks.forCanceled();
        }

        return getStorageReference()
            .child(fileId)
            .putFile(uri);
    }

}
