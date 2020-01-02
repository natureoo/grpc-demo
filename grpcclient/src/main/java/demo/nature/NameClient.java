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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NameClient {

    private static final String DEFAULT_HOST = "localhost";
//    private static final String DEFAULT_HOST = "129.28.194.119";
//    private static final String DEFAULT_HOST = "35.221.214.238";

    private static final int DEFAULT_PORT = 40000;//nginx
//    private static final int DEFAULT_PORT = 80;//apache2
//    private static final int DEFAULT_PORT = 30888;//local server
//    private static final int DEFAULT_PORT = 8080;//haproxy

    private ManagedChannel managedChannel;

    private static final String datePattern = "yyyy-MM-dd HH:mm:ss.SSS";

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

        String ip = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        int index = 0;
        if(args.length > 2) {
            ip = args[0];
            port = Integer.valueOf(args[1]);
            index = 2;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        System.out.println("------------------" + dateFormat.format(new Date()) + "------------------"  );
        System.out.println("req ip: " + ip);
        System.out.println("req port: " + port);


        NameClient nameClient = new NameClient(ip,port);

        for(int i = index; i < args.length; i++){
            String arg = args[i];

            String res = nameClient.getIpByName(arg);

            System.out.println("get result from server: " + res + " as param is " + arg);

        }

    }

}
