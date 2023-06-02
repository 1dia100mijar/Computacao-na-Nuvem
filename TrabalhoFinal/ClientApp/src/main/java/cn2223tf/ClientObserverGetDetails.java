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
        if(landMarksResult.getLandmarkList().size() == 0){
            System.out.println("\nThe photo associated with this request id don't have landmarks!\n");
        }
        else {
            System.out.println("Landmarks:");
            for(LandmarkElement landmark: landMarksResult.getLandmarkList()){
                System.out.println("Landmark: "+landmark.getName()
                        + "\nLatitude: " + landmark.getLatitude()
                        + "\nLongitude: " + landmark.getLongitude()
                        + "\nAccuracy: " + landmark.getAccuracy() + "\n");
            }
            System.out.println("\n");
        }
        isCompleted=true;success=true;
    }
}
