package cn2223tf;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class ClientApp {
    private static String svcIP = "localhost"; private static int svcPort = 8000;
    private static ManagedChannel channel;
    private static CN2223TFGrpc.CN2223TFStub noBlockStub;

    public static void main(String[] args){
        try{
            channel = ManagedChannelBuilder.forAddress("34.163.40.228", svcPort)
                    // Channels are secure by default (via SSL/TLS).
                    // For the example we disable TLS to avoid needing certificates.
                    .usePlaintext()
                    .build();
            noBlockStub = CN2223TFGrpc.newStub(channel);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            while(true){
                System.out.println("Escolha uma das seguintes opções:\n" +
                        "1- Enviar Imagem\n" +
                        "2- Obter resultados de imagem submetida\n" +
                        "3- Obter o mapa da imagem sumetida\n" +
                        "4- Obter nomes de fotos por grau de certeza\n" +
                        "5- Sair\n"+
                        "Comando: ");
                line = reader.readLine();
                if(line.equals("q")) return;
                switch(Integer.parseInt(line)){
                    case 1:
                        uploadPhoto();
                        break;
                    case 2:
                        getResults(reader);
                        break;
                    case 3:
                        getMap(reader);
                        break;
                    case 4:
                        photosNameWithScoreBiggerThan(reader);
                        break;
                    case 5:
                        channel.shutdown();
                        channel.awaitTermination(5, TimeUnit.SECONDS);
                        return;
                    default:
                        System.out.println("Comando inválido\n\n");
                        break;
                }
            }
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

        //Send photo in stream Blocks
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
            System.out.println("Waiting for server to Upload Photo");
            Thread.sleep(2 * 1000);
        }
    }

    static void getResults(BufferedReader reader) throws IOException, InterruptedException {
        System.out.println("Introduza o Id do request: ");
        String requestId = reader.readLine();

        BlobIdentifier blobIdentifier = BlobIdentifier.newBuilder()
                        .setId(requestId).build();
        ClientObserverGetDetails responseObserver = new ClientObserverGetDetails();
        noBlockStub.getLandmarks(blobIdentifier, responseObserver);

        while (!responseObserver.isCompleted) {
            Thread.sleep(1 * 1000);
        }
    }
    static void getMap(BufferedReader reader) throws IOException, InterruptedException {
        System.out.println("Introduza o Id do request: ");
        String requestId = reader.readLine();

        BlobIdentifier blobIdentifier = BlobIdentifier.newBuilder()
                .setId(requestId).build();

        ClientGetMap getMap = new ClientGetMap();
        noBlockStub.getMapImage(blobIdentifier, getMap);

        while (!getMap.isCompleted) {
            Thread.sleep(1 * 1000);
        }
    }
    static void photosNameWithScoreBiggerThan(BufferedReader reader) throws IOException, InterruptedException {
        System.out.println("Introduza o grau de certeza minimo que pretende entre 0 e 1: ");
        Float accuracyValue = null;
        try{
            accuracyValue = Float.valueOf(String.valueOf(reader.readLine()));
        }catch (Exception e){
            System.out.println("O valor não foi inserido corretamente!" +
                    "\nIntroduza o grau de certeza minimo que pretende entre 0 e 1: ");
            accuracyValue = Float.valueOf(String.valueOf(reader.readLine()));
        }


        Accuracy accuracy = Accuracy.newBuilder()
                .setAccuracy(accuracyValue).build();
        ClientGetAccuracy getAccuracy = new ClientGetAccuracy(accuracyValue);
        noBlockStub.getAccurateLandmarks(accuracy, getAccuracy);
        while (!getAccuracy.isCompleted) {
            Thread.sleep(1 * 1000);
        }
    }


}
