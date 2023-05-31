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
            TopicName tName=TopicName.ofProjectTopicName(PROJECT_ID, PubSubTopicName);
            Schema schema = createSchema();

            SchemaSettings schemaSettings =
                    SchemaSettings.newBuilder()
                            .setSchema(schema.getName())
                            .build();
            Topic topic =
                    topicAdmin.createTopic(
                            Topic.newBuilder()
                                    .setName(tName.toString())
                                    .setSchemaSettings(schemaSettings)
                                    .build());

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

    public static Schema createSchema() throws IOException {
//        ProjectName projectName = ProjectName.of(PROJECT_ID);
//        SchemaName schemaName = SchemaName.of(PROJECT_ID, Schema_ID);
//
//        // Read an Avro schema file formatted in JSON as a string.
//        String avscSource = new String(Files.readAllBytes(Paths.get(avscFile)));
//
//        SchemaServiceClient schemaServiceClient= SchemaServiceClient.create();
//
//        Schema schema = schemaServiceClient.createSchema(
//                        projectName,
//                        Schema.newBuilder()
//                                .setName(schemaName.toString())
//                                .setType(Schema.Type.AVRO)
//                                .setDefinition(avscSource)
//                                .build(),
//                        Schema_ID);
//        System.out.println("Schema Created!");
//        return schema;
        return null;
    }

    public static void publishMessage(String requestId, String bucketName, String blobName) throws ExecutionException, InterruptedException, IOException {
        String topicName = PubSubTopicName;


        TopicName tName=TopicName.ofProjectTopicName(PROJECT_ID, topicName);
        Publisher publisher = Publisher.newBuilder(tName).build();

        Map<String, String> map = new HashMap<>();
        map.put("requestId", requestId);
        map.put("bucketName", bucketName);
        map.put("blobName", blobName);


        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .putAllAttributes(ImmutableMap.of("requestId", requestId, "bucketName", bucketName,"blobName", blobName ))
                .build();
        System.out.println(pubsubMessage);
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        String msgID = future.get();
        System.out.println("Message Published with ID=" + msgID);
        // No fim de enviar as mensagens
        publisher.shutdown();
    }

}
