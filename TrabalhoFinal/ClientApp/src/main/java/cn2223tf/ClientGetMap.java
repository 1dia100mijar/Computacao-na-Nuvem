package cn2223tf;

import io.grpc.stub.StreamObserver;

import java.io.*;
import java.nio.file.Files;

public class ClientGetMap implements StreamObserver<MapResult> {
    public boolean isCompleted=false; public boolean success=false;
    byte[] map = new byte[0];
    @Override
    public void onNext(MapResult mapResult) {
        byte[] mapBlock = mapResult.getMap().toByteArray();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] combinedArray = new byte[map.length + mapBlock.length];
        System.arraycopy(map, 0, combinedArray, 0, map.length);
        System.arraycopy(mapBlock, 0, combinedArray, map.length, mapBlock.length);
        map = combinedArray;
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error on call:"+throwable.getMessage());
        isCompleted=true; success=false;
    }

    @Override
    public void onCompleted() {
        if(map.length == 0){
            System.out.println("\nThere is no map associated with this request id\n");
        }
        else{
            System.out.println("Indique o nome do ficheiro que pretende fazer download: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String fileName = null;
            try {
                fileName = reader.readLine();
                fileName = fileName + ".png";
                System.out.println(fileName);
                FileOutputStream outputStream = new FileOutputStream(fileName);
                outputStream.write(map);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
                isCompleted=true;success=true;
    }
}