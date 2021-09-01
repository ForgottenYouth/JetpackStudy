/**
 * FileName: Teacher
 * Author: shiwenliang
 * Date: 2021/8/30 17:09
 * Description:
 */
package com.leon.jetpack.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Teacher {

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    @PrimaryKey(autoGenerate = true)
    private int teacherId;

    @ColumnInfo(name = "name")
    private String name;

    public Teacher(String name) {
        this.name = name;

    }


    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                ", name='" + name + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

}
