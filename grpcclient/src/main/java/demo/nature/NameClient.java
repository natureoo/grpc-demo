package demo.nature;

/**
 * @Author nature
 * @Mail 924943578@qq.com
 * ProtoInputStream 35
 */

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.nameserver.Ip;
import io.grpc.examples.nameserver.Name;
import io.grpc.examples.nameserver.NameServiceGrpc;

import java.util.concurrent.TimeUnit;

public class NameClient {

    private static final String DEFAULT_HOST = "localhost";
//    private static final String DEFAULT_HOST = "129.28.194.119";
//    private static final String DEFAULT_HOST = "35.221.214.238";

//    private static final int DEFAULT_PORT = 30000;//nginx
//    private static final int DEFAULT_PORT = 80;//apache2
//    private static final int DEFAULT_PORT = 30888;//local server
    private static final int DEFAULT_PORT = 8080;//haproxy

    private ManagedChannel managedChannel;

    //服务存根，用于客户端本地调用
    private NameServiceGrpc.NameServiceBlockingStub nameServiceBlockingStub;

    public NameClient(String host,int port) {

        this(ManagedChannelBuilder.forAddress(host,port).usePlaintext(true).build());

    }

    public NameClient(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
        this.nameServiceBlockingStub = NameServiceGrpc.newBlockingStub(managedChannel);
    }

    public void shutdown() throws InterruptedException {
        managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public String getIpByName(String n){

        Name name = Name.newBuilder().setName(n).build();

        Ip ip = nameServiceBlockingStub.getIpByName(name);

        return ip.getIp();
    }

    public static void main(String[] args) {

        NameClient nameClient = new NameClient(DEFAULT_HOST,DEFAULT_PORT);

        for(String arg : args){

            String res = nameClient.getIpByName(arg);

            System.out.println("get result from server: " + res + " as param is " + arg);

        }

    }

}
