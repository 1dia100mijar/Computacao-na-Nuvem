package cn.operations;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;

import java.awt.desktop.QuitResponse;
import java.io.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class main {
    static String collection = "OcupacaoEspacosPublicos";

    public static void main(String[] args) throws Exception {
        File key = new File("cn2223-t2-g06-b2ccbc8bd4b4.json");
        InputStream serviceAccount = new FileInputStream(key.getPath());
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirestoreOptions options = FirestoreOptions
                .newBuilder().setCredentials(credentials).build();
        Firestore db = options.getService();


        populateFirestore(db, "OcupacaoEspacosPublicos.csv");
        listAllFilesInCollection(db);
        listFileWithId(db, "Lab52027");
        deleteFieldInDocument(db, "Lab52027", "location.point");
        listAllFilesWithField(db, "location.freguesia", "Belém");
        listAllFilesWithMultiplesFields(db, "ID", 2000, "location.freguesia", "Belém", "event.tipo", "Filmagem");
        listAllFilesStartedInFebruary2017(db);
    }

    private static void populateFirestore(Firestore db, String dados) throws Exception {
        File csvFile = new File(dados);
        insertDocuments(csvFile.getPath(), db, collection);
    }
    private static void listAllFilesInCollection(Firestore db) throws ExecutionException, InterruptedException {
        CollectionReference cref = db.collection(collection);
        Iterable<DocumentReference> allDocs = cref.listDocuments();
        for (DocumentReference docref : allDocs) {
            ApiFuture<DocumentSnapshot> docfut = docref.get();
            DocumentSnapshot doc = docfut.get();
            System.out.println("doc:" + doc.getData());
        }
    }

    public static void listFileWithId(Firestore db,String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(collection).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        System.out.println(document.getData());
    }
    private static void deleteFieldInDocument(Firestore db, String id, String field) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(collection).document(id);
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, FieldValue.delete());
        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        listFileWithId(db, id);
    }
    private static void listAllFilesWithField(Firestore db, String field, String value) throws ExecutionException, InterruptedException {
        Query query = db.collection(collection).whereEqualTo(field, value);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for(QueryDocumentSnapshot documentSnapshot:querySnapshot.get()){
            System.out.println(documentSnapshot.getData());
        }
    }

    private static void listAllFilesWithMultiplesFields(Firestore db, String field1, Number value1,
                                                    String field2, String value2,
                                                    String field3, String value3) throws ExecutionException, InterruptedException {
        Query query = db.collection(collection).whereGreaterThan(field1, value1)
                .whereEqualTo(field2, value2)
                .whereEqualTo(field3, value3);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for(QueryDocumentSnapshot documentSnapshots:querySnapshot.get()){
            System.out.println(documentSnapshots.getData());
        }
    }

    private static void listAllFilesStartedInFebruary2017(Firestore db) throws ExecutionException, InterruptedException {
        Query query = db.collection(collection).whereGreaterThan("ID", 2015)
                .whereGreaterThan("ID", 2050);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for(QueryDocumentSnapshot documentSnapshots:querySnapshot.get()) {
            System.out.println(documentSnapshots.getData());
        }
    }

    public static OcupacaoTemporaria convertLineToObject (String line) throws ParseException {
        String[] cols = line.split(",");
        OcupacaoTemporaria ocup = new OcupacaoTemporaria();
        ocup.ID = Integer.parseInt(cols[0]);
        ocup.location = new Localizacao();
        ocup.location.point = new GeoPoint(Double.parseDouble(cols[1]), Double.parseDouble(cols[2]));
        ocup.location.coord = new Coordenadas();
        ocup.location.coord.X = (Double) Double.parseDouble(cols[1]);
        ocup.location.coord.Y = (Double) Double.parseDouble(cols[2]);
        ocup.location.freguesia = cols[3];
        ocup.location.local = cols[4];
        ocup.event = new Evento();
        ocup.event.evtID = Integer.parseInt(cols[5]);
        ocup.event.nome = cols[6];
        ocup.event.tipo = cols[7];
        ocup.event.details = new HashMap<String, String>();
        if (!cols[8].isEmpty()) ocup.event.details.put("Participantes", cols[8]);
        if (!cols[9].isEmpty()) ocup.event.details.put("Custo", cols[9]);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        ocup.event.dtInicio = formatter.parse(cols[10]);
        ocup.event.dtFinal = formatter.parse(cols[11]);
        ocup.event.licenciamento = new Licenciamento();
        ocup.event.licenciamento.code = cols[12];
        ocup.event.licenciamento.dtLicenc = formatter.parse(cols[13]);
        return ocup;
    }
    public static void insertDocuments(String pathnameCSV, Firestore db, String collectionName)
            throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(pathnameCSV));
        CollectionReference colRef = db.collection(collectionName);
        String line;
        while ((line = reader.readLine()) != null) {
            OcupacaoTemporaria ocup = convertLineToObject(line);
            DocumentReference docRef = colRef.document("Lab5" + ocup.ID);
            ApiFuture<WriteResult> resultFut = docRef.set(ocup);
            WriteResult result = resultFut.get();
            System.out.println("Update time : " + result.getUpdateTime());
        }
    }
}
