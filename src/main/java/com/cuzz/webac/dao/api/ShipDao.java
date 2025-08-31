package com.cuzz.webac.dao.api;


import com.cuzz.webac.model.doo.GamePlayerDayCountDo;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.model.doo.ProductDO;
import com.cuzz.webac.model.game.postbox.Package;

public interface ShipDao {

    public void connect();

    public void close();

    //    @Override
    //    public Boolean ShipProductToPlayerPostBox(ProductDO productDO) {
    //
    //
    //
    //        this.datastore.save(gamePlayerDayCountDo);
    //        return null;
    //    }
    boolean shipProductToPlayerPostBox(OrderDO orderDO);

    public Boolean updateDayInfo(GamePlayerDayCountDo gamePlayerDayCountDo);
    public GamePlayerDayCountDo getPlayerDayCountByDay(String dayTime);



    public Package savePackageToDB(Package packageX);

}

