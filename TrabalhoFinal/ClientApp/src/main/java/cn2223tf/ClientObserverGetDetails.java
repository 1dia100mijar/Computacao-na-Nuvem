package cn2223tf;

import io.grpc.stub.StreamObserver;

public class ClientObserverGetDetails implements StreamObserver<LandMarksResult> {
    public boolean isCompleted=false; public boolean success=false;
    LandMarksResult landMarksResult;

    @Override
    public void onNext(LandMarksResult landMarkResult) {
        landMarksResult = landMarkResult;
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error on call:"+throwable.getMessage());
        isCompleted=true; success=false;
    }

    @Override
    public void onCompleted() {
        System.out.println("Upload Completed!");
        System.out.println("Landmarks:");
        for(LandmarkElement landmark: landMarksResult.getLandmarkList()){
            System.out.println("Landmark: "+landmark.getName()
                + "\nLatitude: " + landmark.getLatitude()
                + "\nLongitude: " + landmark.getLongitude()
                + "\nAccuracy: " + landmark.getAccuracy() + "\n");
        }
        isCompleted=true;success=true;
    }


}
