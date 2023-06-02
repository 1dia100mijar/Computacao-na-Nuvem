package cn2223tf;

import cn2223tf.Firestore.FirestoreFilteredDocument;
import cn2223tf.Firestore.FirestoreOperations;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Server extends CN2223TFGrpc.CN2223TFImplBase {
    //Storage
    static String BUCKETNAME = "cn2223-g06-tf";

    private static int svcPort = 8000;
    private static FirestoreOperations firestoreOperations = null;
    public static void main(String[] args) throws IOException {

        try{
            io.grpc.Server svc = ServerBuilder.forPort(svcPort).addService(new Server()).build();
            svc.start();
            firestoreOperations = new FirestoreOperations();
            System.out.println("Server started, listening on " + svcPort);
            System.out.println("Hello, gRPC world!");
            firestoreOperations.getPhotoNameWithAccuracyBiggerThan(0.6);
            Scanner scan = new Scanner(System.in);
            scan.nextLine();
            svc.shutdown();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public StreamObserver<Block> uploadPhoto(StreamObserver<BlobIdentifier> responseObserver) {
        System.out.println("Upload photo called");
        return new ServerStreamObserverBlobIdentifier(responseObserver);
    }

    @Override
    public void getLandmarks(BlobIdentifier request, StreamObserver<LandMarksResult> responseObserver){
        System.out.println("Get results of an imageID called");
        //call firestore
        Map<String, Object> map = null;
        try {
            map = firestoreOperations.getResultsFromPhotoId(request.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ArrayList<Map<String, String>> landmarks = (ArrayList<Map<String, String>>) map.get("landmarks");
        LandMarksResult.Builder landmarksResult = LandMarksResult.newBuilder();

        for (Map<String, String> landmark:landmarks){
            LandmarkElement landmarkElement = LandmarkElement.newBuilder()
                    .setName(landmark.get("name"))
                    .setLatitude(Float.parseFloat(String.valueOf(landmark.get("latitude"))))
                    .setLongitude(Float.parseFloat(String.valueOf(landmark.get("longitude"))))
                    .setAccuracy(Float.parseFloat(String.valueOf(landmark.get("score"))))
                    .build();
            landmarksResult.addLandmark(landmarkElement);
        }

        LandMarksResult result = landmarksResult.build();
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    @Override
    public void getMapImage(BlobIdentifier request, StreamObserver<MapResult> responseObserver){
        System.out.println("Get map from a request ID");

        try {
            //call firestore
            String blobName = firestoreOperations.getStaticMap(request.getId());
            //call cloud storage
            StorageOptions storageOptions = StorageOptions.getDefaultInstance();
            Storage storage = storageOptions.getService();
            StorageOperations storageOperations = new StorageOperations(storage);
            byte[] mapData = storageOperations.downloadBlobFromBucket(BUCKETNAME, blobName);

            byte[] buffer = new byte[1024];
            try (InputStream input = new ByteArrayInputStream(mapData)) {
                while (input.read(buffer) >= 0) {
                    try {
                        responseObserver.onNext(
                                MapResult.newBuilder()
                                        .setMap(ByteString.copyFrom(buffer))
                                        .build()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                responseObserver.onCompleted();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getAccurateLandmarks(Accuracy request, StreamObserver<AccuracyResult> responseObserver){
        System.out.println("Getting Photo names with accuracy bigger than " + request.getAccuracy());
        ArrayList<FirestoreFilteredDocument> docs = null;
        try {
            docs = firestoreOperations.getPhotoNameWithAccuracyBiggerThan(request.getAccuracy());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AccuracyResult.Builder builder = AccuracyResult.newBuilder();
        for(FirestoreFilteredDocument filteredDocument:docs){
            builder.addLandmark(LandMarkAccuracyResult.newBuilder().setName(filteredDocument.name)
                    .setImageName(filteredDocument.blobImage)
                    .build());
        }
        AccuracyResult accuracyResult = builder.build();
        responseObserver.onNext(accuracyResult);
        responseObserver.onCompleted();
    }
}
