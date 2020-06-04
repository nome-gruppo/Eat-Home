package nomeGruppo.eathome.db;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class StorageConnection {
    private final StorageReference mStorageRef;

    public StorageConnection() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://eathome-bc890.appspot.com");
        this.mStorageRef = firebaseStorage.getReference();
    }

//    public void uploadImage(String imagePath, String idPlace) {
//        Uri file = Uri.fromFile(new File(imagePath));
//        StorageReference riversRef = this.mStorageRef.child("images/" + idPlace + ".jpg");
//        UploadTask uploadTask = riversRef.putFile(file);
//
//// Register observers to listen for when the download is done or if it fails
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                // ...
//            }
//        });
//
//    }

    public void uploadImageBitmap(ByteArrayOutputStream stream, String idPlace){
        byte[] image = stream.toByteArray();

        UploadTask uploadTask = this.storageReference(idPlace).putBytes(image);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }

    public StorageReference storageReference(String idPlace){
        return this.mStorageRef.child("images/" + idPlace + ".jpg");
    }

}
