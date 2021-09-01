# JetPack学习之--DataBinding



## 一、介绍



[官方学习文档](https://developer.android.google.cn/topic/libraries/data-binding)

DataBinding 是一个支持库，可以使用声明性格式将布局中的界面组件绑定到应用程序的数据源。



简言之：

​			**可以直接将数据源和XML布局文件中的View视图进行关联。**

当数据源是被观察者的话，那么在数据源发生变化的时候，在布局文件绑定的视图也会跟着变化。



**特点**：

> 1. 简化界面控制器中的界面框架的调用（也就是不需要使用**findViewById**来创建view的对象然后进行赋值了）
> 2. 防止空指针异常
> 3. 界面控制器的代码维护起来更简单
> 4. 提高性能



## 二、基本使用



### 1、添加DataBinding支持

```groovy
android{
   dataBinding{
        enabled=true
    }
}
```



### 2、定义数据源

> 继承自BaseObservable ，这样数据源就是一个被观察者
>
> 需要和视图绑定的Field ,使用@Bindable注解，
>
> 修改属性值的方法中，使用notifyPropertyChanged发出通知（该方法是BaseObservable的方法）

```java
//数据源是一个被观察者
public class DataBean extends BaseObservable {

    //此处是表示该属性支持绑定
    @Bindable
    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
        //在修改属性值时，需要发出属性修改的通知
        notifyPropertyChanged(BR.resultDesc);
    }

    @Bindable
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
        notifyPropertyChanged(BR.resultCode);
    }

    private String resultDesc;
    private int resultCode;
}
```



### 3、布局文件添加Layout根布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>

    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".databinding.DataBindingMainActivity">

    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>

```



### 4、绑定数据源

> **在data标签中  添加<variable>标签，可以指定变量的名称，类型就是我们的数据源的类型，这样就将数据源类型和布局绑定成功了；**
>
> **在data标签中，使用<import> 标签 ，可以引用一些我们需要使用的工具类**

```xml
    <data>
        <import type="android.view.View"/>
        <import type="android.text.TextUtils"/>

        <variable
            name="dataSource"
            type="com.leon.jetpack.databinding.DataBean" />

    </data>
```



### 5、数据源与视图属性的绑定

> 绑定数据源，使用@{}的格式来进行关联绑定
>
> 在花括号内部需要使用引号的地方，使用``来代替

```xml
 <TextView
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     app:layout_constraintTop_toTopOf="parent"
     android:text="@{dataSource.resultDesc}"/>
```



### 6、获取dataBinding实例

每一个支持DataBinding的布局文件，添加了Layout根布局后，都会编译生成一个”**布局文件名+Binding.java**“文件，例如下面的布局文件名为：activity_data_binding_main.xml   ，生成的对应的java文件是：ActivityDataBindingMainBinding.java

```java
public class DataBindingMainActivity extends AppCompatActivity {

   ActivityDataBindingMainBinding dataBindingMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBindingMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding_main);

        DataBean dataBean=new DataBean();
        dataBean.setResultDesc("helloworld");
        dataBindingMainBinding.setDataSource(dataBean);
    }
}
```



## 三、原理分析



> **原理分析的思路：**
>
> **1）通过APT技术同使用了databinding的布局文件中的view添加tag ，用于后续使用的时候直接通过tag来查询对应的视图**
>
> **2）通过APT技术将使用了@Bindable注解的数据Field，编译成BR中的常量**
>
> **由于数据源是一个被观察者，在创建dataBinding对象时，给数据源中使用@Bindable注解的字段添加一个监听器，当数据发生变化时，通过该监听器来回调到ViewDataBinding的实现类ActivityDataBindingMainBindingImpl中去；然后再次执行rebind()方法进行UI视图值的更新.**





编译生成的文件目录:

![dataBinding编译生成的文件目录](E:\简书文章\JetPack\databinding\dataBinding编译生成的文件目录)



DataBinding对象创建的类关系调用图：

![](E:\简书文章\JetPack\databinding\jetpack_databinding.png)



### 1、setContentView过程

#### 1) DataBindingUtil类的setContentView()方法

```java
    public static <T extends ViewDataBinding> T setContentView(@NonNull Activity activity,
            int layoutId) {
        return setContentView(activity, layoutId, sDefaultComponent);//此处的第三个参数默认为NULL
    }

    public static <T extends ViewDataBinding> T setContentView(@NonNull Activity activity,
            int layoutId, @Nullable DataBindingComponent bindingComponent) {
        activity.setContentView(layoutId);
        View decorView = activity.getWindow().getDecorView();
        ViewGroup contentView = (ViewGroup) decorView.findViewById(android.R.id.content);
        /*
        * 第一个参数为null 
        * 第二个参数为上面获取到contentView(关于这一点可以参考XML布局的加载过程即容易理解)
        * 第三个参数为0     
        * 第四个参数：布局文件的id
        */
        return bindToAddedViews(bindingComponent, contentView, 0, layoutId);
    }


	//下面的方法我们可以先不去关注处理逻辑，直接从return 语句开始
    private static <T extends ViewDataBinding> T bindToAddedViews(DataBindingComponent component,
            ViewGroup parent, int startChildren, int layoutId) {
        final int endChildren = parent.getChildCount();
        final int childrenAdded = endChildren - startChildren;
        if (childrenAdded == 1) {
            final View childView = parent.getChildAt(endChildren - 1);
            
            //todo 关注点 
            return bind(component, childView, layoutId);
        } else {
            final View[] children = new View[childrenAdded];
            for (int i = 0; i < childrenAdded; i++) {
                children[i] = parent.getChildAt(i + startChildren);
            }
             //todo 关注点 
            return bind(component, children, layoutId);
        }
    }

   static <T extends ViewDataBinding> T bind(DataBindingComponent bindingComponent, View[] roots,
            int layoutId) {
       //这里的sMapper就是我们第二步的Mapper容器，
        return (T) sMapper.getDataBinder(bindingComponent, roots, layoutId);
    }
```



### 2、Mapper容器的创建过程

#### 1) DataBindingUtil.java类

> 说明：
>
> 1） com.leon.jetpack.DataBinderMapperImpl 该类是DataBinderMapper抽象类的实现类
>
> 2）MergedDataBinderMapper 该类也是DataBinderMapper抽象类的实现类,他是对DataBinderMapper子类对象的一个包装类
>
> 3）DataBinderMapperImpl 类又是MergedDataBinderMapper 的子类，他的实现其实就是将com.leon.jetpack.DataBinderMapperImpl类的实例包装后保存到MergedDataBinderMapper的List中去。

```java
public class DataBindingUtil {
    private static DataBinderMapper sMapper = new DataBinderMapperImpl();
    }


//下面这个类是编译生成的MergedDataBinderMapper的实现类
public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new com.leon.jetpack.DataBinderMapperImpl());
  }
}
```



#### 2）com.leon.jetpack.DataBinderMapperImpl类的实现

```java
public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYDATABINDINGMAIN = 1;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(1);

  static {
      //此处就是将我们自己的布局文件ID保存在一个数组中
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.leon.jetpack.R.layout.activity_data_binding_main, LAYOUT_ACTIVITYDATABINDINGMAIN);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYDATABINDINGMAIN: {
          if ("layout/activity_data_binding_main_0".equals(tag)) {
              //这里会创建一个布局文件绑定实现类的实例对象
            return new ActivityDataBindingMainBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_data_binding_main is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }


}
```



### 3、sMapper容器的getDataBinder

由于DataBinderMapperImpl 类继承MergedDataBinderMapper，并没有重写getDataBinder方法，那么就可以进入MergedDataBinderMapper来看看getDataBinder()方法的实现.

> 说明： 
>
> 1、该方法是一个重载方法，唯一的区别是第二个参数不同（一个是单个的view ，一个是view数组）
>
> 2、该方法是通过遍历mMappers容器内的mapper ，来让每一个mapper调用各自的getDataBinder()方法

```java
 @Override
    public ViewDataBinding getDataBinder(DataBindingComponent bindingComponent, View view,
            int layoutId) {
        for(DataBinderMapper mapper : mMappers) {
            //循环遍历容器内的mapper
            ViewDataBinding result = mapper.getDataBinder(bindingComponent, view, layoutId);
            if (result != null) {
                return result;
            }
        }
        if (loadFeatures()) {
            return getDataBinder(bindingComponent, view, layoutId);
        }
        return null;
    }

    @Override
    public ViewDataBinding getDataBinder(DataBindingComponent bindingComponent, View[] view,
            int layoutId) {
        //循环遍历容器内的mapper
        for(DataBinderMapper mapper : mMappers) {
            ViewDataBinding result = mapper.getDataBinder(bindingComponent, view, layoutId);
            if (result != null) {
                return result;
            }
        }
        if (loadFeatures()) {
            return getDataBinder(bindingComponent, view, layoutId);
        }
        return null;
    }
```



进入com.leon.jetpack.DataBinderMapperImpl类的getDataBinder()方法

```java
public class DataBinderMapperImpl extends DataBinderMapper {
 @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYDATABINDINGMAIN: {
          if ("layout/activity_data_binding_main_0".equals(tag)) {
              //这里就会得到我们需要的binding对象
            return new ActivityDataBindingMainBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_data_binding_main is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }
}
```



### 4、binding对象的创建过程

ActivityDataBindingMainBindingImpl.java文件

```java
//构造方法
public ActivityDataBindingMainBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
    	//重点关注一下mapBindings()方法
        this(bindingComponent, root, mapBindings(bindingComponent, root, 1, sIncludes, sViewsWithIds));
    }
    
```



ViewDataBinding.java 该类的mapBindings（）方法 是通过从布局文件的根布局开始遍历每一个视图，将所有include ，tag , id标注的视图全部添加到一个视图数组中去，这些视图就是binding的视图，就可以在Activity等界面控制器中通过binding实例对象来直接使用。

```java
  protected static Object[] mapBindings(DataBindingComponent bindingComponent, View root,
            int numBindings, IncludedLayouts includes, SparseIntArray viewsWithIds) {
        Object[] bindings = new Object[numBindings];
        mapBindings(bindingComponent, root, bindings, includes, viewsWithIds, true);
        return bindings;
    }


private static void mapBindings(DataBindingComponent bindingComponent, View view,
            Object[] bindings, IncludedLayouts includes, SparseIntArray viewsWithIds,
            boolean isRoot) {
        final int indexInIncludes;
        final ViewDataBinding existingBinding = getBinding(view);
        if (existingBinding != null) {
            return;
        }
        Object objTag = view.getTag();
        final String tag = (objTag instanceof String) ? (String) objTag : null;
        boolean isBound = false;
        if (isRoot && tag != null && tag.startsWith("layout")) {
            final int underscoreIndex = tag.lastIndexOf('_');
            if (underscoreIndex > 0 && isNumeric(tag, underscoreIndex + 1)) {
                final int index = parseTagInt(tag, underscoreIndex + 1);
                if (bindings[index] == null) {
                    bindings[index] = view;
                }
                indexInIncludes = includes == null ? -1 : index;
                isBound = true;
            } else {
                indexInIncludes = -1;
            }
        } else if (tag != null && tag.startsWith(BINDING_TAG_PREFIX)) {
            int tagIndex = parseTagInt(tag, BINDING_NUMBER_START);
            if (bindings[tagIndex] == null) {
                bindings[tagIndex] = view;
            }
            isBound = true;
            indexInIncludes = includes == null ? -1 : tagIndex;
        } else {
            // Not a bound view
            indexInIncludes = -1;
        }
        if (!isBound) {
            final int id = view.getId();
            if (id > 0) {
                int index;
                if (viewsWithIds != null && (index = viewsWithIds.get(id, -1)) >= 0 &&
                        bindings[index] == null) {
                    bindings[index] = view;
                }
            }
        }

        if (view instanceof  ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;
            final int count = viewGroup.getChildCount();
            int minInclude = 0;
            for (int i = 0; i < count; i++) {
                final View child = viewGroup.getChildAt(i);
                boolean isInclude = false;
                if (indexInIncludes >= 0 && child.getTag() instanceof String) {
                    String childTag = (String) child.getTag();
                    if (childTag.endsWith("_0") &&
                            childTag.startsWith("layout") && childTag.indexOf('/') > 0) {
                        // This *could* be an include. Test against the expected includes.
                        int includeIndex = findIncludeIndex(childTag, minInclude,
                                includes, indexInIncludes);
                        if (includeIndex >= 0) {
                            isInclude = true;
                            minInclude = includeIndex + 1;
                            final int index = includes.indexes[indexInIncludes][includeIndex];
                            final int layoutId = includes.layoutIds[indexInIncludes][includeIndex];
                            int lastMatchingIndex = findLastMatching(viewGroup, i);
                            if (lastMatchingIndex == i) {
                                bindings[index] = DataBindingUtil.bind(bindingComponent, child,
                                        layoutId);
                            } else {
                                final int includeCount =  lastMatchingIndex - i + 1;
                                final View[] included = new View[includeCount];
                                for (int j = 0; j < includeCount; j++) {
                                    included[j] = viewGroup.getChildAt(i + j);
                                }
                                bindings[index] = DataBindingUtil.bind(bindingComponent, included,
                                        layoutId);
                                i += includeCount - 1;
                            }
                        }
                    }
                }
                if (!isInclude) {
                    mapBindings(bindingComponent, child, bindings, includes, viewsWithIds, false);
                }
            }
        }
    }
```



### 5、binding.setXXX()或setVariable() 的过程

当在使用binding对象设置数据源或者修改数据的某一个Field的时，会完成如下的两个过程：

从dataBindingMainBinding.setDataSource(dataBean);开始，找到ActivityDataBindingMainBinding的实现类：ActivityDataBindingMainBindingImpl，进入方法setDataSource()

```java
    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.dataSource == variableId) {
            setDataSource((com.leon.jetpack.databinding.DataBean) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }


//参数是传递进来的databean   
public void setDataSource(@Nullable com.leon.jetpack.databinding.DataBean DataSource) {
        updateRegistration(0, DataSource);
        this.mDataSource = DataSource;//将传递进来的数据保存下来
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.dataSource);
        super.requestRebind();
    }
```



#### **1） 给Field绑定监听的过程**

> 该过程的主要思路就是根据用户设置需要修改的Field来到监听器数组中去找，
>
> **如果找了该Field的监听器，**并且被观察者也相同，则直接进行后面的通知更新流程；
>
> **如果找到该Field的监听器的被观察者不是当前的被观察者，**先将该Field的监听器注销掉，并将其被观察者修改为null ，然后重新创建该Field的监听器，并将当前的被观察者设为新建的监听器的Target ，然后进行后续的通知更新流程；
>
> **如果没有找到，**就直接创建该Field的监听器，并设置Target为当前的被观察者（observable）,然后执行通知更新；



下面来从源码角度分析该过程

##### 1） updateRegistration(0, DataSource);更新监听

```java

    /**
     * @hide
     */
    protected boolean updateRegistration(int localFieldId, Observable observable) {
        return updateRegistration(localFieldId, observable, CREATE_PROPERTY_LISTENER);
    }

//进入updateRegistration()方法  直到ViewDataBinding.java文件的下面的方法中

    private static final CreateWeakListener CREATE_PROPERTY_LISTENER = new CreateWeakListener() {
        @Override
        public WeakListener create(ViewDataBinding viewDataBinding, int localFieldId) {
            //这里创建针对Field的监听,但是这里并不是真正的创建监听，而是一个创建监听器的方法对象
            return new WeakPropertyListener(viewDataBinding, localFieldId).getListener();
        }
    };


    private boolean updateRegistration(int localFieldId, Object observable,
            CreateWeakListener listenerCreator) {
        if (observable == null) {
            return unregisterFrom(localFieldId);
        }
        //从数组中找是否已经有了该Field的监听
        WeakListener listener = mLocalFieldObservers[localFieldId];
        if (listener == null) {
            //如果没有找到该Field的监听者，就把新创建的该Field的监听器注册进去
            registerTo(localFieldId, observable, listenerCreator);
            return true;
        }
        
        //如果已经存在该Field的监听者并且被观察者是同一个，就直接返回
        if (listener.getTarget() == observable) {
            return false;//nothing to do, same object
        }
        
        //如果找到的该Field的监听器与被观察者不同，则先注销掉该Field的监听，将先创建的监听器进行注册绑定
        unregisterFrom(localFieldId);
        registerTo(localFieldId, observable, listenerCreator);
        return true;
    }



```



##### 2）注销监听

```java
protected boolean unregisterFrom(int localFieldId) {
        WeakListener listener = mLocalFieldObservers[localFieldId];
        if (listener != null) {
            return listener.unregister();
        }
        return false;
    }

 public boolean unregister() {
            boolean unregistered = false;
            if (mTarget != null) {
                mObservable.removeListener(mTarget);
                unregistered = true;
            }
            mTarget = null;
            return unregistered;
        }
```



##### 3）注册监听

```java
 protected void registerTo(int localFieldId, Object observable,
            CreateWeakListener listenerCreator) {
        if (observable == null) {
            return;
        }
     //先从Field数组中查找localFieldId的监听
        WeakListener listener = mLocalFieldObservers[localFieldId];
        if (listener == null) {
            //没找到就创建一个监听
            listener = listenerCreator.create(this, localFieldId);
            //将创建好的监听添加mLocalFieldObservers数组中
            mLocalFieldObservers[localFieldId] = listener;
            if (mLifecycleOwner != null) {
                listener.setLifecycleOwner(mLifecycleOwner);
            }
        }
     	//如果找到了，就给监听设置Target ，这个过程和注销的时候相反，注册是将监听器的target设置为null
        listener.setTarget(observable);
    }
```



#### **2）通知更新的流程**

单Field更新：

```java
public class DataBean extends BaseObservable {

    @Bindable
    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
        notifyPropertyChanged(BR.resultDesc);
    }

    @Bindable
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
        //这里被观察者要发出变更的通知
        notifyPropertyChanged(BR.resultCode);
    }

    private String resultDesc;
    private int resultCode;

}
```



整个数据源更新：

```java
 public void setDataSource(@Nullable com.leon.jetpack.databinding.DataBean DataSource) {
        updateRegistration(0, DataSource);
        this.mDataSource = DataSource;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        //通知更新的流程
        notifyPropertyChanged(BR.dataSource);
        super.requestRebind();
    }
```



##### 1）mCallbacks对象溯源

BaseObservable.java 的addOnPropertyChangedCallback，NofifyPropertyChanged()

> mCallbacks变量中存放的是一个实现了OnPropertyChangedCallback接口的callback对象；
>
> mCallbacks变量是以List数据结构来存放对象的；

```java
public class BaseObservable implements Observable {
    private transient PropertyChangeRegistry mCallbacks;

    public BaseObservable() {
    }

    //这里mCallBacks里面存放的是一个实现了OnPropertyChangedCallback接口的对象
    //mCallBacks里面是用一个List数据结构来存放对象的
    @Override
    public void addOnPropertyChangedCallback(@NonNull OnPropertyChangedCallback callback) {
        synchronized (this) {
            if (mCallbacks == null) {
                mCallbacks = new PropertyChangeRegistry();
            }
        }
        mCallbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(@NonNull OnPropertyChangedCallback callback) {
        synchronized (this) {
            if (mCallbacks == null) {
                return;
            }
        }
        mCallbacks.remove(callback);
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    public void notifyChange() {
        synchronized (this) {
            if (mCallbacks == null) {
                return;
            }
        }
        mCallbacks.notifyCallbacks(this, 0, null);
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with {@link Bindable} to generate a field in
     * <code>BR</code> to be used as <code>fieldId</code>.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */

public void notifyPropertyChanged(int fieldId) {
        synchronized (this) {
            if (mCallbacks == null) {
                return;
            }
        }
      //这里是通过下面的代码来同通知修改的，那么就需要知道mCallBacks的由来
        mCallbacks.notifyCallbacks(this, fieldId, null);
    }
} 
```



##### 2）ViewDataBinding的静态内部类WeakPropertyListener



> 该静态内部类实现了接口 Observable.OnPropertyChangedCallback
>
> 该静态内部类是用来监听Bindings中的variables的变化，**很重要，很重要**
>
> 这个WeakPropertyListener 又是通过一个方法对象在给Filed添加监听的时候创建的。并且在调用addListener的时候，将当前监听器添加到被观察者observable的mCallBack中去

```java
//ViewDataBinding.java
private static class WeakPropertyListener extends Observable.OnPropertyChangedCallback
            implements ObservableReference<Observable> {
        final WeakListener<Observable> mListener;

        public WeakPropertyListener(ViewDataBinding binder, int localFieldId) {
            mListener = new WeakListener<Observable>(binder, localFieldId, this);
        }

        @Override
        public WeakListener<Observable> getListener() {
            return mListener;
        }

        @Override
        public void addListener(Observable target) {
            //这里将当前的监听器添加到mCallBack中
            target.addOnPropertyChangedCallback(this);
        }

        @Override
        public void removeListener(Observable target) {
            target.removeOnPropertyChangedCallback(this);
        }

        @Override
        public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        }

    	
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            ViewDataBinding binder = mListener.getBinder();
            if (binder == null) {
                return;
            }
            Observable obj = mListener.getTarget();
            if (obj != sender) {
                return; // notification from the wrong object?
            }
            binder.handleFieldChange(mListener.mLocalFieldId, sender, propertyId);
        }
    }
```



##### 3）监听回调

当mCallbacks.notifyCallbacks(this, fieldId, null);这个代码执行时，其实就是调用的监听者WeakPropertyListener的onPropertyChanged()回调方法。

```java
public class PropertyChangeRegistry extends
        CallbackRegistry<Observable.OnPropertyChangedCallback, Observable, Void> {

    private static final CallbackRegistry.NotifierCallback<Observable.OnPropertyChangedCallback, Observable, Void> NOTIFIER_CALLBACK = new CallbackRegistry.NotifierCallback<Observable.OnPropertyChangedCallback, Observable, Void>() {
        @Override
        public void onNotifyCallback(Observable.OnPropertyChangedCallback callback, Observable sender,
                int arg, Void notUsed) {
            //这里实际上就是调用前面的Listener的回调
            callback.onPropertyChanged(sender, arg);
        }
    };

    public PropertyChangeRegistry() {
        super(NOTIFIER_CALLBACK);
    }

    /**
     * Notifies registered callbacks that a specific property has changed.
     *
     * @param observable The Observable that has changed.
     * @param propertyId The BR id of the property that has changed or BR._all if the entire
     *                   Observable has changed.
     */
    public void notifyChange(@NonNull Observable observable, int propertyId) {
        notifyCallbacks(observable, propertyId, null);
    }
}


//上一步就回到WeakPropertyListener的onPropertyChanged()方法中
private static class WeakPropertyListener extends Observable.OnPropertyChangedCallback
            implements ObservableReference<Observable> {
        final WeakListener<Observable> mListener;

        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            ViewDataBinding binder = mListener.getBinder();
            if (binder == null) {
                return;
            }
            Observable obj = mListener.getTarget();
            if (obj != sender) {
                return; // notification from the wrong object?
            }
            binder.handleFieldChange(mListener.mLocalFieldId, sender, propertyId);
        }
    }

    private void handleFieldChange(int mLocalFieldId, Object object, int fieldId) {
        if (mInLiveDataRegisterObserver) {
            // We're in LiveData registration, which always results in a field change
            // that we can ignore. The value will be read immediately after anyway, so
            // there is no need to be dirty.
            return;
        }
        
        //onFieldChange是一个抽象方法，
        boolean result = onFieldChange(mLocalFieldId, object, fieldId);
        if (result) {
            requestRebind();
        }
    }

//ViewDataBinding的实现类ActivityDataBindingMainBindingImpl.java中
  @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeDataSource((com.leon.jetpack.databinding.DataBean) object, fieldId);
        }
        return false;
    }
    private boolean onChangeDataSource(com.leon.jetpack.databinding.DataBean DataSource, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        else if (fieldId == BR.resultDesc) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }
```





### 6、布局文件解析



编译时，会将布局文件中使用了@{}绑定数据的view视图，添加一个Tag标签，并生成到一个新的文件：布局文件名-layout.xml,

例如：（app/build/intermediates/data_binding_layout_info+type_merge/debug/out/activity_data_binding_main-layout.xml）

```xml
<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<Layout directory="layout" filePath="app\src\main\res\layout\activity_data_binding_main.xml"
    isBindingData="true" isMerge="false"
    layout="activity_data_binding_main" modulePackage="com.leon.jetpack"
    rootNodeType="androidx.constraintlayout.widget.ConstraintLayout">

    <Variables name="dataSource" declared="true" type="com.leon.jetpack.databinding.DataBean">

        <location endLine="9" endOffset="58" startLine="7" startOffset="8" />
    </Variables>

    <Targets>
        <Target tag="layout/activity_data_binding_main_0"
            view="androidx.constraintlayout.widget.ConstraintLayout">
            <Expressions />

            <location endLine="35" endOffset="55" startLine="13" startOffset="4" />
        </Target>

        <!--这里就是将使用了dataBinding的视图节点添加了一个tag标签-->
        <Target tag="binding_1" view="TextView">
            <Expressions>
                <Expression attribute="android:text" text="dataSource.resultDesc">
                    <Location endLine="30" endOffset="50" startLine="30" startOffset="12" />
                    <TwoWay>false</TwoWay>
                    <ValueLocation endLine="30" endOffset="48" startLine="30" startOffset="28" />
                </Expression>
            </Expressions>
            <location endLine="33" endOffset="63" startLine="26" startOffset="8" />
        </Target>

        <Target id="@+id/change" view="Button">
            <Expressions />
            <location endLine="24" endOffset="55" startLine="18" startOffset="8" />
        </Target>
    </Targets>
</Layout>
```



在DataBindingUtil.setContentView()会一直进入到ViewDataBinding的静态方法mapBindings(),该方法主要是找到标签了tag的视图，

```java
 private static void mapBindings(DataBindingComponent bindingComponent, View view,
            Object[] bindings, IncludedLayouts includes, SparseIntArray viewsWithIds,
            boolean isRoot) {
        final int indexInIncludes;
        final ViewDataBinding existingBinding = getBinding(view);
        if (existingBinding != null) {
            return;
        }
        Object objTag = view.getTag();
        final String tag = (objTag instanceof String) ? (String) objTag : null;
        boolean isBound = false;
        if (isRoot && tag != null && tag.startsWith("layout")) {
            final int underscoreIndex = tag.lastIndexOf('_');
            if (underscoreIndex > 0 && isNumeric(tag, underscoreIndex + 1)) {
                final int index = parseTagInt(tag, underscoreIndex + 1);
                if (bindings[index] == null) {
                    bindings[index] = view;
                }
                indexInIncludes = includes == null ? -1 : index;
                isBound = true;
            } else {
                indexInIncludes = -1;
            }
        } else if (tag != null && tag.startsWith(BINDING_TAG_PREFIX)) {
            int tagIndex = parseTagInt(tag, BINDING_NUMBER_START);
            if (bindings[tagIndex] == null) {
                bindings[tagIndex] = view;
            }
            isBound = true;
            indexInIncludes = includes == null ? -1 : tagIndex;
        } else {
            // Not a bound view
            indexInIncludes = -1;
        }
        if (!isBound) {
            final int id = view.getId();
            if (id > 0) {
                int index;
                if (viewsWithIds != null && (index = viewsWithIds.get(id, -1)) >= 0 &&
                        bindings[index] == null) {
                    bindings[index] = view;
                }
            }
        }

        if (view instanceof  ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;
            final int count = viewGroup.getChildCount();
            int minInclude = 0;
            for (int i = 0; i < count; i++) {
                final View child = viewGroup.getChildAt(i);
                boolean isInclude = false;
                if (indexInIncludes >= 0 && child.getTag() instanceof String) {
                    String childTag = (String) child.getTag();
                    if (childTag.endsWith("_0") &&
                            childTag.startsWith("layout") && childTag.indexOf('/') > 0) {
                        // This *could* be an include. Test against the expected includes.
                        int includeIndex = findIncludeIndex(childTag, minInclude,
                                includes, indexInIncludes);
                        if (includeIndex >= 0) {
                            isInclude = true;
                            minInclude = includeIndex + 1;
                            final int index = includes.indexes[indexInIncludes][includeIndex];
                            final int layoutId = includes.layoutIds[indexInIncludes][includeIndex];
                            int lastMatchingIndex = findLastMatching(viewGroup, i);
                            if (lastMatchingIndex == i) {
                                bindings[index] = DataBindingUtil.bind(bindingComponent, child,
                                        layoutId);
                            } else {
                                final int includeCount =  lastMatchingIndex - i + 1;
                                final View[] included = new View[includeCount];
                                for (int j = 0; j < includeCount; j++) {
                                    included[j] = viewGroup.getChildAt(i + j);
                                }
                                bindings[index] = DataBindingUtil.bind(bindingComponent, included,
                                        layoutId);
                                i += includeCount - 1;
                            }
                        }
                    }
                }
                if (!isInclude) {
                    mapBindings(bindingComponent, child, bindings, includes, viewsWithIds, false);
                }
            }
        }
    }
```

在更新通知流程的最后一步会调用super.requestRebind();

```java
    public void setDataSource(@Nullable com.leon.jetpack.databinding.DataBean DataSource) {
        updateRegistration(0, DataSource);
        this.mDataSource = DataSource;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.dataSource);
        super.requestRebind();
    }
```



进入ViewDataBinding.java的requestRebind(),看到下面会有mUIThreadHandler会执行一个mRebindRunnable子线程来执行更新

```java
 protected void requestRebind() {
        if (mContainingBinding != null) {
            mContainingBinding.requestRebind();
        } else {
            final LifecycleOwner owner = this.mLifecycleOwner;
            if (owner != null) {
                Lifecycle.State state = owner.getLifecycle().getCurrentState();
                if (!state.isAtLeast(Lifecycle.State.STARTED)) {
                    return; // wait until lifecycle owner is started
                }
            }
            synchronized (this) {
                if (mPendingRebind) {
                    return;
                }
                mPendingRebind = true;
            }
            if (USE_CHOREOGRAPHER) {
                mChoreographer.postFrameCallback(mFrameCallback);
            } else {
                //这里执行一个子线程mRebindRunnable
                mUIThreadHandler.post(mRebindRunnable);
            }
        }
    }

 /**
     * Runnable executed on animation heartbeat to rebind the dirty Views.
     */
    private final Runnable mRebindRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                mPendingRebind = false;
            }
            processReferenceQueue();

            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                // Nested so that we don't get a lint warning in IntelliJ
                if (!mRoot.isAttachedToWindow()) {
                    // Don't execute the pending bindings until the View
                    // is attached again.
                    mRoot.removeOnAttachStateChangeListener(ROOT_REATTACHED_LISTENER);
                    mRoot.addOnAttachStateChangeListener(ROOT_REATTACHED_LISTENER);
                    return;
                }
            }
            executePendingBindings();
        }
    };



 private void executeBindingsInternal() {
        if (mIsExecutingPendingBindings) {
            requestRebind();
            return;
        }
        if (!hasPendingBindings()) {
            return;
        }
        mIsExecutingPendingBindings = true;
        mRebindHalted = false;
        if (mRebindCallbacks != null) {
            mRebindCallbacks.notifyCallbacks(this, REBIND, null);

            // The onRebindListeners will change mPendingHalted
            if (mRebindHalted) {
                mRebindCallbacks.notifyCallbacks(this, HALTED, null);
            }
        }
        if (!mRebindHalted) {
            executeBindings();//此方法是一个抽象方法，进入实现类中看具体的实现
            if (mRebindCallbacks != null) {
                mRebindCallbacks.notifyCallbacks(this, REBOUND, null);
            }
        }
        mIsExecutingPendingBindings = false;
    }
```

进入

```java
 protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        com.leon.jetpack.databinding.DataBean dataSource = mDataSource;
        java.lang.String dataSourceResultDesc = null;

        if ((dirtyFlags & 0x7L) != 0) {



                if (dataSource != null) {
                    // read dataSource.resultDesc
                    dataSourceResultDesc = dataSource.getResultDesc();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x7L) != 0) {
            // api target 1
			//这里还是调用的setText来完成更新的
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView1, dataSourceResultDesc);
        }
    }
```



### 7、DataBinding的适配事项

ViewDataBinding.java文件中对对DataBinding库支持的SDK版本做了限制（如下的静态代码块），

```java
 static {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {//需要大于Android 19
            ROOT_REATTACHED_LISTENER = null;
        } else {
            ROOT_REATTACHED_LISTENER = new OnAttachStateChangeListener() {
                @TargetApi(VERSION_CODES.KITKAT)
                @Override
                public void onViewAttachedToWindow(View v) {
                    // execute the pending bindings.
                    final ViewDataBinding binding = getBinding(v);
                    binding.mRebindRunnable.run();
                    v.removeOnAttachStateChangeListener(this);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                }
            };
        }
    }
```





## 四、BindingAdapter

```java
@Target(ElementType.METHOD)
public @interface BindingAdapter {

    /**
     * @return The attributes associated with this binding adapter.
     */
    String[] value();

    /**
     * Whether every attribute must be assigned a binding expression or if some
     * can be absent. When this is false, the BindingAdapter will be called
     * when at least one associated attribute has a binding expression. The attributes
     * for which there was no binding expression (even a normal XML value) will
     * cause the associated parameter receive the Java default value. Care must be
     * taken to ensure that a default value is not confused with a valid XML value.
     *
     * @return whether or not every attribute must be assigned a binding expression. The default
     *         value is true.
     */
    boolean requireAll() default true;
}
```

BindingAdapter 是一个作用在方法上的注解，作用就是：如何将有表达式的值设置给视图。

> 说明：
>
> ​	1）参数value 可以是单个值，也可以是多个值。多个值时使用{}花括号，用逗号隔开
>
> ​	2）requireAll表示是否必须要所有的value值全部使用BindingAdapter才生效，该参数主要是针对多个value值时，默认是true
>
> ​	3) 注解的方法是public static 修饰的，方法的第一个参数是视图View ,后面的参数是对应的value参数中的值。
>



业务场景：

1）当需要经过自定义的处理逻辑后才将结果赋值给某个属性时

2）自定义视图属性

```java
public class ImageViewAdapter{

    //单个属性
	@BindingAdapter("imageurl")//这里是属性名
	public static void setImageUrl(ImageView imageView ,String Url){
		//这里可以完成Glide加载图片等工作
	}
    
    //多个属性
    public static void configImage(ImageView imageView,String url,Drawable error){
        
    }
}
```



## 五、总结

通过本文学习DataBinding的简单使用，与工作原理的源码分析，学习到DataBinding库其实也是使用APT技术+数据结构+观察者模式三个技术点的组合来完成数据的双向绑定，这样我们只需要更新数据源（Observable）就会同步更新视图对应的属性值，这样我们就可以把重心放在数据的处理逻辑上了。

技术点：**APT技术+数据结构+观察者模式**



