/**
 * FileName: Student
 * Author: shiwenliang
 * Date: 2021/8/27 9:34
 * Description: 学生表
 */
package com.leon.jetpack.room.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * TODO 表
 * 使用@Entity表示数据库的表结构
 *
 * @PrimaryKey 表示是一个主键，参数autoGenerate 为true时，表示主键是自动生成的，这里不能给默认值
 * @ColumnInfo 表示表中的一个属性列信息，参数name 就是标的列的名称，与定义的变量名一致
 */
@Entity//(foreignKeys = {@ForeignKey(entity = Teacher.class, parentColumns = "teacherId", childColumns = "teachId")})
public class Student {

    @PrimaryKey(autoGenerate = true)
    private int id;


    public int getTeachId() {
        return teachId;
    }

    public Student(int teachId, String name, int age, char sex) {
        this.teachId = teachId;
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public void setTeachId(int teachId) {
        this.teachId = teachId;
    }

    @ColumnInfo(name = "teachId")
    private int teachId;


    @Ignore
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "age")
    private int age;

    @Ignore
    public Student(String name, int age, char sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    @ColumnInfo(name = "sex")
    private char sex;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
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
}
