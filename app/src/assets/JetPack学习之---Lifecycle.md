# JetPack学习之---Lifecycle



## 一、介绍

[官方学习文档](https://developer.android.google.cn/topic/libraries/architecture/lifecycle?hl=zh_cn)

Lifecycle 是Jetpack库中一个软件包，通过存储有生命周期状态的组件的生命周期信息，并允许其他的对象观察此状态，来达到能方便让自定义的组件感知到Activity ，Fragment等的生命周期，这样就可以根据生命周期来完成对应的工作了。

Lifecycle采用的是观察者模式，观察者需要实现LifecycleObserver接口，   被观察者需要实现LifecycleOwner接口，然后给被观察者与观察者通过addObserver()建立关系。这样达到观察生命周期状态的目的。

特点：
*   精简代码
*   易于维护


#### LifecycleOwner

他是一个只有一个方法的接口，只要实现了这个接口的类，就表示他是具有生命周期状态的提供者了。该接口提供了一个统一的获取生命周期提供者的方法getLifecycle()方法。


#### LifecycleObserver

这是一个空的接口，实现了该接口的类，就可以作为有生命周期状态组件的观察者进行观察了。

实现了LifecycleObserver的观察者角色的组件与实现了LifecycleOwner的被观察者组件通过addObserver()来建立观察关系，这样观察者就可以观察被观察者的生命周期了。



## 二、基本使用

#### 1、添加依赖

```groovy
 def lifecycle_version = "2.4.0-alpha03"
    // Lifecycles only (without ViewModel or LiveData)
 implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

    // Annotation processor
 annotationProcessor "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"
    // alternately - if using Java8, use the following instead of lifecycle-compiler
 implementation "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"
```



#### 2、自定义Observer,需要实现LifecycleObserver接口

通过注解@OnLifecycleEvent()  参数是监听的生命周期的事件

```java
public class CustomLifecycleObserver implements LifecycleObserver {

    private static final String TAG = CustomLifecycleObserver.class.getSimpleName();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreateX() {
        Log.e(TAG, "CustomLifecycleObserver---onCreateX:");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResumeX() {
        Log.e(TAG, "CustomLifecycleObserver---onResumeX:");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStopX() {
        Log.e(TAG, "CustomLifecycleObserver---onStopX:");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestoryX() {
        Log.e(TAG, "CustomLifecycleObserver---onDestoryX:");
    }
}

```

#### 3、观察者与被观察者建立关联

MainActivity--->AppCompatActivity--->FragmentActivity--->ComponentActivity 他们之间的这种父子关系，而ComponentActivity就是LifecycleOwner接口，所以MainActivity是生命周期的提供者，是被观察者角色。

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLifecycle().addObserver(new CustomLifecycleObserver());
    }
}
```



经过上面简单的三步自定义的Observer就可以感知到MainActivity的生命周期了，打印的日志如下：

```ABAP
2021-08-11 10:38:09.105 19493-19493/com.leon.study_jetpack_lifecycle E/CustomLifecycleObserver: CustomLifecycleObserver---onCreateX:
2021-08-11 10:38:09.134 19493-19493/com.leon.study_jetpack_lifecycle E/CustomLifecycleObserver: CustomLifecycleObserver---onResumeX:
2021-08-11 10:38:09.401 19493-19493/com.leon.study_jetpack_lifecycle E/SpannableStringBuilder: SPAN_EXCLUSIVE_EXCLUSIVE spans cannot have a zero length
2021-08-11 10:38:09.401 19493-19493/com.leon.study_jetpack_lifecycle E/SpannableStringBuilder: SPAN_EXCLUSIVE_EXCLUSIVE spans cannot have a zero length
2021-08-11 10:39:03.591 19493-19493/com.leon.study_jetpack_lifecycle E/CustomLifecycleObserver: CustomLifecycleObserver---onStopX:
2021-08-11 10:39:03.605 19493-19493/com.leon.study_jetpack_lifecycle E/CustomLifecycleObserver: CustomLifecycleObserver---onDestoryX:
```





## 三、实现原理



### 1、原理概述

观察者Observer是通过注解处理器将生命周期相关的事件添加到一个集合里面去

给被观察者中添加了一个没有UI界面的Fragment，Fragment与被观察者的生命周期绑定，然后在Fragment的生命周期方法中根据当前Activity的生命周期的状态，与事件的对应关系，然后开始对observer集合进行事件分发，当observer的状态发送变化时，就会通过反射来反射调用observer对应的方法，这样就掉到观察者的方法中去了。

其中核心的一点是同步观察者和被观察者的状态。



### 2、源码分析：

#### 2.1) 先来分析一下建立观察关系的流程

**基本思路：**

​	1、通过反射技术将使用注解的方法的相关信息保存到一个map中去；

​	2、根据当前被观察者的状态，遍历(1)中map里面的方法，找到对应注解事件中的方法，然后分发事件来触发并同步观察者当前被观察者的状态。

**Lifecycle的状态:** 

​		是以一个从0 开始的枚举值，DESTROYED=0   <   INITIALIZED=1  <  CREATED=2  <  STARTED=3   < RESUMED=4

```java
 /**
     * Lifecycle states. You can consider the states as the nodes in a graph and
     * {@link Event}s as the edges between these nodes.
     */
    @SuppressWarnings("WeakerAccess")
    public enum State {
        /**
         * Destroyed state for a LifecycleOwner. After this event, this Lifecycle will not dispatch
         * any more events. For instance, for an {@link android.app.Activity}, this state is reached
         * <b>right before</b> Activity's {@link android.app.Activity#onDestroy() onDestroy} call.
         */
        DESTROYED,

        /**
         * Initialized state for a LifecycleOwner. For an {@link android.app.Activity}, this is
         * the state when it is constructed but has not received
         * {@link android.app.Activity#onCreate(android.os.Bundle) onCreate} yet.
         */
        INITIALIZED,

        /**
         * Created state for a LifecycleOwner. For an {@link android.app.Activity}, this state
         * is reached in two cases:
         * <ul>
         *     <li>after {@link android.app.Activity#onCreate(android.os.Bundle) onCreate} call;
         *     <li><b>right before</b> {@link android.app.Activity#onStop() onStop} call.
         * </ul>
         */
        CREATED,

        /**
         * Started state for a LifecycleOwner. For an {@link android.app.Activity}, this state
         * is reached in two cases:
         * <ul>
         *     <li>after {@link android.app.Activity#onStart() onStart} call;
         *     <li><b>right before</b> {@link android.app.Activity#onPause() onPause} call.
         * </ul>
         */
        STARTED,

        /**
         * Resumed state for a LifecycleOwner. For an {@link android.app.Activity}, this state
         * is reached after {@link android.app.Activity#onResume() onResume} is called.
         */
        RESUMED;

        /**
         * Compares if this State is greater or equal to the given {@code state}.
         *
         * @param state State to compare with
         * @return true if this State is greater or equal to the given {@code state}
         */
        public boolean isAtLeast(@NonNull State state) {
            return compareTo(state) >= 0;
        }
    }
```



##### 第一步：添加观察者

```java
//LifecycleRegistry.java文件

  @Override
    public void addObserver(@NonNull LifecycleObserver observer) {
        State initialState = mState == DESTROYED ? DESTROYED : INITIALIZED;
        //这里将传递进来的观察者与当前的初始化状态打包一下
        ObserverWithState statefulObserver = new ObserverWithState(observer, initialState);
        
        //将打包好的观察者保存到成员变量map中去
        ObserverWithState previous = mObserverMap.putIfAbsent(observer, statefulObserver);

        //如果已经在map中存在，就直接返回了
        if (previous != null) {
            return;
        }
        
        //获取被观察者
        LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
        if (lifecycleOwner == null) {
            // it is null we should be destroyed. Fallback quickly
            return;
        }

        boolean isReentrance = mAddingObserverCounter != 0 || mHandlingEvent;
        State targetState = calculateTargetState(observer);
        mAddingObserverCounter++;
        while ((statefulObserver.mState.compareTo(targetState) < 0
                && mObserverMap.contains(observer))) {
            pushParentState(statefulObserver.mState);
            statefulObserver.dispatchEvent(lifecycleOwner, upEvent(statefulObserver.mState));
            popParentState();
            // mState / subling may have been changed recalculate
            targetState = calculateTargetState(observer);
        }

        if (!isReentrance) {
            // we do sync only on the top level.
            sync();//这里一个同步方法来同步状态
        }
        mAddingObserverCounter--;
    }
```



##### 第二步：包装观察者

```java
//LifecycleRegistry.java
   static class ObserverWithState {
        State mState;
        LifecycleEventObserver mLifecycleObserver;

        ObserverWithState(LifecycleObserver observer, State initialState) {
            //在此构造方法中包装，继续进入Lifecycling类中
            mLifecycleObserver = Lifecycling.lifecycleEventObserver(observer);
            mState = initialState;
        }

        void dispatchEvent(LifecycleOwner owner, Event event) {
            State newState = getStateAfter(event);
            mState = min(mState, newState);
            mLifecycleObserver.onStateChanged(owner, event);
            mState = newState;
        }
    }

//Lifecycling.java
static LifecycleEventObserver lifecycleEventObserver(Object object) {
       	//......这里省略了很多源码
   		//从这里return 的地方看
        return new ReflectiveGenericLifecycleObserver(object);
    }

//ReflectiveGenericLifecycleObserver.java
class ReflectiveGenericLifecycleObserver implements LifecycleEventObserver {//他是一个生命周期事件的观察者
    private final Object mWrapped;
    private final CallbackInfo mInfo;

    ReflectiveGenericLifecycleObserver(Object wrapped) {
        mWrapped = wrapped;
        //在这里看看getInfo
        mInfo = ClassesInfoCache.sInstance.getInfo(mWrapped.getClass());
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Event event) {
        mInfo.invokeCallbacks(source, event, mWrapped);
    }
}

//ClassesInfoCache.java
  CallbackInfo getInfo(Class klass) {
        CallbackInfo existing = mCallbackMap.get(klass);
        if (existing != null) {
            return existing;
        }
        existing = createInfo(klass, null);
        return existing;
    }

//此处才是最关键的，通过注解来回去生命周期注解对应的方法及其实例对象
private CallbackInfo createInfo(Class klass, @Nullable Method[] declaredMethods) {
        //todo 省略了源码

        Class[] interfaces = klass.getInterfaces();
        for (Class intrfc : interfaces) {
            for (Map.Entry<MethodReference, Lifecycle.Event> entry : getInfo(
                    intrfc).mHandlerToEvent.entrySet()) {
                verifyAndPutHandler(handlerToEvent, entry.getKey(), entry.getValue(), klass);
            }
        }

    	// 反射获取注解信息的地方
        Method[] methods = declaredMethods != null ? declaredMethods : getDeclaredMethods(klass);
        boolean hasLifecycleMethods = false;
        for (Method method : methods) {
            OnLifecycleEvent annotation = method.getAnnotation(OnLifecycleEvent.class);
            if (annotation == null) {
                continue;
            }
            hasLifecycleMethods = true;
            Class<?>[] params = method.getParameterTypes();
            int callType = CALL_TYPE_NO_ARG;
            if (params.length > 0) {
                callType = CALL_TYPE_PROVIDER;
                if (!params[0].isAssignableFrom(LifecycleOwner.class)) {
                    throw new IllegalArgumentException(
                            "invalid parameter type. Must be one and instanceof LifecycleOwner");
                }
            }
            Lifecycle.Event event = annotation.value();

            if (params.length > 1) {
                callType = CALL_TYPE_PROVIDER_WITH_EVENT;
                if (!params[1].isAssignableFrom(Lifecycle.Event.class)) {
                    throw new IllegalArgumentException(
                            "invalid parameter type. second arg must be an event");
                }
                if (event != Lifecycle.Event.ON_ANY) {
                    throw new IllegalArgumentException(
                            "Second arg is supported only for ON_ANY value");
                }
            }
            if (params.length > 2) {
                throw new IllegalArgumentException("cannot have more than 2 params");
            }
            MethodReference methodReference = new MethodReference(callType, method);
            //这里将每一个注解过的方法打包放到map中去
            verifyAndPutHandler(handlerToEvent, methodReference, event, klass);
        }
        CallbackInfo info = new CallbackInfo(handlerToEvent);
        mCallbackMap.put(klass, info);
        mHasLifecycleMethods.put(klass, hasLifecycleMethods);
        return info;
    }
```



##### 第三步：同步状态

```java
//LifecycleRegistry.java文件
 private void sync() {
        LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
        if (lifecycleOwner == null) {
            throw new IllegalStateException("LifecycleOwner of this LifecycleRegistry is already"
                    + "garbage collected. It is too late to change lifecycle state.");
        }
        while (!isSynced()) {
            mNewEventOccurred = false;
            // no need to check eldest for nullability, because isSynced does it for us.
            
            if (mState.compareTo(mObserverMap.eldest().getValue().mState) < 0) {
                /**
                例如：mState 最初的初始值是INITIALIZED，而最早添加的观察者的状态是CREATED 这时候，就需要将状态向后转变为对应的事件来通知给观察者
                */
                backwardPass(lifecycleOwner);
            }
            Entry<LifecycleObserver, ObserverWithState> newest = mObserverMap.newest();
            if (!mNewEventOccurred && newest != null
                    && mState.compareTo(newest.getValue().mState) > 0) {
                /**
                这里和backwardPass的方法刚好是相反的
                */
                forwardPass(lifecycleOwner);
            }
        }
        mNewEventOccurred = false;
    }

	//向后分发事件
private void backwardPass(LifecycleOwner lifecycleOwner) {
        Iterator<Entry<LifecycleObserver, ObserverWithState>> descendingIterator =
                mObserverMap.descendingIterator();
        while (descendingIterator.hasNext() && !mNewEventOccurred) {
            Entry<LifecycleObserver, ObserverWithState> entry = descendingIterator.next();
            ObserverWithState observer = entry.getValue();
            while ((observer.mState.compareTo(mState) > 0 && !mNewEventOccurred
                    && mObserverMap.contains(entry.getKey()))) {
                //生命状态转变成对应的事件
                Event event = downEvent(observer.mState);
                pushParentState(getStateAfter(event));
                //开始分发事件
                observer.dispatchEvent(lifecycleOwner, event);
                popParentState();
            }
        }
    }

	//将当前的状态转化成对应的事件
    private static Event downEvent(State state) {
        switch (state) {
            case INITIALIZED:
                throw new IllegalArgumentException();
            case CREATED:
                return ON_DESTROY;
            case STARTED:
                return ON_STOP;
            case RESUMED:
                return ON_PAUSE;
            case DESTROYED:
                throw new IllegalArgumentException();
        }
        throw new IllegalArgumentException("Unexpected state value " + state);
    }
```



通过下图来理解一下上面的状态变化：

![生命周期状态与事件关系图](E:\简书文章\JetPack\lifecycle\image-20210811114726327.png)



#### 2.2）生命周期变化的通知流程

##### 第一步：无UI的空Fragment

进入MainActivity的父类ComponentActivity的onCreate()方法

```java
//ComponentActivity.java    
@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedStateRegistryController.performRestore(savedInstanceState);
        //此处有有一个ReportFragment 
        ReportFragment.injectIfNeededIn(this);
        if (mContentLayoutId != 0) {
            setContentView(mContentLayoutId);
        }
    }

//这里可以看到其实是在Activity中添加了一个空的没有UI界面的Fragment来处理生命周期的 这一点与Glide感知生命周期的原理类似

public static void injectIfNeededIn(Activity activity) {
        // ProcessLifecycleOwner should always correctly work and some activities may not extend
        // FragmentActivity from support lib, so we use framework fragments for activities
        android.app.FragmentManager manager = activity.getFragmentManager();
        if (manager.findFragmentByTag(REPORT_FRAGMENT_TAG) == null) {
            manager.beginTransaction().add(new ReportFragment(), REPORT_FRAGMENT_TAG).commit();
            // Hopefully, we are the first to make a transaction.
            manager.executePendingTransactions();
        }
    }
```



##### 第二步：进入ReportFragment的生命周期方法（例如：onStart()）

```java
//>>>>>>>>>>>>>>>>   ReportFragment.java  
	@Override
    public void onStart() {
        super.onStart();
        //这里应该是一个处理进程间生命周期的监听回调，事件分发
        dispatchStart(mProcessListener);
        
        //这里是事件分发
        dispatch(Lifecycle.Event.ON_START);
    }

//进入方法
	private void dispatch(Lifecycle.Event event) {
        Activity activity = getActivity();
    	//此处已经弃用
        if (activity instanceof LifecycleRegistryOwner) {
            ((LifecycleRegistryOwner) activity).getLifecycle().handleLifecycleEvent(event);
            return;
        }

    //我们的ComponentActivity是实现了LifecycleOwner接口
        if (activity instanceof LifecycleOwner) {
            Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();
            if (lifecycle instanceof LifecycleRegistry) {
                ((LifecycleRegistry) lifecycle).handleLifecycleEvent(event);
            }
        }
    }


```



##### 第三步：同步状态

```java
//>>>>>>>>>>>>>>>>  LifecycleRegistry.java 
public void handleLifecycleEvent(@NonNull Lifecycle.Event event) {
        State next = getStateAfter(event);
        moveToState(next);
    }

static State getStateAfter(Event event) {
        switch (event) {
            case ON_CREATE:
            case ON_STOP:
                return CREATED;
            case ON_START:
            case ON_PAUSE:
                return STARTED;
            case ON_RESUME:
                return RESUMED;
            case ON_DESTROY:
                return DESTROYED;
            case ON_ANY:
                break;
        }
        throw new IllegalArgumentException("Unexpected event value " + event);
    }

//这里添加观察者时一样，都会同步状态
private void moveToState(State next) {
        if (mState == next) {
            return;
        }
        mState = next;
        if (mHandlingEvent || mAddingObserverCounter != 0) {
            mNewEventOccurred = true;
            // we will figure out what to do on upper level.
            return;
        }
        mHandlingEvent = true;
        sync();
        mHandlingEvent = false;
    }
```



##### 第四步：事件分发

```java
//>>>>>>>>>>>>>>>>  LifecycleRegistry.java 
 private void sync() {
       //////...............
        while (!isSynced()) {
            mNewEventOccurred = false;
            // no need to check eldest for nullability, because isSynced does it for us.
            if (mState.compareTo(mObserverMap.eldest().getValue().mState) < 0) {
                //该方法
                backwardPass(lifecycleOwner);
            }
          ////...............
        }
        mNewEventOccurred = false;
    }


private void backwardPass(LifecycleOwner lifecycleOwner) {
        Iterator<Entry<LifecycleObserver, ObserverWithState>> descendingIterator =
                mObserverMap.descendingIterator();
        while (descendingIterator.hasNext() && !mNewEventOccurred) {
            Entry<LifecycleObserver, ObserverWithState> entry = descendingIterator.next();
            ObserverWithState observer = entry.getValue();
            while ((observer.mState.compareTo(mState) > 0 && !mNewEventOccurred
                    && mObserverMap.contains(entry.getKey()))) {
                Event event = downEvent(observer.mState);
                pushParentState(getStateAfter(event));
                
                //观察者分发事件
                observer.dispatchEvent(lifecycleOwner, event);
                popParentState();
            }
        }
    }


static class ObserverWithState {
        State mState;
        LifecycleEventObserver mLifecycleObserver;

        ObserverWithState(LifecycleObserver observer, State initialState) {
            mLifecycleObserver = Lifecycling.lifecycleEventObserver(observer);
            mState = initialState;
        }

        void dispatchEvent(LifecycleOwner owner, Event event) {
            State newState = getStateAfter(event);
            mState = min(mState, newState);
            //下面的onStateChanged是一个借口的方法，他的实现类之一：ReflectiveGenericLifecycleObserver
            mLifecycleObserver.onStateChanged(owner, event);
            mState = newState;
        }
    }
```

第五步：反射回调观察者的方法

```java
//>>>>>>>>>>>>>>>    ReflectiveGenericLifecycleObserver.java
class ReflectiveGenericLifecycleObserver implements LifecycleEventObserver {
    private final Object mWrapped;
    private final CallbackInfo mInfo;

    ReflectiveGenericLifecycleObserver(Object wrapped) {
        mWrapped = wrapped;
        //这个地方之前在添加观察者时进来过
        mInfo = ClassesInfoCache.sInstance.getInfo(mWrapped.getClass());
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Event event) {
        //进入反射回调
        mInfo.invokeCallbacks(source, event, mWrapped);
    }
}


//ClassesInfoCache.java
 void invokeCallbacks(LifecycleOwner source, Lifecycle.Event event, Object target) {
            invokeMethodsForEvent(mEventToHandlers.get(event), source, event, target);
            invokeMethodsForEvent(mEventToHandlers.get(Lifecycle.Event.ON_ANY), source, event,
                    target);
        }

private static void invokeMethodsForEvent(List<MethodReference> handlers,
                LifecycleOwner source, Lifecycle.Event event, Object mWrapped) {
            if (handlers != null) {
                for (int i = handlers.size() - 1; i >= 0; i--) {
                    //这里的handlers是在添加观察者时，保存在map里面的注解过的方法的信息
                    handlers.get(i).invokeCallback(source, event, mWrapped);
                }
            }
        }

//这一步就真正回调到注解注释过的观察者的方法里面去了
void invokeCallback(LifecycleOwner source, Lifecycle.Event event, Object target) {
            //noinspection TryWithIdenticalCatches
            try {
                switch (mCallType) {
                    case CALL_TYPE_NO_ARG:
                        mMethod.invoke(target);
                        break;
                    case CALL_TYPE_PROVIDER:
                        mMethod.invoke(target, source);
                        break;
                    case CALL_TYPE_PROVIDER_WITH_EVENT:
                        mMethod.invoke(target, source, event);
                        break;
                }
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Failed to call observer method", e.getCause());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
```



## 四、总结

Jetpack的Lifecycle的理解 ，核心思想是观察者模式思想；

当给一个生命收起组件添加观察者时，通过反射来获取观察者被注解的方法信息，并将这些方法保存下来方便后续发送事件通知；

对于生命周期的提供者（目前是Activity）在其内部添加了一个空Fragment, 由这个**空Fragmen**t专门负责发送生命周期事件,在事件发送中通过**反射机制**来回调到观察者的方法中。

技术点：**注解，APT技术，反射，观察者模式**，

知识点：生命周期与对应事件的互换思路（如图：<u>生命周期状态与事件关系图</u>）

