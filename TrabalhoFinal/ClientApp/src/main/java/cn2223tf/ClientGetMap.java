package cn2223tf;

import io.grpc.stub.StreamObserver;

public class ClientGetMap implements StreamObserver<LandMarksResult> {
    public boolean isCompleted=false; public boolean success=false;
    byte[] map=null;
    @Override
    public void onNext(LandMarksResult landMarksResult) {

    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error on call:"+throwable.getMessage());
        isCompleted=true; success=false;
    }

    @Override
    public void onCompleted() {
        isCompleted=true;success=true;
    }
}
