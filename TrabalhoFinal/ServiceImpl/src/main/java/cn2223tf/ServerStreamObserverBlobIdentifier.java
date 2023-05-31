package cn2223tf;

import cn2223tf.PubSub.PubSub;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.units.qual.A;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ServerStreamObserverBlobIdentifier  implements StreamObserver<Block> {
    static String BUCKET_NAME = "cn2223-g06-tf";
    static String PROJECT_ID = "cn2223-t2-g06";
    StreamObserver<BlobIdentifier> sBlobIdentifier;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    String photoName;
    String photoDataType;
    byte[] photo;
    int onNextTimesCalled = 0;

    //Pub/Sub
    static TopicAdminClient topicAdmin;
    static String PubSubTopicName = "cn2223-g06-tf";


    public ServerStreamObserverBlobIdentifier(StreamObserver<BlobIdentifier> sBlobId) {
        this.photo = new byte[0];
        this.sBlobIdentifier=sBlobId;
    }

    @Override
    public void onNext(Block block) {
        byte[] photoBlock = block.getData().toByteArray();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] combinedArray = new byte[photo.length + photoBlock.length];
        System.arraycopy(photo, 0, combinedArray, 0, photo.length);
        System.arraycopy(photoBlock, 0, combinedArray, photo.length, photoBlock.length);
        photo = combinedArray;
        if(onNextTimesCalled++ == 0){
            photoName = block.getBlobName();
            photoDataType = block.getDataType();
        }
        photo = combinedArray;
        photoName = block.getBlobName();
        photoDataType = block.getDataType();

        try {
            outputStream.write( photo );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        try{
            if(photoName != null && outputStream  != null){
                StorageOptions storageOptions = StorageOptions.getDefaultInstance();
                Storage storage = storageOptions.getService();
                StorageOperations storageOperations = new StorageOperations(storage);
                if(!verifyIfBucketExists(storageOperations)){
                    storageOperations.createBucket(BUCKET_NAME);
                }
                String[] blobInformation = storageOperations.uploadBlobToBucket(BUCKET_NAME, photo, photoName, photoDataType);
                System.out.println("Photo uploaded!!!");
//                BlobId blobId = BlobId.of(blobInformation[0], blobInformation[1]);

                PubSub.createPubSubTopic();
                PubSub.publishMessage(blobInformation[2], blobInformation[0], blobInformation[1]);

//                {
//                    "requestId":  "b36ffd68-52a0-43bf-b5a2-ebdb2b635729",
//                    "bucketName": "cn2223-g06-tf",
//                    "blobName": "b36ffd68-52a0-43bf-b5a2-ebdb2b635729paulinho.png"
//                }
                sBlobIdentifier.onNext(BlobIdentifier.newBuilder()
                        .setId(blobInformation[2]).build());
                sBlobIdentifier.onCompleted();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static boolean verifyIfBucketExists(StorageOperations storageOperations) throws Exception {
        String[] buckets = storageOperations.listBuckets();
        boolean haveBucket = false;
        for(String bucket: buckets){
            if (bucket.equals(BUCKET_NAME)) {
                haveBucket = true; break;
            }
        }
        return haveBucket;
    }
}
