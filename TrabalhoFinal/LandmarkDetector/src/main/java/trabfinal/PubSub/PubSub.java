package trabfinal.PubSub;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.pubsub.v1.*;
import com.google.cloud.pubsub.v1.*;
import com.google.common.collect.ImmutableMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class PubSub {
    static String PROJECT_ID = "cn2223-t2-g06";
    static TopicAdminClient topicAdmin;
    static String PubSubTopicName = "cn2223-g06-tf";
    static String subscriberName = "cn2223-g06-tf-subscriber";
    static String subscriptionName = "cn2223-g06-tf-subscription";

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

    public static void createSubscription() throws IOException {
        TopicName tName=TopicName.ofProjectTopicName(PROJECT_ID, PubSubTopicName);
        SubscriptionName subsName = SubscriptionName.of(PROJECT_ID, subscriptionName);
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create();
        PushConfig pconfig=PushConfig.getDefaultInstance();
        subscriptionAdminClient.createSubscription(subsName, tName, pconfig, 0);
        subscriptionAdminClient.close();
        System.out.println("Subscription created");
    }

    public static void createSubscriber() {
        ProjectSubscriptionName SubscriptionName = ProjectSubscriptionName.of(PROJECT_ID, subscriptionName);

        ExecutorProvider executorProvider = InstantiatingExecutorProvider
                .newBuilder()
                .setExecutorThreadCount(1) // um só thread no handler
                .build();

        Subscriber subscriber =
                Subscriber.newBuilder(SubscriptionName, new MessageReceiveHandler())
                        .setExecutorProvider(executorProvider)
                        .build();
        subscriber.startAsync().awaitRunning();
        System.out.println("Subscriber created");
        subscriber.awaitTerminated();
    }

}
