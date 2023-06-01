package cn2223tf.Firestore;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreOperations {
    static String collection = "CN2223TF";
    static Firestore db = null;

    public FirestoreOperations() throws Exception {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        FirestoreOptions options = FirestoreOptions
                .newBuilder().setCredentials(credentials).build();
        db = options.getService();
    }

    public Map<String, Object> getResultsFromPhotoId(String photoId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(collection).document(photoId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return document.getData();
    }

    public static String getStaticMap(String requestId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(collection).document(requestId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return String.valueOf(document.getData().get("blobMap"));
    }

}
