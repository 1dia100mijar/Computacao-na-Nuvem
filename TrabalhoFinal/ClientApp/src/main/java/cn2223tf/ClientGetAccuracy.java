package cn2223tf;

import io.grpc.stub.StreamObserver;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class ClientGetAccuracy implements StreamObserver<AccuracyResult> {
    public boolean isCompleted=false; public boolean success=false;

    List<LandMarkAccuracyResult> landMarkAccuracyResults;
    private float accuracy;

    public ClientGetAccuracy(Float accuracy){
        landMarkAccuracyResults = new ArrayList<>();
        this.accuracy = accuracy;
    }

    @Override
    public void onNext(AccuracyResult accuracyResult) {
        landMarkAccuracyResults = accuracyResult.getLandmarkList();
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error on call:"+throwable.getMessage());
        isCompleted=true; success=false;
    }

    @Override
    public void onCompleted() {
        if(landMarkAccuracyResults.isEmpty()){
            System.out.println("\nNão existem resultados para um nivel de certeza de " + accuracy);
            isCompleted=true;success=true;
        }
        else {
            System.out.println("\nResultados obtidos para um nivel de certeza de " + accuracy);
            for (LandMarkAccuracyResult landmarkResult:landMarkAccuracyResults){
                System.out.println("Landmark: " + landmarkResult.getName() +
                        "\n\tblob: " + landmarkResult.getImageName() + "\n");
            }
            isCompleted=true;success=true;
        }
    }
}
