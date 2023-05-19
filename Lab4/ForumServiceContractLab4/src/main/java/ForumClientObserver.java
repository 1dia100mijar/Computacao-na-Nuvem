import forum.ForumMessage;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;

public class ForumClientObserver implements StreamObserver<ForumMessage> {
    private boolean isCompleted=false; private boolean success=false;
    public boolean OnSuccesss() { return success; }
    public boolean isCompleted() { return isCompleted; }
    List<ForumMessage> forumMessages = new ArrayList<ForumMessage>();
    public List<ForumMessage> getForumMessages() { return forumMessages; }


    @Override
    public void onNext(ForumMessage forumMessage) {
        System.out.println("ForumMessage");
        System.out.println("FromUser: "+forumMessage.getFromUser());
        System.out.println("TopicMessage: "+forumMessage.getTopicName());
        System.out.println("Message: "+forumMessage.getTxtMsg());
        forumMessages.add(forumMessage);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error on call:"+throwable.getMessage());
        isCompleted=true; success=false;
    }
    @Override
    public void onCompleted() {
        System.out.println("Stream completed");
        isCompleted=true; success=true;
    }
}
