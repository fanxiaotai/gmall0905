package com.atguigu.gmall.manage.service;

import com.atguigu.gmall.bean.PmsSkuInfo;

import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/19 20:29
 * @Version 1.0
 */
public interface SkuService {
    String saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);

    List<PmsSkuInfo> getAllSku();
}
