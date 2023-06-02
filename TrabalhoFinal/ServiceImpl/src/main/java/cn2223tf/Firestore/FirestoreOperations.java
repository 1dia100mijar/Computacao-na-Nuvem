package cn2223tf.Firestore;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

import java.util.*;
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

    public void stop() throws Exception {
        db.close();
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

    public ArrayList<FirestoreFilteredDocument> getPhotoNameWithAccuracyBiggerThan(double accuracy) throws ExecutionException, InterruptedException {
        Query queryDocs = db.collection(collection);
        ArrayList<FirestoreFilteredDocument> docsFiltered = new ArrayList<>();

        //Get all firestore documents and convert them to FirestoreObject class, that was implemented
        //Couldn't get the documents with a firestore query because the landmarks where stored inside an Array
        for (FirestoreObject firestoreObject : queryDocs.get().get().toObjects(FirestoreObject.class)) {
            for (LandmarkFirestore landmark : firestoreObject.landmarks) {
                //Filtering the results
                if (landmark.score >= accuracy) {
                    FirestoreFilteredDocument doc = new FirestoreFilteredDocument();
                    doc.blobImage = firestoreObject.blobImage;
                    doc.name = landmark.name;
                    docsFiltered.add(doc);
                }
            }
        }
        return docsFiltered;
    }
}
