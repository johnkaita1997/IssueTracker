package kaita.stream_app_final.Adapteres;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class uploadImage {

     static Context context;
     static Uri mImageUri;
     static DatabaseReference mDatabaseRef;
     static StorageReference storageReference;

    public uploadImage(Context context, Uri mImageUri, DatabaseReference mDatabaseRef, StorageReference storageReference) {
        this.context = context;
        this.mImageUri = mImageUri;
        this.mDatabaseRef = mDatabaseRef;

        if (mImageUri != null) {
            storageReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Upload upload = new Upload(FirebaseAuth.getInstance().getCurrentUser().getUid(),downloadUri.toString());
                        mDatabaseRef.push().setValue(upload);
                    } else {
                        Toast.makeText(context, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
