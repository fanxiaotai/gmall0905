package com.atguigu.gmall.manage.service.impl;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.bean.PmsBaseSaleAttr;
import com.atguigu.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.atguigu.gmall.manage.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;

/**
 * @Author: fanyitai
 * @Date: 2019/12/19 10:48
 * @Version 1.0
 */
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> arrtInfoList(String catalog3Id) {

        Example example = new Example(PmsBaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id",catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectByExample(example);
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            Example exampleValue = new Example(PmsBaseAttrValue.class);
            exampleValue.createCriteria().andEqualTo("attrId",pmsBaseAttrInfo.getId());
            List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.selectByExample(exampleValue);
            pmsBaseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }
        return pmsBaseAttrInfos;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        String id = pmsBaseAttrInfo.getId();
        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
        if (StringUtils.isBlank(id)){
            //id为空，执行保存操作
            try {
                //保存属性
                pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);

                //保存属性值

                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                    pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                    pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "保存失败";
            }
        }else {
            //id不为空，执行修改操作
            try {
                Example example = new Example(PmsBaseAttrInfo.class);
                example.createCriteria().andEqualTo("id",id);
                pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo,example);

                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                    Example example1 = new Example(PmsBaseAttrValue.class);
                    example1.createCriteria().andEqualTo("id",pmsBaseAttrValue.getId());
                    pmsBaseAttrValueMapper.updateByExample(pmsBaseAttrValue,example1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "修改失败";
            }

        }

        return "success";
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {

        Example example = new Example(PmsBaseAttrValue.class);
        example.createCriteria().andEqualTo("attrId",attrId);

        return pmsBaseAttrValueMapper.selectByExample(example);
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {

        return pmsBaseSaleAttrMapper.selectAll();
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet) {

        String valueIdStr = StringUtils.join(valueIdSet, ",");

        return pmsBaseAttrInfoMapper.selectAttrValueListByValueId(valueIdStr);
    }
}
