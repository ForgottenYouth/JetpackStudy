package com.leon.jetpack.room;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.leon.jetpack.R;
import com.leon.jetpack.room.dao.StudentDao;
import com.leon.jetpack.room.database.StudentDataBase;
import com.leon.jetpack.room.entity.Student;
import com.leon.jetpack.room.result.MutilTableResult;
import com.leon.jetpack.room.result.PartFieldResult;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO room的使用
 * 1.room 的使用是采用建造者模式来使用的，通过建造者模式就可以生成对应数据库的实例对象（由于前面数据库定义是抽象类，那么这个实例对象的类就是通过apt编译期生成的）
 * 2.三个参数：全局的上下文环境，数据库类型class ，数据库名字，
 * 3.数据库的操作是在一个线程中执行的，不能放到主线程做
 */
public class RoomMainActivity extends AppCompatActivity {

    private static final String TAG = RoomMainActivity.class.getSimpleName();
    StudentDao studentDB;
    RoomThread roomThread;
    private static int type = -1;

    private TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_main);

        show = findViewById(R.id.show);

        studentDB = StudentDataBase.getInstance(RoomMainActivity.this).getStudentDao();
        roomThread = new RoomThread();
        roomThread.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            show.setText(show.getText() + msg.obj.toString());
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.queryall:
                type = 1;
                roomThread = new RoomThread();
                roomThread.start();
                break;
            case R.id.querysigle:
                type = 2;
                roomThread = new RoomThread();
                roomThread.start();
                break;
            case R.id.querymutil:
                type = 3;
                roomThread = new RoomThread();
                roomThread.start();
                break;
            case R.id.querymutiltable:
                type = 4;
                roomThread = new RoomThread();
                roomThread.start();
                break;
            case R.id.queryfileds:
                type = 5;
                roomThread = new RoomThread();
                roomThread.start();
                break;
            case R.id.inserts:
                type = 0;
                roomThread = new RoomThread();
                roomThread.start();
                break;
            default:
                break;
        }
    }

    class RoomThread extends Thread {
        @Override
        public void run() {
            switch (type) {
                case 0:
                    insertDataBase();
                    break;
                case 1:
                    queryAll();
                    break;
                case 2:
                    querySingle();
                    break;
                case 3:
                    queryMutil();
                    break;
                case 4:
                    queryMutilTable();
                    break;
                case 5:
                    queryFields();
                    break;
                case 6:
                    insertDataBase();
                    break;
                default:
                    break;
            }
        }
    }

    private void queryFields() {
        List<PartFieldResult> partFieldResults = studentDB.queryFields();
        Log.e(TAG, "字段查询:\n" + partFieldResults.toString());

        Message message = new Message();
        message.obj = "\n\n\n字段查询:\n" + partFieldResults.toString();
        mHandler.sendMessage(message);
    }

    private void queryMutilTable() {
        List<MutilTableResult> queryMutilTable = studentDB.queryMutilTable();
        Log.e(TAG, "多表查询:\n" + queryMutilTable.toString());

        Message message = new Message();
        message.obj = "\n\n\n多表查询:\n" + queryMutilTable.toString();
        mHandler.sendMessage(message);
    }

    void queryMutil() {
        List<String> temp = new ArrayList<String>();
        temp.add("张三");
        temp.add("王五");

        List<Student> students = studentDB.queryStudents(temp);
        Log.e(TAG, "多条件查询:\n" + students.toString());

        Message message = new Message();
        message.obj = "\n\n\n多条件查询:\n" + students.toString();
        mHandler.sendMessage(message);
    }

    void querySingle() {
        Student student = studentDB.queryStudent("马六",15);
        Log.e(TAG, "单条件查询:\n" + student.toString());
        Message message = new Message();
        message.obj = "\n\n\n单条件查询:\n" + student.toString();
        mHandler.sendMessage(message);
    }

    void queryAll() {
        List<Student> all = studentDB.getAll();
        Log.e(TAG, "查询全部:\n" + all);
        Message message = new Message();
        message.obj = "查询全部:\n" + all;
        mHandler.sendMessage(message);
    }

    void insertDataBase() {
        studentDB.insert(new Student("张三", 12));
        studentDB.insert(new Student("李四", 13));
        studentDB.insert(new Student("王五", 14));
        studentDB.insert(new Student("马六", 15));
    }
}