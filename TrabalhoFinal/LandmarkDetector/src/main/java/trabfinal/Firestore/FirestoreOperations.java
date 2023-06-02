package trabfinal.Firestore;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

public class FirestoreOperations {
    static String collection = "CN2223TF";
    static Firestore db = null;

    public FirestoreOperations() throws Exception {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        FirestoreOptions options = FirestoreOptions
                .newBuilder().setCredentials(credentials).build();
        db = options.getService();
    }

    public void populateFirestore(FirestoreObject dados) throws Exception {
        CollectionReference colRef = db.collection(collection);
        DocumentReference docRef = colRef.document(dados.requestId);
        ApiFuture<WriteResult> resultFut = docRef.set(dados);
        WriteResult result = resultFut.get();
        System.out.println("Armazenada informação no firestore");
    }
}
