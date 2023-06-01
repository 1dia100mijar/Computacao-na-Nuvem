package trabfinal.Firestore;

import com.google.cloud.firestore.GeoPoint;

import java.io.InputStream;
import java.util.ArrayList;

public class DetectLandmarksGcs {
    public InputStream photo;
    public String requestId;
    public String blobImage;
    public String bucket;
    public ArrayList<LandMark> landmarks;
    public DetectLandmarksGcs(){
        this.landmarks = new ArrayList<LandMark>();
    }
}
