package trabfinal.Firestore;

import com.google.cloud.firestore.GeoPoint;

import java.util.ArrayList;

public class FirestoreObject {
    public String requestId;
    public String blobImage;
    public String blobMap;
    public String bucket;
    public ArrayList<LandMark> landmarks;

    public FirestoreObject(){
        this.landmarks = new ArrayList<LandMark>();
    }
}
