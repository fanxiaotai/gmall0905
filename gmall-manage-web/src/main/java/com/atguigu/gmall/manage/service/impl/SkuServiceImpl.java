package com.atguigu.gmall.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.manage.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/19 20:29
 * @Version 1.0
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        try {
            //插入skuInfo
            pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
            String skuId = pmsSkuInfo.getId();

            //插入平台属性关联
            List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
            if (skuAttrValueList!=null){
                for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                    pmsSkuAttrValue.setSkuId(skuId);
                    pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
                }
            }

            //插入销售属性关联
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
            if (skuSaleAttrValueList!=null){
                for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                    pmsSkuSaleAttrValue.setSkuId(skuId);
                    pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
                }
            }


            //插入图片集合
            List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
            if (skuImageList!=null){
                for (PmsSkuImage pmsSkuImage : skuImageList) {
                    pmsSkuImage.setSkuId(skuId);
                    pmsSkuImage.setProductImgId(pmsSkuImage.getSpuImgId());
                    pmsSkuImageMapper.insertSelective(pmsSkuImage);
                }
            }

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "保存失败";
        }


    }

    public PmsSkuInfo getSkuByIdDB(String skuId) {
        Example example = new Example(PmsSkuInfo.class);
        example.createCriteria().andEqualTo("id",skuId);
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectOneByExample(example);

        //图片
        Example exampleImage = new Example(PmsSkuImage.class);
        exampleImage.createCriteria().andEqualTo("skuId",skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.selectByExample(exampleImage);
        pmsSkuInfo.setSkuImageList(pmsSkuImages);

        //平台属性
        Example exampleAttr = new Example(PmsSkuAttrValue.class);
        exampleAttr.createCriteria().andEqualTo("skuId",skuId);
        List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.selectByExample(exampleAttr);
        pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);

        //销售属性
        Example exampleSaleAttr = new Example(PmsSkuSaleAttrValue.class);
        exampleSaleAttr.createCriteria().andEqualTo("skuId",skuId);
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = pmsSkuSaleAttrValueMapper.selectByExample(exampleSaleAttr);
        pmsSkuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValues);

        return pmsSkuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId) {

        PmsSkuInfo pmsSkuInfo;
        //链接缓存
        Jedis jedis = redisUtil.getJedis();
        try {
            pmsSkuInfo = new PmsSkuInfo();

            //查询缓存
            String skuKey = "sku:" +skuId + ":info";
            String skuJson = jedis.get(skuKey);
            if (StringUtils.isNotBlank(skuJson)){
                pmsSkuInfo = JSON.parseObject(skuJson,PmsSkuInfo.class);
                System.out.println("查询缓存");
            }else {
                //如果缓存中没有，查询mysql

                //设置分布式锁
                String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10000);
                if (StringUtils.isNotBlank(OK)&&OK.equals("OK")){
                    //设置成功，有权在10秒内的过期时间内访问数据库
                    pmsSkuInfo = getSkuByIdDB(skuId);

                    //mysql查询结果存入redis
                    if (pmsSkuInfo!=null){
                        jedis.set(skuKey,JSON.toJSONString(pmsSkuInfo));
                    }else{
                        //数据库不存在该sku
                        //为了防止缓存穿透，将null或者字符串设置给redis
                        jedis.setex(skuKey,60*3, JSON.toJSONString(""));
                        System.out.println("查询数据库");
                    }

                    //访问mysql后，释放分布式锁
                    jedis.del("sku:" + skuId + ":lock");
                }else{
                    //设置失败
                    Thread.sleep(3000);
                    return getSkuById(skuId);
                }


            }

            return pmsSkuInfo;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("查询PmsSkuInfo出错");
        } finally {
            jedis.close();
        }
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {

        return  pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuInfoId = pmsSkuInfo.getId();

            Example example = new Example(PmsSkuAttrValue.class);
            example.createCriteria().andEqualTo("skuId",skuInfoId);
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.selectByExample(example);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }

        return pmsSkuInfos;
    }
}
