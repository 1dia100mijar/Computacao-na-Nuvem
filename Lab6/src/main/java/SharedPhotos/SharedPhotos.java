package SharedPhotos;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.protobuf.Internal;
import com.google.pubsub.v1.*;

import java.io.*;
import java.util.concurrent.ExecutionException;


public class SharedPhotos {
    static String PROJECT_ID = "cn2223-t2-g06";
    static TopicAdminClient topicAdmin;

    public static void main(String[] args) throws Exception {
        topicAdmin = TopicAdminClient.create();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line = "";
         while(!line.equals("q")){
             System.out.println("Escolha uma das seguintes opções:\n" +
                     "1- Criar tópico\n" +
                     "2- Listar tópicos\n" +
                     "3- Criar subscrição\n" +
                     "4- Publicar mensagem\n" +
                     "5- Aceder a subscrição\n" +
                     "q- Terminar\n\n" +
                     "Comando: ");
             line = reader.readLine();
             if(line.equals("q")) return;

             switch(Integer.parseInt(line)){
                 case 1:
                     createTopic(reader);
                     break;
                 case 2:
                     ListTopics();
                     break;
                 case 3:
                     createSubscription(reader);
                     break;
                 case 4:
                     publishMessage(reader);
                     break;
                 case 5:
                     createSubscriber(reader);
                     break;
                 default:
                     System.out.println("Comando inválido\n\n");
                     break;
             }
         }


        topicAdmin.close();
    }

    private static void createTopic(BufferedReader reader) throws IOException {
        System.out.println("Topic name: ");
        TopicName tName=TopicName.ofProjectTopicName(PROJECT_ID, reader.readLine());
        Topic topic=topicAdmin.createTopic(tName);
    }

    private static void ListTopics(){
        TopicAdminClient.ListTopicsPagedResponse res = topicAdmin.listTopics(ProjectName.of(PROJECT_ID));
        for (Topic top : res.iterateAll()) {
            System.out.println("TopicName=" + top.getName());
        }
    }
    private static void createSubscription(BufferedReader reader) throws IOException {
        System.out.println("Introduza um topic name.\n" +
                "Topic name: ");
        String topicName = reader.readLine();
        System.out.println("Subscription name: ");
        String subsName = reader.readLine();

        TopicName tName=TopicName.ofProjectTopicName(PROJECT_ID, topicName);
        SubscriptionName subscriptionName = SubscriptionName.of(PROJECT_ID, subsName);
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create();
        PushConfig pconfig=PushConfig.getDefaultInstance();
        //PushConfig.newBuilder().setPushEndpoint(ConsumerURL).build();
        Subscription subscription = subscriptionAdminClient.createSubscription(subscriptionName, topicName, pconfig, 0);
        subscriptionAdminClient.close();
    }

    private static void publishMessage(BufferedReader reader) throws ExecutionException, InterruptedException, IOException {
        System.out.println("Introduza um topic name. Nao necessita de colocar o TopicName completo\n" +
                "Topic name: ");
        String topicName = reader.readLine();
        System.out.println("Mensagem: ");
        String msgTxt = reader.readLine();

        TopicName tName=TopicName.ofProjectTopicName(PROJECT_ID, topicName);
        Publisher publisher = Publisher.newBuilder(tName).build();
        // Por cada mensagem
        ByteString msgData = ByteString.copyFromUtf8(msgTxt);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(msgData)
                .putAttributes("key1", "value1")
                .build();
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        String msgID = future.get();
        System.out.println("Message Published with ID=" + msgID);
        // No fim de enviar as mensagens
        publisher.shutdown();
    }
    private static void createSubscriber(BufferedReader reader) throws IOException {
        System.out.println("Subscription name: ");
        String subsName = reader.readLine();
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(PROJECT_ID, subsName);

        ExecutorProvider executorProvider = InstantiatingExecutorProvider
                .newBuilder()
                .setExecutorThreadCount(1) // um só thread no handler
                .build();

        Subscriber subscriber =
                Subscriber.newBuilder(subscriptionName, new MessageReceiveHandler())
                        .setExecutorProvider(executorProvider)
                        .build();
        subscriber.startAsync().awaitRunning();
        System.out.println("Para parar a subscrição introduza um caracter\n");
        String line = reader.readLine();
        while(line.isEmpty()){
            line = reader.readLine();
        }
        subscriber.stopAsync();
    }
}
