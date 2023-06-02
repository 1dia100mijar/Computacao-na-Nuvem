package cn2223tf;

import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import io.grpc.internal.JsonUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
        //Generate a random requestID
        UUID requestId = UUID.randomUUID();
        //Creating the blobname to upload the photo inside a folder with the request ID
        String blobName = requestId+"/"+photoName;
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();

        System.out.println("Uploading photo to cloud Storage");
        try (WriteChannel writer = storage.writer(blobInfo)) {
            byte[] buffer = new byte[1024];
            try (InputStream input =  new ByteArrayInputStream(blobData)) {
                int limit;
                while ((limit = input.read(buffer)) >= 0) {
                    try {
                        //Sending the photo bytes to cloud Store
                        writer.write(ByteBuffer.wrap(buffer, 0, limit));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println("Photo uploaded!!!");
            }
        }
        return new String[]{bucketName, blobName, requestId.toString()};
    }

    public byte[] downloadBlobFromBucket(String bucketName, String blobName){
        return storage.readAllBytes(bucketName, blobName);
    }

    public void makeBlobPublic(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the name of the Bucket? ");
        String bucketName = scan.nextLine();
        System.out.println("Enter the name of the Blob? ");
        String blobName = scan.nextLine();
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        Acl.Entity aclEnt = Acl.User.ofAllUsers();
        Acl.Role role = Acl.Role.READER;
        Acl acl = Acl.newBuilder(aclEnt, role).build();
        blob.createAcl(acl);

    }
    // TODO: Develop other Operations. Some of them in slides


}