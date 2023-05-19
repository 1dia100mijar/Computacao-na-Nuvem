
import com.google.protobuf.Empty;
import forum.*;
/*import forum.ExistingTopics;
import forum.ForumGrpc;
import forum.ForumMessage;
import forum.SubscribeUnSubscribe;*/
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class ForumClient {
    private static String svcIP = "localhost"; private static int svcPort = 8000;
    private static ManagedChannel channel;
    private static ForumGrpc.ForumStub nonBlockStub;

    static void topicSubscribe() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("UsrName:");
        String usrName = scanner.nextLine();
        System.out.println("Topic:");
        String topic = scanner.nextLine();
        ForumClientObserver stream = new ForumClientObserver();
        SubscribeUnSubscribe subscribe =SubscribeUnSubscribe.newBuilder().setUsrName(usrName).setTopicName(topic).build();
        nonBlockStub.topicSubscribe(subscribe, stream);

        while (!stream.isCompleted()) {
            System.out.println("Active and waiting for TopicSubscribe to complete");
            Thread.sleep(1 * 1000);
        }
        if (stream.OnSuccesss()) {
            for (ForumMessage message : stream.getForumMessages()) {
                System.out.println("ForumMessage Success!!!");
                System.out.println("FromUser: "+message.getFromUser());
                System.out.println("TopicMessage: "+message.getTopicName());
                System.out.println("Message: "+message.getTxtMsg());
            }
        }

    }
    static void topicUnSubscribe() throws InterruptedException{

    }

    public static void main(String[] args) {
        try{
            channel = ManagedChannelBuilder.forAddress(svcIP, svcPort)
                    // Channels are secure by default (via SSL/TLS).
                    // For the example we disable TLS to avoid needing certificates.
                    .usePlaintext()
                    .build();
            nonBlockStub = ForumGrpc.newStub(channel);
            topicSubscribe();
            System.out.println("Hello from ClientApp!");
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }

}

 /*
    3-
Relativamente ao serviço Storage do GCP, contas de serviço e Java API, indique quais as questões Verdadeiras ou Falsas
F Em dois projetos GCP diferentes é possível ter buckets com o mesmo nome, no entanto, os nomes dos Blobs num determinado
bucket têm de ser diferentes.
V Para suportar diferentes níveis de controlo de acessos (público ou privado) nos Blobs de um bucket é necessário configurar o
bucket como fine-grained.
V No Laboratório 4, quando a aplicação cliente envia uma mensagem com indicação de um bucket e de um Blob para um
determinado tópico, só os clientes, que tenham previamente subscrito esse tópico, recebem a mensagem e assim podem fazer download do Blob.
F Todas as aplicações que façam acessos a Blobs de um bucket de um determinado projeto GCP têm forçosamente de partilhar a
mesma conta de serviço.
     */