package com.atguigu.gmall.manage.service.impl;

import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.PmsProductImageMapper;
import com.atguigu.gmall.manage.mapper.PmsProductInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.atguigu.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.atguigu.gmall.manage.service.SpuService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/19 14:46
 * @Version 1.0
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {

        Example example = new Example(PmsProductInfo.class);
        example.createCriteria().andEqualTo("catalog3Id",catalog3Id);
        return pmsProductInfoMapper.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {

        try {
            pmsProductInfoMapper.insert(pmsProductInfo);
            String id = pmsProductInfo.getId();

            List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
            if (pmsProductSaleAttrList!=null){
                for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {

                    pmsProductSaleAttr.setProductId(id);
                    pmsProductSaleAttrMapper.insert(pmsProductSaleAttr);

                    List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
                    if (pmsProductSaleAttrValueList!=null){
                        for (PmsProductSaleAttrValue pmsProductSaleAttrValue : pmsProductSaleAttrValueList) {
                            pmsProductSaleAttrValue.setProductId(id);
                            pmsProductSaleAttrValueMapper.insert(pmsProductSaleAttrValue);
                        }
                    }
                }
            }

            List<PmsProductImage> pmsProductImageList = pmsProductInfo.getSpuImageList();
            if (pmsProductImageList!=null){
                for (PmsProductImage pmsProductImage : pmsProductImageList) {
                    pmsProductImage.setProductId(id);
                    pmsProductImageMapper.insert(pmsProductImage);
                }
            }

            return "保存成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "保存失败";
        }
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        Example example = new Example(PmsProductSaleAttr.class);
        example.createCriteria().andEqualTo("productId",spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectByExample(example);
        if (pmsProductSaleAttrs!=null){
            for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {

                Example exampleValue = new Example(PmsProductSaleAttrValue.class);
                exampleValue.createCriteria()
                        .andEqualTo("productId",pmsProductSaleAttr.getProductId())
                        .andEqualTo("saleAttrId",pmsProductSaleAttr.getSaleAttrId());
                List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.selectByExample(exampleValue);
                pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
            }
        }
        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {

        Example example = new Example(PmsProductImage.class);
        example.createCriteria().andEqualTo("productId",spuId);

        return pmsProductImageMapper.selectByExample(example);
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId) {

        List<PmsProductSaleAttr> pmsProductSaleAttrs =
                pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId,skuId);

        return pmsProductSaleAttrs;
    }
}
