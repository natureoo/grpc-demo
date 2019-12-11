package demo.nature;

/**
 * @Author nature
 * @Mail 924943578@qq.com
 * ProtoInputStream 35
 */


import io.grpc.examples.nameserver.Ip;
import io.grpc.examples.nameserver.Name;
import io.grpc.examples.nameserver.NameServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NameServiceImplBaseImpl extends NameServiceGrpc.NameServiceImplBase {

    //记录名称内容的map，实际项目中应该放置在数据库
    private Map<String,String> map = new HashMap<String,String>();

    private Logger logger = Logger.getLogger(NameServiceImplBaseImpl.class.getName());

    //构造方法中加入一些条目
    public NameServiceImplBaseImpl() {

        map.put("Sunny","125.216.242.51");

        map.put("David","117.226.178.139");

    }

    @Override
    public void getIpByName(Name request, StreamObserver<Ip> responseObserver) {
        logger.log(Level.INFO,"requst is coming. args=" + request.getName());

        //只有私有构造方法，所以只能通过builder来构造
        Ip ip = Ip.newBuilder().setIp(getName(request.getName())).build();

        //用于返回结果
        responseObserver.onNext(ip);

        //用于告诉客户端调用已结束
        responseObserver.onCompleted();

    }

    //实际查找工作在这里完成
    public String getName(String name){
        String ip = map.get(name);
        if(ip == null){
            return "0.0.0.0";
        }
        return ip;
    }
}
