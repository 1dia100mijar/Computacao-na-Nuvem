package cn2223tf;

import com.google.rpc.context.AttributeContext;
import io.grpc.stub.StreamObserver;

public class ServerStreamObserver implements StreamObserver<Block> {
    StreamObserver<Block> sFinalreply; String finalText="";
    public ServerStreamObserver(StreamObserver<Block> sreplies) {
        this.sFinalreply=sreplies;
    }
    @Override
    public void onNext(Request request) {
// More one request
        finalText += request.getTxt() + ":";
    }
    @Override
    public void onError(Throwable throwable) { . . . }
    @Override
    public void onCompleted() {
        Reply rply = Reply.newBuilder()
                .setRplyID(9999)
                .setTxt(finalText.toUpperCase()).build();
        sFinalreply.onNext(rply);
        sFinalreply.onCompleted();
