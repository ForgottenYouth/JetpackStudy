/**
 * FileName: FamilyAddress
 * Author: shiwenliang
 * Date: 2021/8/27 10:48
 * Description: 家庭住址表
 */
package com.leon.jetpack.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FamilyAddress {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    public FamilyAddress(String province, String city) {
        this.province = province;
        this.city = city;
    }

    @ColumnInfo(name = "province")
    private String province;

    @ColumnInfo(name = "ciy")
    private String city;

    @Override
    public String toString() {
        return "FamilyAddress{" +
                "id=" + id +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
