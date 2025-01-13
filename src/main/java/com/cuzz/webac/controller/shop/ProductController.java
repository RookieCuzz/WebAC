package com.cuzz.webac.controller.shop;


import com.cuzz.webac.model.doo.ProductDO;
import com.cuzz.webac.servers.ProductService;
import com.cuzz.webac.utils.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@Slf4j
@RequestMapping("/product")
public class ProductController {



    @Resource
    ProductService productService;

    @CrossOrigin(origins = "http://127.1.0.1:8866")  // 允许来自指定域名的跨域请求
    @GetMapping("/getPageProduct")
    public  Result getAllProduct(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(defaultValue = "available") String productStatus){
        Logger.getLogger("11").log(Level.INFO,"!!!!!!!!!!!");
        this.productService.getPageProduct(pageNum,pageSize);
        return   Result.ok(this.productService.getPageProduct(pageNum,pageSize));
    }

    @PostMapping("/delProduct/{productId}")
    public Result delProduct(@PathVariable int productId){
        return Result.ok("成功");
        }

    @PostMapping("/modifyProduct")
    public Result modifyProduct(@RequestBody ProductDO productDO){
        return Result.ok("成功");
    }
}
