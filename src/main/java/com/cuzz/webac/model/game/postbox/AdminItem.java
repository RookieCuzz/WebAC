package com.cuzz.webac.model.game.postbox;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Indexed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity(value="abstractItems", discriminator="adminItem")
@Data
@AllArgsConstructor // 生成全参构造函数
@NoArgsConstructor
public class AdminItem extends AbstractItem {

    //所属的商品编号
    private String storeID;

    //若为贵重物品(武器 装备)
    @Indexed
    private String uniqueKey;

}