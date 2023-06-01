package cn2223tf.PubSub;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SchemaServiceClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PubSub {
    static String PROJECT_ID = "cn2223-t2-g06";
    static TopicAdminClient topicAdmin;
    static String PubSubTopicName = "cn2223-g06-tf";
    static String Schema_ID = "cn2223-g06-tf";
    static String avscFile = "schema_structure.json";

    public static void createPubSubTopic() throws IOException {
        topicAdmin = TopicAdminClient.create();
        if(!verifyIfTopicExists(PubSubTopicName)){
            System.out.println("Creating topic PUBSUB");
            TopicName tName=TopicName.ofProjectTopicName(PROJECT_ID, PubSubTopicName);
            Topic topic=topicAdmin.createTopic(tName);
        }
    }
    public static boolean verifyIfTopicExists(String topicName){
        TopicAdminClient.ListTopicsPagedResponse res = topicAdmin.listTopics(ProjectName.of(PROJECT_ID));
        boolean haveTopic = false;
        for (Topic top : res.iterateAll()) {
            String name = top.getName().split("/")[3];
            if(name.equals(topicName)) {haveTopic=true; break;}
        }
        return haveTopic;
    }

    public static void publishMessage(String requestId, String bucketName, String blobName) throws ExecutionException, InterruptedException, IOException {
        String topicName = PubSubTopicName;


        TopicName tName=TopicName.ofProjectTopicName(PROJECT_ID, topicName);
        Publisher publisher = Publisher.newBuilder(tName).build();

        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .putAllAttributes(ImmutableMap.of("requestId", requestId, "bucketName", bucketName,"blobName", blobName ))
                .build();
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        String msgID = future.get();
        System.out.println("Message Published with ID=" + msgID);
        // No fim de enviar as mensagens
        publisher.shutdown();
    }

}
