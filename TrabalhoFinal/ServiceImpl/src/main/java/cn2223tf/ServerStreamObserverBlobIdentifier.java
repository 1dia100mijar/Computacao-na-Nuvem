package cn2223tf;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import cn2223tf.StorageOperations;

public class ServerStreamObserverBlobIdentifier  implements StreamObserver<Block> {
    static String BUCKETNAME = "cn2223-g06-tf";
    StreamObserver<BlobIdentifier> sBlobIdentifier;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    String photoName;
    String photoDataType;
    byte[] photo ;

    public ServerStreamObserverBlobIdentifier(StreamObserver<BlobIdentifier> sBlobId){
        this.sBlobIdentifier=sBlobId;
    }

    @Override
    public void onNext(Block block) {
        photo = block.getData().toByteArray();
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
                String[] blobInformation = storageOperations.uploadBlobToBucket(BUCKETNAME, photo, photoName, photoDataType);
                BlobId blobId = BlobId.of(blobInformation[0], blobInformation[1]);
                sBlobIdentifier.onNext(BlobIdentifier.newBuilder()
                        .setId(blobId.toString()).build());
                sBlobIdentifier.onCompleted();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
