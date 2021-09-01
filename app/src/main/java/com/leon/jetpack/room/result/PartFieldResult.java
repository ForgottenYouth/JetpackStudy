/**
 * FileName: PartFieldResult
 * Author: shiwenliang
 * Date: 2021/8/31 15:49
 * Description:
 */
package com.leon.jetpack.room.result;

/**
 * TODO 部分字段查询的结果
 */
public class PartFieldResult {
    private String name;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PartFieldResult{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private int age;

}
