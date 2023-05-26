package cn2223tf;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Scanner;

public class Server extends CN2223TFGrpc.CN2223TFImplBase {
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
        Text text = Text.newBuilder().setMsg("aliveMessage").build();


        return null;
    }

}
