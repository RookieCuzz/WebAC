package com.cuzz.webac.dao;

import com.cuzz.webac.dao.api.ShipDao;
import com.cuzz.webac.model.doo.GamePlayerDayCountDo;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.model.game.postbox.AdminItem;
import com.cuzz.webac.model.game.postbox.Package;
import com.cuzz.webac.model.game.postbox.PostBox;
import com.cuzz.webac.service.OrderWechatService;
import com.mongodb.DBRef;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperator;
import dev.morphia.query.experimental.updates.UpdateOperators;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class MongoDaoImp implements ShipDao {


    @Value("${mongodb.url}")
    String url;
    private Datastore datastore;
    private MongoClient mongoClient;

    @Resource
    private OrderWechatService orderWechatService;

    @Override
    @PostConstruct
    public void connect() {
        try {
            System.out.println(this.url);
            mongoClient = MongoClients.create(this.url);
            datastore = Morphia.createDatastore(mongoClient,"RookiePostBox");
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

//    @Override
//    public Boolean ShipProductToPlayerPostBox(ProductDO productDO) {
//
//
//
//        this.datastore.save(gamePlayerDayCountDo);
//        return null;
//    }
    @Override
    public boolean shipProductToPlayerPostBox(OrderDO orderDO) {
        if (orderDO==null){
            return false;
        }
        String orderNumber = orderDO.getOrderNumber();
        String consigneeUuid = orderDO.getConsigneeUuid();
        //打包
        List<AdminItem> allProductByOrderNumber = orderWechatService.getAllProductByOrderNumber(orderNumber);
        allProductByOrderNumber.forEach(single->{
            System.out.println("订单包含"+single.getStoreID());
        });
        Package aPackage = new Package();
        aPackage.setMessage(orderNumber+"订单包含的商品");
        aPackage.setOwnerUUID(consigneeUuid);
        aPackage.setOwner(orderDO.getConsigneeName());
        aPackage.setItemContent(allProductByOrderNumber);
        Package savedPackage = this.savePackageToDB(aPackage);
        System.out.println(savedPackage.getId());
        //保存
        DBRef dbRef = new DBRef("somePackage",savedPackage.getId());
        if (datastore != null) {
            // postbox保存package的引用？ package要先保存到db？
            UpdateOperator pullOperator = UpdateOperators.addToSet("packages", dbRef);
            this.datastore.find(PostBox.class).filter(Filters.eq("_id",consigneeUuid)).update(pullOperator).execute();
            return true;
        } else {
            System.err.println("Datastore is not initialized.");
            return false;
        }
    }
    @Override
    public Boolean updateDayInfo(GamePlayerDayCountDo gamePlayerDayCountDo) {

        this.datastore.save(gamePlayerDayCountDo);

        return null;
    }

    @Override
    public Package savePackageToDB(Package packageX){
        if (datastore != null) {
            return this.datastore.save(packageX);
        } else {
            System.err.println("Datastore is not initialized.");
            return null;
        }
    }
}
