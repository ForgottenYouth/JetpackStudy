/**
 * FileName: StudentDao
 * Author: shiwenliang
 * Date: 2021/8/27 10:01
 * Description: 学生表的Dao层
 */
package com.leon.jetpack.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.leon.jetpack.room.entity.Student;
import com.leon.jetpack.room.result.MutilTableResult;
import com.leon.jetpack.room.result.PartFieldResult;

import java.util.List;

/**
 * TODO 表操作的Dao层
 * 说明：
 * dao层使用@Dao注解表示，且是一个接口
 * 1.
 */
@Dao
public interface StudentDao {

    //插入
    @Insert
    void insert(Student student);

    //删除
    @Delete
    void delete(Student student);

    //修改
    @Update
    void update(Student student);

    //查询
    @Query("select * from Student")
    List<Student> getAll();

    /**
     * TODO 条件查询:
     * 1.方法的参数name 就是在SQL语句like ：冒号后面的参数name
     * 2.多个参数查询时使用and来连接参数，每个参数使用like+冒号+参数名
     * 3.集合查询使用 in (: 参数名)
     */
    @Query("select * from Student where name like :name and age like :age")
    Student queryStudent(String name,int age);

    //集合查询
    @Query("select * from Student where name in (:userNames)")
    List<Student> queryStudents(List<String> userNames);

    /**
     * TODO 部分字段查询
     */
    @Query("select name ,age From Student")
    List<PartFieldResult> queryFields();

    /**
     * TODO 多表查询查询
     * 1、多表查询，如果表中的有字段是重复时，需要使用as来指定对应关系
     * 2、多表查询的结果需要保存在一个新的实体中
     */

    @Query("select Student.name as name ,Student.age as age ,Teacher.name as teacherName from student,teacher")
    List<MutilTableResult> queryMutilTable();
}
