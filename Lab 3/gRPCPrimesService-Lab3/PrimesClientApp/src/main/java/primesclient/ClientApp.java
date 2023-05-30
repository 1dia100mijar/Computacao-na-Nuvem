package primesclient;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import primesservice.PrimesServiceGrpc;
import primesservice.Text;
import primesservice.*;
import primesservice.Void;

public class ClientApp {
    private static String svcIP = "localhost"; private static int svcPort = 8000;
    private static ManagedChannel channel;
    private static PrimesServiceGrpc.PrimesServiceBlockingStub blockingStub;
    private static PrimesServiceGrpc.PrimesServiceStub noBlockStub;
    private static PrimesServiceGrpc.PrimesServiceFutureStub futStub;

    static void isAliveBlock(){
        Text text = blockingStub.isAlive(Void.newBuilder().build());
        System.out.println(text.getMsg());
    };
    static void findPrimes() throws InterruptedException {
        int startNumber = 1, endNumber = 500;
        int elems_per_thread = 100;
        int interval = endNumber - startNumber;
        int nCalls = Math.round(interval/elems_per_thread);

        ClientStreamObserver[] streams = new ClientStreamObserver[nCalls];

        for(int i = 0; i < nCalls ; ++i){
            int start = startNumber+(elems_per_thread*i);
            int endTemp = start+elems_per_thread-1;
            if(i == nCalls-1) ++endTemp;
            int end = endTemp;

            PrimesInterval primesInterval = PrimesInterval.newBuilder().setStartNum(1+i * 100).setEndNum(i * 100).build();
            ClientStreamObserver clientStreamObserver = new ClientStreamObserver();
            noBlockStub.findPrimes(primesInterval, clientStreamObserver);
            while (!clientStreamObserver.isCompleted()) {
                System.out.println("Active and waiting for Case2 completed ");
                Thread.sleep(1 * 1000);
            }
            if (clientStreamObserver.OnSuccesss()) {
                for (Prime prime : clientStreamObserver.getPrimes()) {
                    System.out.println("Reply non BlockStub:"+prime.getPrime());
                }
            }
        }


    }

    public static void main(String[] args) {
        try{
            channel = ManagedChannelBuilder.forAddress(svcIP, svcPort)
                    // Channels are secure by default (via SSL/TLS).
                    // For the example we disable TLS to avoid needing certificates.
                    .usePlaintext()
                    .build();
            blockingStub = PrimesServiceGrpc.newBlockingStub(channel);
            noBlockStub = PrimesServiceGrpc.newStub(channel);
            futStub = PrimesServiceGrpc.newFutureStub(channel);
            isAliveBlock();
            findPrimes();
            System.out.println("Hello from ClientApp!");
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }



}
