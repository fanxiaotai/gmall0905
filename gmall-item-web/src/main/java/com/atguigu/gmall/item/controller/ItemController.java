package com.atguigu.gmall.item.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.client.ManageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: fanyitai
 * @Date: 2019/12/20 9:38
 * @Version 1.0
 */
@Controller
@CrossOrigin
public class ItemController {

    @Autowired
    private ManageClient manageClient;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap){

        PmsSkuInfo pmsSkuInfo = manageClient.getSkuById(skuId);

        //sku对象
        modelMap.put("skuInfo",pmsSkuInfo);

        //销售属性表
        List<PmsProductSaleAttr> pmsProductSaleAttrs = manageClient.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),skuId);
        modelMap.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        //查询当前sku的spu的其他集合的hash表
        Map<String,String> hashMap = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = manageClient.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";
            }

            hashMap.put(k,v);
        }
        String hashMapString = JSON.toJSONString(hashMap);
        modelMap.put("hashMapString",hashMapString);

        return "item";
    }
}
