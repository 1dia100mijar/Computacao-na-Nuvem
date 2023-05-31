package cn2223tf;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Scanner;

public class Server extends CN2223TFGrpc.CN2223TFImplBase {
    //Storage
    static String BUCKETNAME = "cn2223-g06-tf";

    private static int svcPort = 8000;
    public static void main(String[] args) throws IOException {

        try{
            io.grpc.Server svc = ServerBuilder.forPort(svcPort).addService(new Server()).build();
            svc.start();
            System.out.println("Server started, listening on " + svcPort);
            System.out.println("Hello, gRPC world!");
            Scanner scan = new Scanner(System.in);
            scan.nextLine();
            svc.shutdown();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public StreamObserver<Block> uploadPhoto(StreamObserver<BlobIdentifier> responseObserver) {
        System.out.println("Upload photo called");
        return new ServerStreamObserverBlobIdentifier(responseObserver);
    }

    @Override
    public void getLandmarks(BlobIdentifier request, StreamObserver<LandMarksResult> responseObserver){
    }

    @Override
    public void getMapImage(BlobIdentifier request, StreamObserver<MapResult> responseObserver){

    }

    @Override
    public void getAccurateLandmarks(Accuracy request, StreamObserver<AccuracyResult> responseObserver){

    }

}
