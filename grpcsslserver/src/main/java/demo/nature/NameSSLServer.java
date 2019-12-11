package demo.nature;

/**
 * @Author nature
 * @Mail 924943578@qq.com
 * ProtoInputStream 35
 */


import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class NameSSLServer {

    private Logger logger = Logger.getLogger(NameSSLServer.class.getName());

//    private static final int DEFAULT_PORT = 8088;
    private static final int DEFAULT_PORT = 30888;

    private int port;//服务端口号

    private Server server;

    private String certChainFilePath = "/certs/grpcserver.crt";

    private String privateKeyFilePath = "/certs/grpcserver.key";

//    public NameSSLServer(int port) {
//        this(port,ServerBuilder.forPort(port));
//    }

    private SslContextBuilder getSslContextBuilder() throws URISyntaxException {
        File certChainFile = new File(getClass().getResource(certChainFilePath).toURI());
        File privateKeyFile = new File(getClass().getResource(privateKeyFilePath).toURI());
        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(certChainFile,
                privateKeyFile);
//        if (trustCertCollectionFilePath != null) {
//            sslClientContextBuilder.trustManager(new File(trustCertCollectionFilePath));
//            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
//        }
        return GrpcSslContexts.configure(sslClientContextBuilder);
    }

    public NameSSLServer(int port) throws IOException, URISyntaxException {

        this.port = port;

        //构造服务器，添加我们实际的服务
        server = NettyServerBuilder.forPort(port)
                .addService(new NameSSLServiceImplBaseImpl())
                .sslContext(getSslContextBuilder().build())
                .build();
//                .start();

    }

    private void start() throws IOException {
        server.start();
        logger.info("Server has started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                NameSSLServer.this.stop();

            }
        });
    }

    private void stop() {

        if(server != null)
            server.shutdown();

    }

    //阻塞到应用停止
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        NameSSLServer nameSSLServer;

        if(args.length > 0){
            nameSSLServer = new NameSSLServer(Integer.parseInt(args[0]));
        }else{
            nameSSLServer = new NameSSLServer(DEFAULT_PORT);
        }

        nameSSLServer.start();

        nameSSLServer.blockUntilShutdown();

    }
}
