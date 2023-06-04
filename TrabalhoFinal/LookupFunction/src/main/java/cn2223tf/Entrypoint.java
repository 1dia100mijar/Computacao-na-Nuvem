package cn2223tf;

import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.checkerframework.checker.units.qual.A;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entrypoint implements HttpFunction {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        BufferedWriter writer = response.getWriter();
        String projectID = "cn2223-t2-g06";
        String zone = request.getFirstQueryParameter("zone").orElse("europe-west9-a");
        try (InstancesClient client = InstancesClient.create()) {
            for (Instance instance : client.list(projectID, zone).iterateAll()) {
                if (instance.getStatus().compareTo("RUNNING") == 0 && instance.getName().contains("instance-group-server")) {
                    String ip = instance.getNetworkInterfaces(0).getAccessConfigs(0).getNatIP();
                    writer.write(ip+"/\n");
                }
            }
        }catch (Exception e){
            writer.write(e.getMessage());
        }
    }
}
