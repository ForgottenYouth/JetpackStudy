# Jetpack学习之----ROOM



## 介绍



[官方学习文档](https://developer.android.google.cn/jetpack/androidx/releases/room?hl=zh_cn)

Jetpack 的ROOM就是在android支持的SQLite的基础上进行了一个抽象的封装，这样方便用户更好的利用SQLite的强大功能，进行更加强健的数据库访问机制。

------

ROOM 是以注解的方式在编译期帮助我们生成很多代码，这样就可以将我们的重心放在业务逻辑的处理上了。



笔者对ROOM的理解：

**通过注解的方式将数据库操作、表操作等比较繁琐的工作交给注解处理器工作，我们只需要创建数据模型和对数据库的操作即可。**



------

**ROOM的三大组件**

> **1) DataBase: 他所标记的类就是一个数据库类，**
>
> **2) Entity：表示数据库中的表**
>
> **3) Dao：表示一个可以对数据库进行操作（增删改查）的接口规范（不包括对数据库的创建）**



## 基本使用

### 1、引入依赖库

```groovy
    def room_version = "2.3.0"

    compile "android.arch.persistence.room:runtime:$room_version"
    kapt "android.arch.persistence.room:compiler:$room_version"

    //如果您已迁移到androidx
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
```



### 2、数据库

数据库的操作不允许允许在主线程中，但是可以通过配置使其在主线程中允许（allowMainThreadQueries）

> 1. **数据库使用@Database注解，必须继承RoomDatabase ，而且是一个抽象类，数据库用于提供表操作Dao实例对象的获取，这里都是抽象方法，编译时，APT会生成其相关的实现类**
> 2.  **entities 是指数据库中的表的类class,如果有多个表，则用逗号隔开即可**
> 3. **version 代表数据库的版本号，用于数据库升级，迁移等使用**
> 4. **exportSchema 需要指明是否导出到文件中去**

```java
@Database(entities = {Student.class, FamilyAddress.class}, version = 1, exportSchema = true)
public abstract class StudentDataBase extends RoomDatabase {

    private static StudentDataBase mInstance;

    //单例
 	public static synchronized StudentDataBase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (StudentDataBase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),//应用上下文
                            StudentDataBase.class,//数据库类型
                            "StudentDataBse")//数据库的名字
                            .allowMainThreadQueries()//允许在主线程中允许
                            .build();
                }
            }
        }
        return mInstance;
    }

    //获取dao的实例对象
    public abstract StudentDao getStudentDao();
}
```



#### 2.1、数据库强制升级（不建议）

在创建数据库实例对象的build()之前，调用fallbackToDestructiveMigration(),但是这种方式不建议使用，因为这种强制升级会导致数据库的结构发生变化，并且会导致数据库中的数据全部丢失。

```java
  public static synchronized StudentDataBase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (StudentDataBase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            StudentDataBase.class,
                            "StudentDataBse")
                            .fallbackToDestructiveMigration()//强制升级
                            .build();
                }
            }
        }
        return mInstance;
    }
```



#### 2.2、MIGRATION保留数据升级

1）创建MIGRATION变量（需要依次传入两个版本的版本号）

在给表增加或者修改列信息时，同时也需要将@Entity对应的实体进行修改

```java
    private static Migration MIGRATION_1_to_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
         	//这里通过SQL语句来进行升级,下面是给表增加列信息的升级
            database.execSQL("ALTER TABLE Student ADD COLUMN sex CHAR NOT NULL DEFAULT 'm'");
        }
    };
```

2) 添加MIGRATION变量

```java
 public static synchronized StudentDataBase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (StudentDataBase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            StudentDataBase.class,
                            "StudentDataBse")
                            .addMigrations(MIGRATION_1_to_2)
                            .build();
                }
            }
        }
        return mInstance;
    }
```

3）修改原数据库版本信息

```java
@Database(entities = {Student.class, FamilyAddress.class}, version = 2, exportSchema = true)//这里需要将version的值由1 修改到 2
```



#### 2.3、 数据库迁移

数据库迁移的思路：

创建新的数据库，并将表结构及其数据复制到新的数据库中去，删除原数据库，重命名新数据库为原数据库名，这样就达到数据库迁移的目标了。





### 3、表结构（实体）

> 1. **@Entity**表示数据库的表结构;
> 2. **@PrimaryKey** 表示是一个主键，参数autoGenerate 为true时，表示主键是自动生成的，这里不能给默认值;
> 3. **@ColumnInfo** 表示表中的一个属性列信息，参数name 就是标的列的名称，与定义的变量名一致;
> 4. **@Embedded** 对象注解
> 5. **@Ignore** 忽略注解，用在方法或字段上；

```java
@Entity
public class Student {

    @PrimaryKey(autoGenerate = true)
    private int id;

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
```



### 4、Dao层

Dao层其实就是定义一套操作数据库的标准（增删改查等），这样编译器编译生成Dao层的实例类，对数据库的操作就可以直接使用Dao层对象操作了。

#### 4.1、 使用@Dao注解来表示Dao层，且是一个接口

```java
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
    }
```



#### 4.2、 条件查询：

> 1）方法的参数name 就是在SQL语句like ：冒号后面的参数name
>
> 2）多个参数查询时使用and来连接参数，每个参数使用like+冒号+参数名
>
> 3）集合查询使用 in (: 参数名)

```java
 @Query("select * from Student where name like :name and age like :age")
    Student queryStudent(String name,int age);

    //集合查询
    @Query("select * from Student where name in (:userNames)")
    List<Student> queryStudents(List<String> userNames);
```



#### 4.3、字段查询

查询的结果需要重新定义一个数据结构来存放。

```java
    @Query("select name ,age From Student")
    List<PartFieldResult> queryFields();
```



#### 4.4、多表查询

> 1） 多表查询，如果表中的有字段是重复时，需要使用as来指定对应关系
>
> 2） 多表查询的结果需要保存在一个新的实体中

```java
    @Query("select Student.name as name ,Student.age as age ,Teacher.name as teacherName from student,teacher")
    List<MutilTableResult> queryMutilTable();
```



### 5、使用：

#### 5.1、获取数据库Dao实例对象

```java
 StudentDao studentDB;
 RoomThread roomThread;

 studentDB = StudentDataBase.getInstance(RoomMainActivity.this).getStudentDao();

 roomThread = new RoomThread();
```



#### 5.2、创建子线程

```java
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
```



#### 5.3、调用Dao的方法操作数据库

```java
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
```





## 原理分析



> **在编译期通过注解处理器生成对应的Dao层和抽象数据库的实例类，**
>
> **当开始调用Room的build()时，通过反射技术反射数据库实现类的实例对象，同时创建对应的数据库表；**
>
> **当通过数据库实例对象获取Dao的实例对象时，就会进行数据库表结构的初始化工作，**
>
> **至此数据库及表结构就都已经创建好了，接下来通过Dao实例对象调用对应的接口标准进行相应的数据库操作。**



### 1、RoomDataBase类的build()

工作：新建创建数据库的工厂，准备数据库需要的相关配置信息等。

```java
 public T build() {
           
     //下面都是创建对应的创建数据库的工厂
            SupportSQLiteOpenHelper.Factory factory;
            if (mFactory == null) {
                factory = new FrameworkSQLiteOpenHelperFactory();
            } else {
                factory = mFactory;
            }

            if (mAutoCloseTimeout > 0) {
                factory = new AutoClosingRoomOpenHelperFactory(factory, autoCloser);
            }
            if (mCopyFromAssetPath != null
                    || mCopyFromFile != null
                    || mCopyFromInputStream != null) {
                factory = new SQLiteCopyOpenHelperFactory(mCopyFromAssetPath, mCopyFromFile,
                        mCopyFromInputStream, factory);
            }

            if (mQueryCallback != null) {
                factory = new QueryInterceptorOpenHelperFactory(factory, mQueryCallback,
                        mQueryCallbackExecutor);
            }
     
     	
//配置创建数据库对应的参数
            DatabaseConfiguration configuration =
                    new DatabaseConfiguration(
                            mContext,
                            mName,
                            factory,
                            mMigrationContainer,
                            mCallbacks,
                            mAllowMainThreadQueries,
                            mJournalMode.resolve(mContext),
                            mQueryExecutor,
                            mTransactionExecutor,
                            mMultiInstanceInvalidation,
                            mRequireMigration,
                            mAllowDestructiveMigrationOnDowngrade,
                            mMigrationsNotRequiredFrom,
                            mCopyFromAssetPath,
                            mCopyFromFile,
                            mCopyFromInputStream,
                            mPrepackagedDatabaseCallback,
                            mTypeConverters);
     	//下面是反射获取生成的数据库实例
            T db = Room.getGeneratedImplementation(mDatabaseClass, DB_IMPL_SUFFIX);
     	
     //对数据库进行配置
            db.init(configuration);
            return db;
        }
    }
```



### 2、Room类的getGeneratedImplementation()

通过反射技术反射出来数据库实现类的实例对象

```java
static <T, C> T getGeneratedImplementation(Class<C> klass, String suffix) {
        final String fullPackage = klass.getPackage().getName();
        String name = klass.getCanonicalName();
        final String postPackageName = fullPackage.isEmpty()
                ? name
                : name.substring(fullPackage.length() + 1);
        final String implName = postPackageName.replace('.', '_') + suffix;
        //noinspection TryWithIdenticalCatches
        try {
			//反射获取抽象数据库类的实例对象
            final String fullClassName = fullPackage.isEmpty()
                    ? implName
                    : fullPackage + "." + implName;
            @SuppressWarnings("unchecked")
            final Class<T> aClass = (Class<T>) Class.forName(
                    fullClassName, true, klass.getClassLoader());
            return aClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("cannot find implementation for "
                    + klass.getCanonicalName() + ". " + implName + " does not exist");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access the constructor"
                    + klass.getCanonicalName());
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to create an instance of "
                    + klass.getCanonicalName());
        }
    }
```





### 3、Dao层的实现类

实现数据库的操作：表结构初始化、数据库操作SQL调用执行等。

```java
public final class StudentDao_Impl implements StudentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Student> __insertionAdapterOfStudent;

  private final EntityDeletionOrUpdateAdapter<Student> __deletionAdapterOfStudent;

  private final EntityDeletionOrUpdateAdapter<Student> __updateAdapterOfStudent;

  public StudentDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStudent = new EntityInsertionAdapter<Student>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Student` (`id`,`teachId`,`name`,`age`,`sex`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Student value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getTeachId());
        if (value.getName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getName());
        }
        stmt.bindLong(4, value.getAge());
        stmt.bindLong(5, value.getSex());
      }
    };
  }

  @Override
  public void insert(final Student student) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfStudent.insert(student);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Student student) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfStudent.handle(student);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Student student) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfStudent.handle(student);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }
//其他的方法省略
}
```



### 4、数据库抽象类的实现类

1）创建数据库，包括数据库中的表

2）创建Dao层的实例对象

```java
public final class StudentDataBase_Impl extends StudentDataBase {
  private volatile StudentDao _studentDao;

  @Override
  public StudentDao getStudentDao() {
    if (_studentDao != null) {
      return _studentDao;
    } else {
      synchronized(this) {
        if(_studentDao == null) {
          _studentDao = new StudentDao_Impl(this);
        }
        return _studentDao;
      }
    }
  }
}
```





## 总结

### 1、技术点：

反射+APT+单例模式+工厂模式



### 2、ROOM 坑：

在使用Room时，会出现如下的错误：

```ABAP
java.lang.RuntimeException: cannot find implementation for com.xx.xx.db.xxDatabase. xxDatabase_Impl does not exist
```

解决方法：

```groovy
 	def room_version = "2.3.0"

    compile "android.arch.persistence.room:runtime:$room_version"
    kapt "android.arch.persistence.room:compiler:$room_version"

    //如果您已迁移到androidx
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
```

切记：一定要**<u>在所有使用到room的module中</u>**都添加一下“  kapt "androidx.room:room-compiler:$room_version"



### 3、遗留问题

1）关于外键注解的使用？？？

2）@Embedded 注解的作用？？？？
