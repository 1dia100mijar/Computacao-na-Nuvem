package primesservice;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;

public class PrimesServer extends PrimesServiceGrpc.PrimesServiceImplBase {
    private static int svcPort = 8000;
    public static void main(String[] args) {
        try{
            io.grpc.Server svc = ServerBuilder.forPort(svcPort).addService(new PrimesServer()).build();
            svc.start();
            System.out.println("Server started, listening on " + svcPort);
            System.out.println("Hello, gRPC world!");
            Scanner scan = new Scanner(System.in);
            scan.nextLine();
            svc.shutdown();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void isAlive(Void request, StreamObserver<Text> responseObserver) {
        System.out.println("isAlive called");
        Text text = Text.newBuilder().setMsg("aliveMessage").build();
        responseObserver.onNext(text);
        responseObserver.onCompleted();
    }

    @Override
    public void findPrimes(PrimesInterval primesInterval, StreamObserver<Prime> responseObserver){
        System.out.println("findPrimes called");
        for(int i = primesInterval.getStartNum(); i <= primesInterval.getEndNum(); ++i){
            if(isPrime(i)){
                Prime prime = Prime.newBuilder().setPrime(i).build();
                responseObserver.onNext(prime);
            }
        }
        responseObserver.onCompleted();
    }
    private static boolean isPrime(int num) {
        if (num <= 1) return false;
        if (num == 2 || num == 3) return true;
        if (num % 2 == 0) return false;
        for (int i=3; i <= Math.sqrt(num); i+=2) {
            if (num % i == 0) return false;
        }
        return true;
    }
}
