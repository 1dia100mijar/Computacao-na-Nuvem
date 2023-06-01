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

    StorageClass getStorageClass() {
        StorageClass[] CLASSES = new StorageClass[]{
                StorageClass.STANDARD,
                StorageClass.NEARLINE,
                StorageClass.COLDLINE,
                StorageClass.ARCHIVE
        };
        return CLASSES[0];
    }

    String getLocation() {
        Scanner scan = new Scanner(System.in);
        String[] LOCALS = new String[]{
                "northamerica-northeast1", "us-central1", "us-east1", "us-east4", "us-west1", "us-west2",
                "southamerica-east1",
                "europe-north1", "europe-west1", "europe-west2", "europe-west3", "europe-west4", "europe-west6",
                "asia-east1", "asia-east2", "asia-northeast1", "asia-south1", "asia-southeast1",
                "australia-southeast1", "asia", "eu", "us", "EUR4","NAM4"
        };
//            System.out.println("Region names:");
//            System.out.println("North America:");
//            System.out.println("1:northamerica-northeast1	 (Montréal)");
//            System.out.println("2:us-central1	(Iowa)");
//            System.out.println("3:us-east1	(South Carolina)");
//            System.out.println("4: us-east4	(Northern Virginia)");
//            System.out.println("5:us-west1	(Oregon)");
//            System.out.println("6:us-west2	(Los Angeles)");
//            System.out.println("South America:");
//            System.out.println("7:southamerica-east1	(São Paulo)");
//            System.out.println("Europe");
//            System.out.println("8:europe-north1	(Finland)");
//            System.out.println("9:europe-west1	(Belgium)");
//            System.out.println("10:europe-west2	(London)");
//            System.out.println("11:europe-west3	(Frankfurt)");
//            System.out.println("12:europe-west4	(Netherlands)");
//            System.out.println("13:europe-west6	(Zürich)");
//            System.out.println("Asia:");
//            System.out.println("14:asia-east1	(Taiwan)");
//            System.out.println("15:asia-east2	(Hong Kong)");
//            System.out.println("16:asia-northeast1	(Tokyo)");
//            System.out.println("17:asia-south1	(Mumbai)");
//            System.out.println("18:asia-southeast1	(Singapore)");
//            System.out.println("Australia:");
//            System.out.println("19:australia-southeast1	(Sydney)");
//            System.out.println("Multi-regional locations:");
//            System.out.println("20:asia	(Data centers in Asia)");
//            System.out.println("21:eu	 (Data centers in the European Union1)");
//            System.out.println("22:us	 (Data centers in the United States)");
//            System.out.println("Dual-region locations:");
//            System.out.println("23: EUR4 	EUROPE-NORTH1 and EUROPE-WEST4");
//            System.out.println("24: NAM4 	US-CENTRAL1 and US-EAST1");
//            System.out.print("Choose an option: ");
        return LOCALS[21 - 1];
    }

    public void createBucket(String bucketName) throws Exception {
        Bucket bucket = storage.create(
                BucketInfo.newBuilder(bucketName)
                        // See here for possible values: http://g.co/cloud/storage/docs/storage-classes
                        .setStorageClass(getStorageClass())
                        // Possible values: http://g.co/cloud/storage/docs/bucket-locations#location-mr
                        .setLocation("eu")
                        .build());
    }

    public void deleteBucket() throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the name of the Bucket to delete? ");
        String bucketName = scan.nextLine();
        Bucket bucket = storage.get(bucketName);
        bucket.delete();
    }

    public String[] listBuckets(){

        ArrayList<String> buckets = new ArrayList<String>();

        for (Bucket bucket : storage.list().iterateAll()) {
            buckets.add(bucket.getName());
        }
        for(String b:buckets.toArray(new String[0])){
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

    public void downloadBlobFromBucket() throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("The name of Bucket? ");
        String bucketName = scan.nextLine();
        System.out.println("The name of Blob? ");
        String blobName = scan.nextLine();
        System.out.println("What is the file pathname for downloading the Blob? ");
        String absFileName = scan.nextLine();
        Path downloadTo = Paths.get(absFileName);
        //System.out.println("download to: "+downloadTo);
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            System.out.println("No such Blob exists !");
            return;
        }
        PrintStream writeTo = new PrintStream(Files.newOutputStream(downloadTo));
        if (blob.getSize() < 1_000_000) {
            // Blob is small read all its content in one request
            byte[] content = blob.getContent();
            writeTo.write(content);
        } else {
            // When Blob size is big or unknown use the blob's channel reader.
            try (ReadChannel reader = blob.reader()) {
                WritableByteChannel channel = Channels.newChannel(writeTo);
                ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
                while (reader.read(bytes) > 0) {
                    bytes.flip();
                    channel.write(bytes);
                    bytes.clear();
                }
            }
        }
        writeTo.close();
        System.out.println("Blob " + blobName + " downloaded to " + downloadTo);
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