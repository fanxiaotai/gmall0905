package com.atguigu.gmall.manage.controller;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.bean.PmsBaseSaleAttr;
import com.atguigu.gmall.manage.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @Author: fanyitai
 * @Date: 2019/12/19 10:45
 * @Version 1.0
 */
@Controller
@CrossOrigin
public class AttrController {

    @Autowired
    private AttrService attrService;

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){

        return attrService.baseSaleAttrList();
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){

        return attrService.getAttrValueList(attrId);
    }

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){

        return attrService.saveAttrInfo(pmsBaseAttrInfo);
    }

    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> arrtInfoList(String catalog3Id){

        return attrService.arrtInfoList(catalog3Id);
    }

    @RequestMapping("getAttrValueListByValueId")
    @ResponseBody
    List<PmsBaseAttrInfo> getAttrValueListByValueId(@RequestParam("valueIdSet") Set<String> valueIdSet){
        return attrService.getAttrValueListByValueId(valueIdSet);
    }
}
