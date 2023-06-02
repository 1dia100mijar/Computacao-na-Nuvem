package trabfinal;

import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class StorageOperations {

    Storage storage = null;

    public StorageOperations(Storage storage) {
        this.storage = storage;
    }

    public void createBucket(String bucketName) throws Exception {
        Bucket bucket = storage.create(
                BucketInfo.newBuilder(bucketName)
                        .setStorageClass(StorageClass.STANDARD)
                        .setLocation("eu")
                        .build());
    }

    public String[] listBuckets(){
        ArrayList<String> buckets = new ArrayList<String>();

        for (Bucket bucket : storage.list().iterateAll()) {
            buckets.add(bucket.getName());
        }
        return buckets.toArray(new String[0]);
    }

    public String[] uploadBlobToBucket(String bucketName, byte[] blobData, String photoName, String contentType) throws Exception {
        BlobId blobId = BlobId.of(bucketName, photoName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        try (WriteChannel writer = storage.writer(blobInfo)) {
            byte[] buffer = new byte[1024];
            try (InputStream input =  new ByteArrayInputStream(blobData)) {
                int limit;
                while ((limit = input.read(buffer)) >= 0) {
                    try {
                        writer.write(ByteBuffer.wrap(buffer, 0, limit));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return new String[]{bucketName, photoName};
    }
}