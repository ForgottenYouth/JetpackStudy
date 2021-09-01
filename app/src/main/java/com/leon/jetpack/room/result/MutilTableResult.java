/**
 * FileName: RoomResult
 * Author: shiwenliang
 * Date: 2021/8/31 15:24
 * Description:
 */
package com.leon.jetpack.room.result;

public class MutilTableResult {
    private String name;
    private int age;

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

    public String getTeacherName() {
        return teacherName;
    }

    @Override
    public String toString() {
        return "RoomResult{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", teacherName='" + teacherName + '\'' +
                '}';
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    private String teacherName;
}
