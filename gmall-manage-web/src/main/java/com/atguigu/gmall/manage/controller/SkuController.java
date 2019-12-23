package com.atguigu.gmall.manage.controller;

import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.manage.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/19 20:10
 * @Version 1.0
 */
@CrossOrigin
@Controller
public class SkuController {

    @Autowired
    SkuService skuService;

    @RequestMapping("getSkuSaleAttrValueListBySpu")
    @ResponseBody
    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(@RequestParam("productId") String productId){
        return skuService.getSkuSaleAttrValueListBySpu(productId);
    }


    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){

        //将spuId封装给productId
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        skuService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }

    @RequestMapping("getSkuById")
    @ResponseBody
    public PmsSkuInfo getSkuById(@RequestParam("skuId") String skuId){

        return skuService.getSkuById(skuId);
    }

    @RequestMapping("getAllSku")
    @ResponseBody
    public List<PmsSkuInfo> getAllSku(){
        return skuService.getAllSku();
    }
}
