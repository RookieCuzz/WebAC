package com.cuzz.webac.model.game.postbox;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity(value = "postboxtable", discriminator = "PostBox")
@NoArgsConstructor  // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class PostBox {


    @Builder.Default
    @Reference
    private List<Package> packages=new ArrayList<>();
    @Id
    private String ownerUUID;
}
