package com.atguigu.gmall.bean;

import javax.persistence.*;
import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/18 16:54
 * @Version 1.0
 */
public class PmsBaseCatalog3 {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String name;
    @Column
    private String catalog2Id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog1Id() {
        return catalog2Id;
    }

    public void setCatalog1Id(String catalog1Id) {
        this.catalog2Id = catalog1Id;
    }

}
