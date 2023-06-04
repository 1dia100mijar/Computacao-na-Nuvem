package cn2223tf;

import io.grpc.stub.StreamObserver;

public class ClientStreamUpload implements StreamObserver<BlobIdentifier>{
    private boolean isCompleted=false; private boolean success=false;
    public boolean OnSuccesss() { return success; }
    public boolean isCompleted() { return isCompleted; }

    BlobIdentifier blobsIdentifier;

    public BlobIdentifier getBlobsIdentifiers(){return blobsIdentifier;}

    @Override
    public void onNext(BlobIdentifier blobIdentifier) {
        blobsIdentifier = blobIdentifier;
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error on call:"+throwable.getMessage());
        isCompleted=true; success=false;
    }

    @Override
    public void onCompleted() {
        System.out.println("Upload Completed!");
        isCompleted=true; success=true;
        System.out.println("Request ID: " + blobsIdentifier.getId()+"\n");
    }
}
