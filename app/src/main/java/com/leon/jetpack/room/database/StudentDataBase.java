/**
 * FileName: StudentDataBase
 * Author: shiwenliang
 * Date: 2021/8/27 9:56
 * Description: 学生数据库
 */
package com.leon.jetpack.room.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.leon.jetpack.room.entity.FamilyAddress;
import com.leon.jetpack.room.entity.Student;
import com.leon.jetpack.room.dao.StudentDao;
import com.leon.jetpack.room.entity.Teacher;

/**
 * TODO 数据库
 * <p>
 * 数据库使用@Database注解，必须继承RoomDatabase ，而且是一个抽象类，
 * 数据库用于提供表操作Dao实例对象的获取，这里都是抽象方法，编译时，APT会生成其相关的实现类
 * <p>
 * 说明：
 * 1. entities 是指数据库中的表的类class,如果有多个表，则用逗号隔开即可
 * 2. version 代表数据库的版本号，用于数据库升级，迁移等使用
 * 3. exportSchema 需要指明是否到处到文件中去
 */
@Database(entities = {Student.class, FamilyAddress.class, Teacher.class}, version = 1, exportSchema = false)
public abstract class StudentDataBase extends RoomDatabase {

    private static StudentDataBase mInstance;

    public static synchronized StudentDataBase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (StudentDataBase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            StudentDataBase.class,
                            "StudentDataBse")
//                            .allowMainThreadQueries()//允许在主线程中允许
//                            .fallbackToDestructiveMigration()
//                            .addMigrations(MIGRATION_1_to_2)
                            .build();
                }
            }
        }
        return mInstance;
    }


    public abstract StudentDao getStudentDao();

    private static Migration MIGRATION_1_to_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //这里通过SQL语句来进行升级,下面是给表增加列信息的升级
            database.execSQL("ALTER TABLE Student ADD COLUMN sex CHAR NOT NULL DEFAULT 'm'");
        }
    };

    private static Migration MIGRATION_2_to_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //这里通过SQL语句来进行升级,下面是给表增加列信息的升级
            database.execSQL("ALTER TABLE Student ADD COLUMN sex CHAR NOT NULL DEFAULT 'm'");
        }
    };
}
