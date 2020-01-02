package demo.nature;

/**
 * @Author nature
 * @Mail 924943578@qq.com
 * ProtoInputStream 35
 */

import io.grpc.ManagedChannel;
import io.grpc.examples.nameserver.Ip;
import io.grpc.examples.nameserver.Name;
import io.grpc.examples.nameserver.NameServiceGrpc;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class NameSSLClient {

    private static final String DEFAULT_HOST = "localhost";
//    private static final String DEFAULT_HOST = "proxy.backend.com";
//    private static final String DEFAULT_HOST = "129.28.194.119";
//    private static final String DEFAULT_HOST = "35.221.214.238";

    private static final int DEFAULT_PORT = 40000;//nginx
//    private static final int DEFAULT_PORT = 80;//apache2
//    private static final int DEFAULT_PORT = 30888;//local server
//    private static final int DEFAULT_PORT = 30001;//haproxy certs

    private static final String trustCertCollectionFilePath = "/certs/ca.crt";


    private static String certChainFilePath = "/certs/grpcclient.crt";
    //换cert测试双向认证有没有生效
//    private static String certChainFilePath = "/certs/grpcserver.crt";

//    private static String privateKeyFilePath = "/certs/grpcserver.key";
    private static String privateKeyFilePath = "/certs/grpcclient.key";


    private ManagedChannel managedChannel;

    //服务存根，用于客户端本地调用
    private NameServiceGrpc.NameServiceBlockingStub nameServiceBlockingStub;


    private static SslContext buildSslContext() throws SSLException, URISyntaxException {
        SslContextBuilder builder = GrpcSslContexts.forClient();

        File trustCertCollectionFile = new File(NameSSLClient.class.getResource(trustCertCollectionFilePath).toURI());

        File certChainFile = new File(NameSSLClient.class.getResource(certChainFilePath).toURI());
        File privateKeyFile = new File(NameSSLClient.class.getResource(privateKeyFilePath).toURI());

        builder.keyManager(certChainFile, privateKeyFile);
        builder.trustManager(trustCertCollectionFile);


        return builder.build();
    }

    public NameSSLClient(String host, int port) throws SSLException, URISyntaxException {

        this(NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.TLS)
                .overrideAuthority("demo.com")//demo.com input by generating server1.csr
//                .overrideAuthority("root.com")//demo.com input by generating server1.csr
                .sslContext(buildSslContext())
                .build());

    }

    public NameSSLClient(ManagedChannel managedChannel) {
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

    public static void main(String[] args) throws SSLException, URISyntaxException {

        NameSSLClient nameSSLClient = new NameSSLClient(DEFAULT_HOST,DEFAULT_PORT);

        for(String arg : args){

            String res = nameSSLClient.getIpByName(arg);

            System.out.println("get result from server: " + res + " as param is " + arg);

        }

    }

}
