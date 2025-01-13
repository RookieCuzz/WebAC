package com.cuzz.webac.dao;

import com.cuzz.webac.dao.api.GameInfoDao;
import com.cuzz.webac.model.doo.GamePlayerDayCountDo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class GameInfoDaImp implements GameInfoDao {


    @Value("${mangodb.url}")
    String url;
    private Datastore datastore;
    private MongoClient mongoClient;

    @Override
    @PostConstruct
    public void connect() {

        try {
            System.out.println(this.url);
            mongoClient = MongoClients.create(this.url);
            datastore = Morphia.createDatastore(mongoClient,"GameServerInfo");
            log.info("MongoDB connected and datastore initialized.");
        }catch (Exception e){
            log.error("Error connecting to MongoDB: " + e.getMessage());
        }

    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        } else {
            System.err.println("MongoClient is not initialized.");
        }
    }

    public GamePlayerDayCountDo getPlayerDayCountByDay(String dayTime){
        //this.datastore.find("1")
        return null;

    }

    @Override
    public Boolean updateDayInfo(GamePlayerDayCountDo gamePlayerDayCountDo) {

        this.datastore.save(gamePlayerDayCountDo);

        return null;
    }
}
