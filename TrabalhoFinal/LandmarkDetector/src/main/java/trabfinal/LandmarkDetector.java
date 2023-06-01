package trabfinal;

import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.pubsub.v1.Subscriber;
import trabfinal.Firestore.*;
import trabfinal.PubSub.PubSub;
import com.google.cloud.vision.v1.*;
import com.google.type.LatLng;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import trabfinal.PubSub.PubSub;

public class LandmarkDetector {
    final static int ZOOM = 15; // Streets
    final static String SIZE = "600x300";
    static String apiKey = "AIzaSyD25lWtl-Ox6wA5bD-ZmWKVOAYvlhgUiqM";

    public static void main(String[] args) throws IOException {
        try{
            PubSub.createSubscription();
        }catch(AlreadyExistsException e){
            System.out.println("subscription already exists!");
        }
        catch (Exception e){throw new RuntimeException(e);}
        PubSub.createSubscriber();

//        detectAllLandmarksGcs(args[0]);
//        PubSub.stopSubscriber(subscriber);
    }

//    public static void detectAllLandmarksGcs(String apiKey, Map<String, String> map) throws IOException {
//        for (String name : images) {
//            String blobGsPath = "gs://"+BUCKET_NAME+"/" + name;
//            detectLandmarksGcs(blobGsPath, apiKey);
//        }
//    }

    // Detects landmarks in the specified remote image on Google Cloud Storage.
    public static DetectLandmarksGcs detectLandmarksGcs(String requestId,String blobName,String bucketname) throws IOException {
        String blobGsPath = "gs://"+bucketname+"/" + blobName;
        System.out.println("Detecting landmarks for: " + blobGsPath);
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(blobGsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LANDMARK_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);
        DetectLandmarksGcs returnClass = new DetectLandmarksGcs();
        returnClass.requestId = requestId;
        returnClass.blobImage = blobName;
        returnClass.bucket = bucketname;
        returnClass.landmarks = new ArrayList<LandMark>();
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return null;
                }

                System.out.println("Landmarks list size: " + res.getLandmarkAnnotationsList().size());
                // For full list of available annotations, see http://g.co/cloud/vision/docs
                boolean first = true; // Only get map for first annotation
                for (EntityAnnotation annotation : res.getLandmarkAnnotationsList()) {
                    LocationInfo info = annotation.getLocationsList().listIterator().next();
                    LandMark landmark = new LandMark();
                    landmark.name = annotation.getDescription();
                    landmark.score = annotation.getScore();
                    LatLng latLng = info.getLatLng();
                    landmark.latitude = latLng.getLatitude();
                    landmark.longitude = latLng.getLongitude();
                    returnClass.landmarks.add(landmark);
                    System.out.format("Landmark: %s(%f)%n %s%n",
                            annotation.getDescription(),
                            annotation.getScore(),
                            info.getLatLng());
                    if (first) {
                        returnClass.photo = (getStaticMapSaveImage(info.getLatLng(), apiKey));
                        first = false;
                    }
                }
            }
        }
        return returnClass;
    }

    private static InputStream getStaticMapSaveImage(LatLng latLng, String apiKey) {
        String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?"
                + "center=" + latLng.getLatitude() + "," + latLng.getLongitude()
                + "&zoom=" + ZOOM
                + "&size=" + SIZE
                + "&key=" + apiKey;
        System.out.println(mapUrl);
        InputStream in = null;
        try {
            URL url = new URL(mapUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            in = conn.getInputStream();
//            BufferedInputStream bufIn = new BufferedInputStream(in);
//            FileOutputStream out = new FileOutputStream("static_map_"+ UUID.randomUUID() +".png");
//            byte[] buffer = new byte[8*1024];
//            int bytesRead;
//            while ((bytesRead = bufIn.read(buffer)) != -1) {
//                out.write(buffer, 0, bytesRead);
//            }
//            out.close();
//            bufIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }
}