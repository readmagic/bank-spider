package oop.fun;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import oop.fun.service.SpiderService;
import org.jdom.JDOMException;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException, JDOMException {
        System.out.println("采集程序开始启动");
        System.out.println("mongodbURI:"+args[0]);
        MongoClient mongoClient = createMongoClient(args[0]);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("bank");
        SpiderService  spiderService= new SpiderService(mongoDatabase);
        spiderService.run();
        mongoClient.close();
        System.out.println("采集程序开始结束");
    }

    private static MongoClient createMongoClient(String uri) {
        return new MongoClient(new MongoClientURI(uri));
    }

}
