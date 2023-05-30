package cn2223tf;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.units.qual.C;

import java.awt.image.BufferedImageOp;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientApp {
    private static String svcIP = "localhost"; private static int svcPort = 8000;
    private static ManagedChannel channel;
    private static CN2223TFGrpc.CN2223TFStub noBlockStub;

    public static void main(String[] args){
        try{
            channel = ManagedChannelBuilder.forAddress(svcIP, svcPort)
                    // Channels are secure by default (via SSL/TLS).
                    // For the example we disable TLS to avoid needing certificates.
                    .usePlaintext()
                    .build();
            noBlockStub = CN2223TFGrpc.newStub(channel);
            uploadPhoto();
            System.out.println("Hello from ClientApp!");
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }

    static void uploadPhoto() throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Introduza o nome do ficheiro");
        String photoName = reader.readLine();
        Path filePath = Paths.get(photoName);
        String contentType = Files.probeContentType(filePath);

        ClientStreamObserver blobIdentifierObserver = new ClientStreamObserver();

        StreamObserver<Block> blockRequest = noBlockStub.uploadPhoto(blobIdentifierObserver);

        byte[] buffer = new byte[1024];
        try (InputStream input = new ByteArrayInputStream(Files.readAllBytes(filePath))) {
            while (input.read(buffer) >= 0) {
                try {
                    blockRequest.onNext(
                            Block.newBuilder().setBlobName(photoName)
                                    .setData(ByteString.copyFrom(buffer))
                                    .setDataType(contentType)
                                    .build()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            blockRequest.onCompleted();
        }
        while (!blobIdentifierObserver.isCompleted()) {
            System.out.println("Active and waiting for Case2 completed ");
            Thread.sleep(1 * 1000);
        }
        if (blobIdentifierObserver.OnSuccesss()) {
            System.out.println("finished");
        }
    }


}
