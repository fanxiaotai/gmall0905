package com.atguigu.gmall.manage.controller;

import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.manage.service.SpuService;
import com.atguigu.gmall.util.GmallUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/19 14:43
 * @Version 1.0
 */
@Controller
@CrossOrigin
public class SpuController {

    @Autowired
    SpuService spuService;

    @RequestMapping("spuSaleAttrListCheckBySku")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(@RequestParam("productId") String productId,@RequestParam("skuId") String skuId){
        return spuService.spuSaleAttrListCheckBySku(productId,skuId);
    }

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){

        return  spuService.spuImageList(spuId);
    }


    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){

        return  spuService.spuSaleAttrList(spuId);
    }


    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){

        return  spuService.spuList(catalog3Id);
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        return  spuService.saveSpuInfo(pmsProductInfo);
    }

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){

        String url = "";

        try {
            url = GmallUtil.uploadImage(multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  url;
    }


}
