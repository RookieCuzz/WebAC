package com.cuzz.webac.model.game.postbox;


import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import lombok.Data;

@Entity(discriminator = "AbstractItem")
@Data
public abstract class AbstractItem {


    private String base64Item;

    private String itemDisplayName;

    private  Integer amount;
    public AbstractItem(){

    }
}
