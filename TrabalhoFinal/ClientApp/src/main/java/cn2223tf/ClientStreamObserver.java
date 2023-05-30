package cn2223tf;

import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class ClientStreamObserver implements StreamObserver<BlobIdentifier>{
    private boolean isCompleted=false; private boolean success=false;
    public boolean OnSuccesss() { return success; }
    public boolean isCompleted() { return isCompleted; }

    BlobIdentifier blobsIdentifier;

    public BlobIdentifier getBlobsIdentifiers(){return blobsIdentifier;}

    @Override
    public void onNext(BlobIdentifier blobIdentifier) {
        System.out.println("BlobIdentifier (" + blobIdentifier.getId()+ ")");
        blobsIdentifier = blobIdentifier;
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error on call:"+throwable.getMessage());
        isCompleted=true; success=false;
    }

    @Override
    public void onCompleted() {
        System.out.println("Stream completed");
        isCompleted=true; success=true;
        System.out.println("Photo ID: " + blobsIdentifier.getId());
    }
}
