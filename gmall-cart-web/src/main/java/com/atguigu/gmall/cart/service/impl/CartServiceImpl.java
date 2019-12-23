package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.cart.mapper.OmsCartItemMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @Author: fanyitai
 * @Date: 2019/12/22 22:04
 * @Version 1.0
 */

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public OmsCartItem ifCartBxistByUser(String memberId, String skuId) {

        List<OmsCartItem> omsCartItems = cartList(memberId, skuId);
        if (omsCartItems!=null&&omsCartItems.size()!=0){
            return omsCartItems.get(0);
        }else {
 /*           Example example = new Example(OmsCartItem.class);
            example.createCriteria().andEqualTo("productSkuId",skuId).andEqualTo("memberId",memberId);
            OmsCartItem omsCartItem = omsCartItemMapper.selectOneByExample(example);*/
            return null;
        }
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        if (StringUtils.isNotBlank(omsCartItem.getMemberId())){
            omsCartItemMapper.insertSelective(omsCartItem);
            List<OmsCartItem> omsCartItems = cartList(omsCartItem.getMemberId(),null);
            omsCartItems.add(omsCartItem);
            //同步到缓存
            redis(omsCartItems);
        }
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemDB) {

        omsCartItemMapper.updateByPrimaryKeySelective(omsCartItemDB);
        List<OmsCartItem> omsCartItems = cartList(omsCartItemDB.getMemberId(),null);
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getProductSkuId().equals(omsCartItemDB.getProductSkuId())){
                omsCartItem = omsCartItemDB;
            }
        }
        //同步到redis
        redis(omsCartItems);

    }

    @Override
    public void flushCartCache(String memberId) {

        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(example);
        if (omsCartItems==null){
            return;
        }
        //同步到缓存
        redis(omsCartItems);
    }

    @Override
    public List<OmsCartItem> cartList(String memberId,String skuId) {

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();

        try {
            if (StringUtils.isBlank(skuId)){
                //查询缓存
                List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
                for (String hval : hvals) {
                    OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                    omsCartItems.add(omsCartItem);
                }
            }else {
                String hget = jedis.hget("user:" + memberId + ":cart", skuId);
                OmsCartItem omsCartItem = JSON.parseObject(hget, OmsCartItem.class);
                omsCartItems.add(omsCartItem);
            }
            /*if (omsCartItems.size()!=0){
                //设置redis分布式锁
                String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10000);
                if (StringUtils.isNotBlank(OK)&&OK.equals("OK")){
                    //设置成功，有权在10秒内的过期时间内访问数据库
                    flushCartCache(memberId);

                    //访问mysql后，释放分布式锁
                    jedis.del("sku:" + skuId + ":lock");
                }else {
                    Thread.sleep(3000);
                    return cartList(memberId, skuId);
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
        return omsCartItems;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {

        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId())
                .andEqualTo("productSkuId",omsCartItem.getProductSkuId());

        omsCartItemMapper.updateByExampleSelective(omsCartItem,example);

        List<OmsCartItem> omsCartItems = cartList(omsCartItem.getMemberId(),null);
        for (OmsCartItem cartItem : omsCartItems) {
            if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                cartItem.setMemberId(omsCartItem.getMemberId());
                cartItem.setProductSkuId(omsCartItem.getProductSkuId());
                cartItem.setIsChecked(omsCartItem.getIsChecked());
            }
        }
        //同步到redis
        redis(omsCartItems);
    }

    public void redis(List<OmsCartItem> omsCartItems){

        String memberId = "";
        Jedis jedis = redisUtil.getJedis();
        try {
            Map<String,String> map = new HashMap<>();
            for (OmsCartItem cartItem : omsCartItems) {
                if (memberId.equals("")){
                    memberId = cartItem.getMemberId();
                }
                cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
                String productSkuId = cartItem.getProductSkuId();
                map.put(productSkuId,JSON.toJSONString(cartItem));
            }
            jedis.del("user:"+memberId+":cart");
            jedis.hmset("user:"+memberId+":cart",map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
