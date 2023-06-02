package trabfinal.PubSub;

import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.pubsub.v1.PubsubMessage;
import org.checkerframework.checker.units.qual.A;
import trabfinal.Firestore.*;
import trabfinal.LandmarkDetector;
import trabfinal.StorageOperations;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class MessageReceiveHandler implements MessageReceiver {
    static String BUCKETNAME = "cn2223-g06-tf";
    public void receiveMessage(PubsubMessage msg, AckReplyConsumer ackReply){
        Map<String, String> map = msg.getAttributesMap();
        String requestId = map.get("requestId");
        String blobName = map.get("blobName");
        String bucketName = map.get("bucketName");
        try {
            //Send photo to VisionAPI & MapsAPI
            DetectLandmarksGcs landmarksGcs = null;
            try {
                landmarksGcs =LandmarkDetector.detectLandmarksGcs(requestId, blobName, bucketName);
            } catch (Exception e){
                System.out.println("photo with no landmarks!");
            }


            //upload photo to google Cloud
            StorageOptions storageOptions = StorageOptions.getDefaultInstance();
            Storage storage = storageOptions.getService();
            StorageOperations storageOperations = new StorageOperations(storage);
            InputStream image = landmarksGcs.photo;
            String[] blobNameSplited = blobName.split("/");
            String imageName = blobNameSplited[0] + "/staticMap_"+blobNameSplited[1];
            try{
                storageOperations.uploadBlobToBucket(bucketName, image.readAllBytes(), imageName, "image/png");
                image.close();
            }catch (Exception e){
                System.out.println("Upload photo canceled because dont have landmarks");
            }

            //Add information to Firestore
            FirestoreOperations firestoreOperations = new FirestoreOperations();
            FirestoreObject firestoreData = new FirestoreObject();
            firestoreData.bucket = landmarksGcs.bucket;
            firestoreData.blobImage = landmarksGcs.blobImage;
            firestoreData.blobMap = imageName;
            firestoreData.requestId = landmarksGcs.requestId;
            for(LandMark landmark: landmarksGcs.landmarks){
                firestoreData.landmarks.add(landmark);
            }

            firestoreOperations.populateFirestore(firestoreData);

        } catch (Exception e) {
            e.printStackTrace();
            ackReply.nack();
            throw new RuntimeException(e);
        }
        ackReply.ack(); // acknowledge positivo
    }
}
