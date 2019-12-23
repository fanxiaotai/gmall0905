package com.atguigu.gmall.client;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsSkuInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @Author: fanyitai
 * @Date: 2019/12/20 10:11
 * @Version 1.0
 */
@FeignClient(value = "manage-web")
public interface ManageClient {

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo);

    @RequestMapping("getSkuById")
    @ResponseBody
    PmsSkuInfo getSkuById(@RequestParam("skuId") String skuId);

    @RequestMapping("spuSaleAttrListCheckBySku")
    @ResponseBody
    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(@RequestParam("productId") String productId,@RequestParam("skuId") String skuId);

    @RequestMapping("getSkuSaleAttrValueListBySpu")
    @ResponseBody
    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(@RequestParam("productId") String productId);

    @RequestMapping("getAllSku")
    @ResponseBody
    List<PmsSkuInfo> getAllSku();

    @RequestMapping("getAttrValueListByValueId")
    @ResponseBody
    List<PmsBaseAttrInfo> getAttrValueListByValueId(@RequestParam("valueIdSet") Set<String> valueIdSet);
}
