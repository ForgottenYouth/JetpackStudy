# Jetpack学习之----Navigation



## 介绍



[官方学习文档](https://developer.android.google.cn/guide/navigation?hl=zh_cn)

<!--Navigation 组件旨在用于具有一个主Activity和多个Fragment目的地的应用。-->

导航Navigator是方便用户在一个主Activity包含多个Fragment时，进行内容片断的切换的交互。

#### 优点：

1）将处理Fragment的事务进行了封装处理，用户不需要再处理该类事务

2）切换的动画标准化了

3）只需要配置好导航视图即可方便切换

4）片断之间的数据传递需要使用Gradle插件（该点本文未涉及）



#### **三个角色：**

**导航图：**该图包含了导航需要的相关信息，全部保存在xml资源文件中

**NavHost:**  负责显示目标内容的容器；

**NavController:** 管理导航的对象，根据需要来替换显示容器中的内容；



## Navigation使用



### 1、添加依赖

```groovy
 val nav_version = "2.3.5"

  // Java language implementation
  implementation("androidx.navigation:navigation-fragment:$nav_version")
  implementation("androidx.navigation:navigation-ui:$nav_version")
```



### 2、创建导航图

在res/navigation/nav_graph.xml 这里的文件名会在布局文件中使用；

> **navigation  是导航图的根节点，**
>
> ​		**app:startDestination 指定第一个目的地**
>
> **fragment标签：指明了目的地的信息**
>
> ​		**id 属性 是唯一标识，用于在代码中引用该目的地**
>
> ​		**name 属性 目的地的全类名**
>
> ​		**Label 属性 包含该目的地的用户可读名称**
>
> ​		**tool:layout:  属性 指定对应的布局文件**
>
> **action标签： 是目的地的子元素，例如有安装点击跳转时，可以直接调用action的对应的id即可**

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    app:startDestination="@id/home">

    <fragment
        android:id="@+id/home"
        android:name="com.leon.jetpack.navigator.FragmentFirst"
        android:label="首页"
        tools:layout="@layout/fragment_navigator_first_layout">
        
        <!--这里给第一个Fragment的按钮添加点击事件后的目的地是到第二个Fragment中-->
        <action
            android:id="@+id/gotoSectond"
            app:destination="@id/square" />
    </fragment>

    <fragment
        android:id="@+id/square"
        android:name="com.leon.jetpack.navigator.FragmentSecond"
        android:label="广场"
        tools:layout="@layout/fragment_navigator_second_layout">
        <action
            android:id="@+id/backtoFirst"
            app:destination="@id/home" />

        <action
            android:id="@+id/gotothird"
            app:destination="@+id/mine" />
    </fragment>
    <fragment
        android:id="@+id/mine"
        android:name="com.leon.jetpack.navigator.FragmentThird"
        android:label="我的"
        tools:layout="@layout/fragment_navigator_third_layout">
        <action
            android:id="@+id/backtoSecond"
            app:destination="@+id/square" />

        <action
            android:id="@+id/gotofirst"
            app:destination="@+id/home" />
    </fragment>

</navigation>
```



### 3、布局文件XML添加NavHostFragment容器

> **name: 是指定的NavHost的实现类**
>
> **app:navGraph 指向导航视图文件，这样导航控制器根据这个导航视图就可以导航所有的目的地**
>
> **app:defaultNavHost: 这个属性时确保NavHostFragment会拦截系统的返回事件。**

```xml
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_container"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toTopOf="@+id/nav_view"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/nav_graph" />
```



### 4、初始化导航控制器

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
       //这里就可以得到导航控制器
        NavController navController = navHostFragment.getNavController();

    }
```



### 5、操作导航控制器

```java
 Button btn = view.findViewById(R.id.btn1);
        btn.setOnClickListener(v -> {
            //这里的v 是当前操作的view ,navigate中的参数就是我们的导航视图中写好的action 的Id
            Navigation.findNavController(v).navigate(R.id.gotoSectond);
        });
```



### 6、结合BottomNavigationView 使用

#### 1）引入依赖包

```groovy
implementation 'com.google.android.material:material:1.1.0'
```



#### 2）创建Menu

<!--特别说明：-->

​		<!--item 的id 需要和nav_graph.xml 中fragment的id保持一致，否则不会tab切换会无效的。-->

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/home"
        android:icon="@drawable/ic_home"
        android:title="首页" />
    <item
        android:id="@+id/square"
        android:icon="@drawable/ic_square"
        android:title="广场" />

    <item
        android:id="@+id/mine"
        android:icon="@drawable/ic_person"
        android:title="我的" />
</menu>
```



#### 3）添加布局

```xml
  <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/nav_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        app:itemTextColor="#ff0000"
        app:layout_constraintBottom_toBottomOf="parent"
        app:menu="@menu/menu"
        tools:ignore="MissingConstraints" />
```



#### 4）绑定NavController

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        NavController navController = navHostFragment.getNavController();

        //下面通过NavigationUI将bottomNavigationView 与 navController进行绑定
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
```



## 原理分析



***当Activity 加载Fragment时，先从xml布局文件中解析导航视图的ID，当Fragment创建的时候根据解析出来的导航视图的ID找到导航视图的第一个目的地视图的ID ,然后找到对应该目的地ID的节点信息，通过类加载器加载找到的目的地ID对应的Fragment，最后通过FragmentSupportManager 来替换默认的显示容器，从而显示出来。***

***当在调用navigate导航新的目的地时，会根据传入的actionID ，找到新的目的地ID ,然后重复上面的步骤通过类加载器来加载新目的地Fragment ，然后再次替换显示容器。***

以上就是Navigator的执行过程，其中核心的技术是：**XMLPull解析，类加载 ，工厂模式来创建目的地实例**



### 1、创建第一个目的地Fragment(从Fragment的生命周期分析)

![navigator加载第一个目的地](E:\简书文章\JetPack\navigator\image-20210917110141522.png)

#### 1.1）NavHostFragment的onInflate（）解析布局文件

​	解析布局文件中的defaultNavHost 、navGraph 的值，并保存下来

```java
 @CallSuper
    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs,
            @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        final TypedArray navHost = context.obtainStyledAttributes(attrs,
                androidx.navigation.R.styleable.NavHost);
        final int graphId = navHost.getResourceId(
                androidx.navigation.R.styleable.NavHost_navGraph, 0);
        if (graphId != 0) {
            mGraphId = graphId;
        }
        navHost.recycle();

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment);
        final boolean defaultHost = a.getBoolean(R.styleable.NavHostFragment_defaultNavHost, false);
        if (defaultHost) {
            mDefaultNavHost = true;
        }
        a.recycle();
    }
```



#### 1.2) NavHostFragment的onCreate



##### 1.2.1)、 创建NavHostController 并添加FragmentNavigator

```java
public void onCreate(@Nullable Bundle savedInstanceState) {
        final Context context = requireContext();

    	// 创建NavHostController
        mNavController = new NavHostController(context);
        mNavController.setLifecycleOwner(this);
        mNavController.setOnBackPressedDispatcher(requireActivity().getOnBackPressedDispatcher());
        // Set the default state - this will be updated whenever
        // onPrimaryNavigationFragmentChanged() is called
        mNavController.enableOnBackPressed(
                mIsPrimaryBeforeOnCreate != null && mIsPrimaryBeforeOnCreate);
        mIsPrimaryBeforeOnCreate = null;
        mNavController.setViewModelStore(getViewModelStore());
    
    	// 给NavHostController 的provider 添加FragmentNavigator
        onCreateNavController(mNavController);

        Bundle navState = null;
        if (savedInstanceState != null) {
            navState = savedInstanceState.getBundle(KEY_NAV_CONTROLLER_STATE);
            if (savedInstanceState.getBoolean(KEY_DEFAULT_NAV_HOST, false)) {
                mDefaultNavHost = true;
                getParentFragmentManager().beginTransaction()
                        .setPrimaryNavigationFragment(this)
                        .commit();
            }
            mGraphId = savedInstanceState.getInt(KEY_GRAPH_ID);
        }

        if (navState != null) {
            // Navigation controller state overrides arguments
            mNavController.restoreState(navState);
        }
        if (mGraphId != 0) {
            // Set from onInflate()
            //此处官方已经注释是在onInflate()中解析到布局文件中的值，这里是关键
            mNavController.setGraph(mGraphId);
        } else {
            // See if it was set by NavHostFragment.create()
            final Bundle args = getArguments();
            final int graphId = args != null ? args.getInt(KEY_GRAPH_ID) : 0;
            final Bundle startDestinationArgs = args != null
                    ? args.getBundle(KEY_START_DESTINATION_ARGS)
                    : null;
            if (graphId != 0) {
                mNavController.setGraph(graphId, startDestinationArgs);
            }
        }

        // We purposefully run this last as this will trigger the onCreate() of
        // child fragments, which may be relying on having the NavController already
        // created and having its state restored by that point.
        super.onCreate(savedInstanceState);
    }
```



##### 1.2.2) NavController 设置Graph 



###### 1)、inflate 导航视图

> XMLPull 解析器解析导航视图xml文件
>
> 创建NavDestination(NavGraph)，
>
> dest.onInflate(mContext, attrs); 这里需要注意，由于NavGraph 是NavDestination 的子类，所以这里会调入NavGraph 的onInflate()方法中

```java
//NavInflater.java
@SuppressLint("ResourceType")
    @NonNull
    public NavGraph inflate(@NavigationRes int graphResId) {
        Resources res = mContext.getResources();
        XmlResourceParser parser = res.getXml(graphResId);
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        try {
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG
                    && type != XmlPullParser.END_DOCUMENT) {
                // Empty loop
            }
            if (type != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("No start tag found");
            }

            String rootElement = parser.getName();
            //解析NavDestination 
            NavDestination destination = inflate(res, parser, attrs, graphResId);
            if (!(destination instanceof NavGraph)) {
                throw new IllegalArgumentException("Root element <" + rootElement + ">"
                        + " did not inflate into a NavGraph");
            }
            return (NavGraph) destination;
        } catch (Exception e) {
            throw new RuntimeException("Exception inflating "
                    + res.getResourceName(graphResId) + " line "
                    + parser.getLineNumber(), e);
        } finally {
            parser.close();
        }
    }


@NonNull
    private NavDestination inflate(@NonNull Resources res, @NonNull XmlResourceParser parser,
            @NonNull AttributeSet attrs, int graphResId)
            throws XmlPullParserException, IOException {
        Navigator<?> navigator = mNavigatorProvider.getNavigator(parser.getName());
        
        //这里创建一个NavDestination
        final NavDestination dest = navigator.createDestination();

        //进入NavDestination 的onInflate中 这里需要特别注意
        dest.onInflate(mContext, attrs);

        final int innerDepth = parser.getDepth() + 1;
        int type;
        int depth;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && ((depth = parser.getDepth()) >= innerDepth
                || type != XmlPullParser.END_TAG)) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            if (depth > innerDepth) {
                continue;
            }

            final String name = parser.getName();
            if (TAG_ARGUMENT.equals(name)) {
                inflateArgumentForDestination(res, dest, attrs, graphResId);
            } else if (TAG_DEEP_LINK.equals(name)) {
                inflateDeepLink(res, dest, attrs);
            } else if (TAG_ACTION.equals(name)) {
                inflateAction(res, dest, attrs, parser, graphResId);
            } else if (TAG_INCLUDE.equals(name) && dest instanceof NavGraph) {
                final TypedArray a = res.obtainAttributes(
                        attrs, androidx.navigation.R.styleable.NavInclude);
                final int id = a.getResourceId(
                        androidx.navigation.R.styleable.NavInclude_graph, 0);
                ((NavGraph) dest).addDestination(inflate(id));
                a.recycle();
            } else if (dest instanceof NavGraph) {
                ((NavGraph) dest).addDestination(inflate(res, parser, attrs, graphResId));
            }
        }
        return dest;
    }


//NavGraph.java
    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
        super.onInflate(context, attrs);//这句话又会调用NavDestination 的onInflate方法
        TypedArray a = context.getResources().obtainAttributes(attrs,
                R.styleable.NavGraphNavigator);
        //下面就更导航视图文件的根节点解析到了第一个目的地
        setStartDestination(
                a.getResourceId(R.styleable.NavGraphNavigator_startDestination, 0));
        mStartDestIdName = getDisplayName(context, mStartDestId);
        a.recycle();
    }


///NavDestination.java 
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
        final TypedArray a = context.getResources().obtainAttributes(attrs,
                R.styleable.Navigator);
        setId(a.getResourceId(R.styleable.Navigator_android_id, 0));
        mIdName = getDisplayName(context, mId);
        setLabel(a.getText(R.styleable.Navigator_android_label));
        a.recycle();
    }

   @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
        super.onInflate(context, attrs);
        TypedArray a = context.getResources().obtainAttributes(attrs,
                R.styleable.NavGraphNavigator);
        setStartDestination(
                a.getResourceId(R.styleable.NavGraphNavigator_startDestination, 0));
        mStartDestIdName = getDisplayName(context, mStartDestId);
        a.recycle();
    }
//NavGraph.java


```



###### 2)、setGraph()

```java
 //NavController.java

public void setGraph(@NavigationRes int graphResId) {
        setGraph(graphResId, null);
    }


//>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 @CallSuper
    public void setGraph(@NavigationRes int graphResId, @Nullable Bundle startDestinationArgs) {
        //下面的startDestinationArgs 是通过上一步传递进来的null
        //这里进入inflate，参数就是导航文件
        setGraph(getNavInflater().inflate(graphResId), startDestinationArgs);
    }


//>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void setGraph(@NonNull NavGraph graph, @Nullable Bundle startDestinationArgs) {
        if (mGraph != null) {
            // Pop everything from the old graph off the back stack
            popBackStackInternal(mGraph.getId(), true);
        }
        mGraph = graph;//此处将创建的NavDestination保存下来
        onGraphCreated(startDestinationArgs);
    }


//>>>>>>>>>>>>>>>>>>>>>>>>>>>>
private void onGraphCreated(@Nullable Bundle startDestinationArgs) {
        
    	//此处省略了干扰代码
    
        if (mGraph != null && mBackStack.isEmpty()) {
            boolean deepLinked = !mDeepLinkHandled && mActivity != null
                    && handleDeepLink(mActivity.getIntent());
            if (!deepLinked) {
                // Navigate to the first destination in the graph
                // if we haven't deep linked to a destination
                //这里将第一个目的地作为第一个参数传递进去
                navigate(mGraph, startDestinationArgs, null, null);
            }
        } else {
            dispatchOnDestinationChanged();
        }
    }


//>>>>>>>>>>>>>>>>>>>>>>>>>>>>
private void navigate(@NonNull NavDestination node, @Nullable Bundle args,
            @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
       
    	//.........
    
        Navigator<NavDestination> navigator = mNavigatorProvider.getNavigator(
                node.getNavigatorName());
        Bundle finalArgs = node.addInDefaultArgs(args);
    //	此处使用Navigator 进行导航，而Navigator是抽象类，那么就进入他的实现类FragmentNavigator
        NavDestination newDest = navigator.navigate(node, finalArgs,
                navOptions, navigatorExtras);
       //.....
    }

```



###### 3)、FragmentNavigator 的Navigate()

```java
public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args,
            @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        if (mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state");
            return null;
        }
        String className = destination.getClassName();
        if (className.charAt(0) == '.') {
            className = mContext.getPackageName() + className;
        }
   		 //这里会根据目的地创建一个Fragment 
        final Fragment frag = instantiateFragment(mContext, mFragmentManager,
                className, args);
        frag.setArguments(args);
    
    	//下面就开始Fragment transcation了
        final FragmentTransaction ft = mFragmentManager.beginTransaction();

        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = enterAnim != -1 ? enterAnim : 0;
            exitAnim = exitAnim != -1 ? exitAnim : 0;
            popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
            popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        }

    	//这里使用创建好的目的地Fragment 来replace mContainerId,这个ID是系统内置的R.id.nav_host_fragment_container
        ft.replace(mContainerId, frag);
        ft.setPrimaryNavigationFragment(frag);

        ///............此处省略代码
    }


///>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public Fragment instantiateFragment(@NonNull Context context,
            @NonNull FragmentManager fragmentManager,
            @NonNull String className, @SuppressWarnings("unused") @Nullable Bundle args) {
        
        return fragmentManager.getFragmentFactory().instantiate(
                context.getClassLoader(), className);
    }
```



###### 4)、创建目的地实例 FragmentFactory.java instantiate()

> 通过类加载器来加载第一个目的地的类型；
>
> 类构造器构造创建目的地实例。

```java
//FragmentFactory.java

public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        try {
            //使用类加载器加载类
            Class<? extends Fragment> cls = loadFragmentClass(classLoader, className);
            
            //类构造器构造实例
            return cls.getConstructor().newInstance();
        } catch (java.lang.InstantiationException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        } catch (IllegalAccessException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        } catch (NoSuchMethodException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": could not find Fragment constructor", e);
        } catch (InvocationTargetException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": calling Fragment constructor caused an exception", e);
        }
    }


public static Class<? extends Fragment> loadFragmentClass(@NonNull ClassLoader classLoader,
            @NonNull String className) {
        try {
            Class<?> clazz = loadClass(classLoader, className);
            return (Class<? extends Fragment>) clazz;
        } catch (ClassNotFoundException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": make sure class name exists", e);
        } catch (ClassCastException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": make sure class is a valid subclass of Fragment", e);
        }
    }
```



### 2、导航控制器导航目的地



![Navigator导航调用过程](E:\简书文章\JetPack\navigator\image-20210917110744292.png)



#### 2.1） 从调用开始

```java
 Navigation.findNavController(v).navigate(R.id.backtoSecond);
```



#### 2.2）找到目的地ID

1. 根据actionID找到导航视图中对应的Action信息
2. 从Action信息中得到新的目的地ID
3. 查找目的地ID ,得到对应的NavDestation()
4. 导航到新的目的地

```java
//NavController.java
public void navigate(@IdRes int resId, @Nullable Bundle args, @Nullable NavOptions navOptions,
            @Nullable Navigator.Extras navigatorExtras) {
        NavDestination currentNode = mBackStack.isEmpty()
                ? mGraph
                : mBackStack.getLast().getDestination();
        if (currentNode == null) {
            throw new IllegalStateException("no current navigation node");
        }
        @IdRes int destId = resId;
    //根据传入的ID找到当前Fragment节点中的action节点信息
        final NavAction navAction = currentNode.getAction(resId);
        Bundle combinedArgs = null;
        if (navAction != null) {
            if (navOptions == null) {
                navOptions = navAction.getNavOptions();
            }
            //从action中获取下一个目的地的id
            destId = navAction.getDestinationId();
            Bundle navActionArgs = navAction.getDefaultArguments();
            if (navActionArgs != null) {
                combinedArgs = new Bundle();
                combinedArgs.putAll(navActionArgs);
            }
        }

        if (args != null) {
            if (combinedArgs == null) {
                combinedArgs = new Bundle();
            }
            combinedArgs.putAll(args);
        }

        if (destId == 0 && navOptions != null && navOptions.getPopUpTo() != -1) {
            popBackStack(navOptions.getPopUpTo(), navOptions.isPopUpToInclusive());
            return;
        }

        if (destId == 0) {
            throw new IllegalArgumentException("Destination id == 0 can only be used"
                    + " in conjunction with a valid navOptions.popUpTo");
        }

    //查找下一目的地
        NavDestination node = findDestination(destId);
        if (node == null) {
            final String dest = NavDestination.getDisplayName(mContext, destId);
            if (navAction != null) {
                throw new IllegalArgumentException("Navigation destination " + dest
                        + " referenced from action "
                        + NavDestination.getDisplayName(mContext, resId)
                        + " cannot be found from the current destination " + currentNode);
            } else {
                throw new IllegalArgumentException("Navigation action/destination " + dest
                        + " cannot be found from the current destination " + currentNode);
            }
        }
    //导航到新的目的地
        navigate(node, combinedArgs, navOptions, navigatorExtras);
    }


///>>>>>>>>>>>>>>>>>>>>>>>
    NavDestination findDestination(@IdRes int destinationId) {
        if (mGraph == null) {
            return null;
        }
        if (mGraph.getId() == destinationId) {
            return mGraph;
        }
        NavDestination currentNode = mBackStack.isEmpty()
                ? mGraph
                : mBackStack.getLast().getDestination();
        NavGraph currentGraph = currentNode instanceof NavGraph
                ? (NavGraph) currentNode
                : currentNode.getParent();
        return currentGraph.findNode(destinationId);
    }

////>>>>>>>>>>>>>>>>>>>
private void navigate(@NonNull NavDestination node, @Nullable Bundle args,
            @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        boolean popped = false;
        boolean launchSingleTop = false;
        if (navOptions != null) {
            if (navOptions.getPopUpTo() != -1) {
                popped = popBackStackInternal(navOptions.getPopUpTo(),
                        navOptions.isPopUpToInclusive());
            }
        }
        Navigator<NavDestination> navigator = mNavigatorProvider.getNavigator(
                node.getNavigatorName());
        Bundle finalArgs = node.addInDefaultArgs(args);
    //这里进入FragmentNavigator的navigate()中，进行导航
        NavDestination newDest = navigator.navigate(node, finalArgs,
                navOptions, navigatorExtras);
       //.........
    }
```

#### 2.3）类加载器加载新的目的地Fragment

这里和加载第一个目的地Fragment的方式一样，通过类加载器加载目的地Fragment,然后通过替换显示容器将目的地Fragment显示出来。





## 总结



技术点：**类加载、工厂模式、XML---PULL解析**





