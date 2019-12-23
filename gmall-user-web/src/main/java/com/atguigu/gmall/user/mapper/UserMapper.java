package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.bean.UmsMember;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: fanyitai
 * @Date: 2019/12/18 20:49
 * @Version 1.0
 */
@Repository
public interface UserMapper extends Mapper<UmsMember> {
}
