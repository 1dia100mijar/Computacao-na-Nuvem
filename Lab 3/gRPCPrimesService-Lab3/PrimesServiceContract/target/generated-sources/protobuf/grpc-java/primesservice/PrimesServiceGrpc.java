package primesservice;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.28.1)",
    comments = "Source: PrimesService.proto")
public final class PrimesServiceGrpc {

  private PrimesServiceGrpc() {}

  public static final String SERVICE_NAME = "primesservice.PrimesService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<primesservice.Void,
      primesservice.Text> getIsAliveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "isAlive",
      requestType = primesservice.Void.class,
      responseType = primesservice.Text.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<primesservice.Void,
      primesservice.Text> getIsAliveMethod() {
    io.grpc.MethodDescriptor<primesservice.Void, primesservice.Text> getIsAliveMethod;
    if ((getIsAliveMethod = PrimesServiceGrpc.getIsAliveMethod) == null) {
      synchronized (PrimesServiceGrpc.class) {
        if ((getIsAliveMethod = PrimesServiceGrpc.getIsAliveMethod) == null) {
          PrimesServiceGrpc.getIsAliveMethod = getIsAliveMethod =
              io.grpc.MethodDescriptor.<primesservice.Void, primesservice.Text>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "isAlive"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  primesservice.Void.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  primesservice.Text.getDefaultInstance()))
              .setSchemaDescriptor(new PrimesServiceMethodDescriptorSupplier("isAlive"))
              .build();
        }
      }
    }
    return getIsAliveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<primesservice.PrimesInterval,
      primesservice.Prime> getFindPrimesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "findPrimes",
      requestType = primesservice.PrimesInterval.class,
      responseType = primesservice.Prime.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<primesservice.PrimesInterval,
      primesservice.Prime> getFindPrimesMethod() {
    io.grpc.MethodDescriptor<primesservice.PrimesInterval, primesservice.Prime> getFindPrimesMethod;
    if ((getFindPrimesMethod = PrimesServiceGrpc.getFindPrimesMethod) == null) {
      synchronized (PrimesServiceGrpc.class) {
        if ((getFindPrimesMethod = PrimesServiceGrpc.getFindPrimesMethod) == null) {
          PrimesServiceGrpc.getFindPrimesMethod = getFindPrimesMethod =
              io.grpc.MethodDescriptor.<primesservice.PrimesInterval, primesservice.Prime>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "findPrimes"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  primesservice.PrimesInterval.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  primesservice.Prime.getDefaultInstance()))
              .setSchemaDescriptor(new PrimesServiceMethodDescriptorSupplier("findPrimes"))
              .build();
        }
      }
    }
    return getFindPrimesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PrimesServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PrimesServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PrimesServiceStub>() {
        @java.lang.Override
        public PrimesServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PrimesServiceStub(channel, callOptions);
        }
      };
    return PrimesServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PrimesServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PrimesServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PrimesServiceBlockingStub>() {
        @java.lang.Override
        public PrimesServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PrimesServiceBlockingStub(channel, callOptions);
        }
      };
    return PrimesServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PrimesServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PrimesServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PrimesServiceFutureStub>() {
        @java.lang.Override
        public PrimesServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PrimesServiceFutureStub(channel, callOptions);
        }
      };
    return PrimesServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class PrimesServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void isAlive(primesservice.Void request,
        io.grpc.stub.StreamObserver<primesservice.Text> responseObserver) {
      asyncUnimplementedUnaryCall(getIsAliveMethod(), responseObserver);
    }

    /**
     */
    public void findPrimes(primesservice.PrimesInterval request,
        io.grpc.stub.StreamObserver<primesservice.Prime> responseObserver) {
      asyncUnimplementedUnaryCall(getFindPrimesMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getIsAliveMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                primesservice.Void,
                primesservice.Text>(
                  this, METHODID_IS_ALIVE)))
          .addMethod(
            getFindPrimesMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                primesservice.PrimesInterval,
                primesservice.Prime>(
                  this, METHODID_FIND_PRIMES)))
          .build();
    }
  }

  /**
   */
  public static final class PrimesServiceStub extends io.grpc.stub.AbstractAsyncStub<PrimesServiceStub> {
    private PrimesServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PrimesServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PrimesServiceStub(channel, callOptions);
    }

    /**
     */
    public void isAlive(primesservice.Void request,
        io.grpc.stub.StreamObserver<primesservice.Text> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getIsAliveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void findPrimes(primesservice.PrimesInterval request,
        io.grpc.stub.StreamObserver<primesservice.Prime> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getFindPrimesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PrimesServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<PrimesServiceBlockingStub> {
    private PrimesServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PrimesServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PrimesServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public primesservice.Text isAlive(primesservice.Void request) {
      return blockingUnaryCall(
          getChannel(), getIsAliveMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<primesservice.Prime> findPrimes(
        primesservice.PrimesInterval request) {
      return blockingServerStreamingCall(
          getChannel(), getFindPrimesMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PrimesServiceFutureStub extends io.grpc.stub.AbstractFutureStub<PrimesServiceFutureStub> {
    private PrimesServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PrimesServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PrimesServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<primesservice.Text> isAlive(
        primesservice.Void request) {
      return futureUnaryCall(
          getChannel().newCall(getIsAliveMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_IS_ALIVE = 0;
  private static final int METHODID_FIND_PRIMES = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PrimesServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PrimesServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_IS_ALIVE:
          serviceImpl.isAlive((primesservice.Void) request,
              (io.grpc.stub.StreamObserver<primesservice.Text>) responseObserver);
          break;
        case METHODID_FIND_PRIMES:
          serviceImpl.findPrimes((primesservice.PrimesInterval) request,
              (io.grpc.stub.StreamObserver<primesservice.Prime>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class PrimesServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PrimesServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return primesservice.PrimesServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PrimesService");
    }
  }

  private static final class PrimesServiceFileDescriptorSupplier
      extends PrimesServiceBaseDescriptorSupplier {
    PrimesServiceFileDescriptorSupplier() {}
  }

  private static final class PrimesServiceMethodDescriptorSupplier
      extends PrimesServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PrimesServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PrimesServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PrimesServiceFileDescriptorSupplier())
              .addMethod(getIsAliveMethod())
              .addMethod(getFindPrimesMethod())
              .build();
        }
      }
    }
    return result;
  }
}
