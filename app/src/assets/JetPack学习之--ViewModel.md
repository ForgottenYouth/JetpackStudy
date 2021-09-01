# JetPack学习之--ViewModel



## 一、介绍

[官方学习文档](https://developer.android.google.cn/topic/libraries/architecture/viewmodel?hl=zh_cn)

ViewModel就是存储页面相关的数据，并将这些数据和Activity、Fragment等有生命周期相关的组件相关联，赋予数据生命周期。

**特点：**

1.  **将数据与界面控制器进行分离**（也就是将经常在Actvity、Fragment中的保存的数据分离出来，这样这样界面控制器就主要负责与UI相关的事情即可）

2.  **加大保存数据的范围**（当页面发送无意关闭时，我们一般是onSaveInstanceState()来保存数据，但是这样保存的数据大小有限制，而且是必须可序列化再反序列化的数据，而viewmodel可以保存我们页面所需的所有数据）

3.  **分离界面控制器的工作**（将一些与数据相关的业务处理分来出来，减少控制器对业务的处理工作）

4.  **ViewModel 保存的数据会在activity完成时，由框架调用onCleared方法清理资源**

**ViewModel的生命周期**

在viewModel对象创建时开始，一直到他所关联的界面控制器销毁时才销毁，这就说明了即使发生了横竖屏切换，界面相关的数据也是一直存在并且不受横竖屏切换的影响。

通常我们是在Actvity的onCreate()方法中来创建ViewModel对象，该ViewModel对象会一直在内存中，直到这个Activity销毁时才释放资源。

## 二、使用

### 1、 添加依赖

```kotlin
// ViewModel
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0-alpha03"
    // LiveData
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.4.0-alpha03"
```



### 2、 创建viewmodel

```kotlin
class MainViewModel:ViewModel() {
    var name:String="Leon"
    var position:MutableLiveData<String> = MutableLiveData()
}
```



### 3、 简单使用

```kotlin
class MainActivity : AppCompatActivity() {

    val TAG=MainActivity::class.java.simpleName
    lateinit var viewModel:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel= ViewModelProvider(this).get(MainViewModel::class.java)
        
      	//下面是结合LiveData来使用ViewModel的数据的
      	viewModel.position.observe(this,{
            Log.e(TAG, "onCreate: position value = ${it}")
        })
    }
}
```



## 三、工作原理

> 				**原理：**
> 			
> 				**当开始创建Activity的实例对象时，会生成用来存储ViewModel对象的ViewModelStore实例（使用Hashmap数据结构存储），并给当前Activity的生命周期添加观察者，用于观察Activity的生命周期变化，当Activity的生命周期是ON_DESTROY时，就会清理掉ViewModelStore中存储的ViewModel的所有对象，释放资源。**
> 			
> 				**创建ViewModelProvider对象实例时，会在其构造中调用生成一个AndroidViewModelFactory的全局工程对象，我们会使用这个工厂对象来反射自定义的ViewModel对象。**
> 			
> 				**当使用ViewModelProvider对象实例get自定义的ViewModel对象时，会先从ViewModelStore的Hashmap去找，如果没有找到，就用上一步的AndroidViewModelFactory实例来反射自定义的ViewModel对象，并将该ViewModel保存到Hashmap中，下次使用的时候就可以直接使用；如果找了ViewModel 就直接返回该ViewModel的对象。**

从上面ViewModel的工作原理可以得知：

	1、ViewModel 一旦创建好了，就会一直保存到当前界面控制器（Activity 、Fragment等）销毁时才会释放资源；
	
	2、不同的界面控制器，ViewModel 的对象时存在不同的Hashmap中的，他们也是不同的对象；局部单例；
	
	3、要做到全局单例ViewModel对象，可以将ViewModel放到Application中去；


​	

接下来从源码角度来分析一下原理：	

### 1、创建存储ViewModel的容器ViewModelStore对象

	在构建Activity的对象时，在其父类ComponentActivity.java中实现了接口ViewModelStoreOwner，在其实现方法中生成ViewModelStore对象

```java
//ComponentActivity.java
 public ViewModelStore getViewModelStore() {
        if (getApplication() == null) {
            throw new IllegalStateException("Your activity is not yet attached to the "
                    + "Application instance. You can't request ViewModel before onCreate call.");
        }
        if (mViewModelStore == null) {
            NonConfigurationInstances nc =
                    (NonConfigurationInstances) getLastNonConfigurationInstance();
            if (nc != null) {
                // Restore the ViewModelStore from NonConfigurationInstances
                mViewModelStore = nc.viewModelStore;
            }
            if (mViewModelStore == null) {
                mViewModelStore = new ViewModelStore();
            }
        }
        return mViewModelStore;
    }
```



### 2、ViewModelProvider(this)生成AndroidViewModelFactory工厂对象

```kotlin
//ViewModelProvider.kt
public constructor(
        owner: ViewModelStoreOwner
    ) : this(owner.viewModelStore, defaultFactory(owner))


//AndroidViewModelFactory的伴生对象
public open class AndroidViewModelFactory(
        private val application: Application
    ) : NewInstanceFactory() {
        @Suppress("DocumentExceptions")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
                try {
                    modelClass.getConstructor(Application::class.java).newInstance(application)
                } catch (e: NoSuchMethodException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                }
            } else super.create(modelClass)
        }

        public companion object {
            internal fun defaultFactory(owner: ViewModelStoreOwner): Factory =
                if (owner is HasDefaultViewModelProviderFactory)
                    owner.defaultViewModelProviderFactory else instance

            internal const val DEFAULT_KEY = "androidx.lifecycle.ViewModelProvider.DefaultKey"

            private var sInstance: AndroidViewModelFactory? = null

            /**
             * Retrieve a singleton instance of AndroidViewModelFactory.
             *
             * @param application an application to pass in [AndroidViewModel]
             * @return A valid [AndroidViewModelFactory]
             */
            @JvmStatic
            public fun getInstance(application: Application): AndroidViewModelFactory {
                if (sInstance == null) {
                  //**** 生成全局的ViewModelFactory对象
                    sInstance = AndroidViewModelFactory(application)
                }
                return sInstance!!
            }
        }
    }

```



### 3、生成自定义的ViewModel实例

```kotlin
//ViewModelProvider.kt
public open operator fun <T : ViewModel> get(key: String, modelClass: Class<T>): T {
        var viewModel = store[key]
        if (modelClass.isInstance(viewModel)) {
            (factory as? OnRequeryFactory)?.onRequery(viewModel)
            return viewModel as T
        } else {
            @Suppress("ControlFlowWithEmptyBody")
            if (viewModel != null) {
                // TODO: log a warning.
            }
        }
        viewModel = if (factory is KeyedFactory) {
            factory.create(key, modelClass)
        } else {
          	//此处进入上一步创建好的AndroidViewModelFactory的create()方法中去
            factory.create(modelClass)
        }
        store.put(key, viewModel)
        return viewModel
    }


//AndroidViewModelFactory 类
@Suppress("DocumentExceptions")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
                try {
                  //1.第一步获取自定义ViewModel的构造器，第二部构造viewmodel的实例
                    modelClass.getConstructor(Application::class.java).newInstance(application)
                } catch (e: NoSuchMethodException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                }
            } else super.create(modelClass)
        }
    
    
```



### 4、ViewModel 观察界面控制器的生命周期

		在界面控制器的构造函数中，就添加了对生命周期的观察者，而当观察者收到当前的界面控制器的生命周期是Lifecycle.Event.ON_DESTROY时，就会将mViewModelStore对象map中所有保存的viewModel清理掉，这样来达到释放资源。
	
		这里只处理了ON_DESTROY的生命周期状态，那么也就说明了在ViewModel对象实例创建成功后，不管界面控制器（如Activity）的生命周期（除ON_DESTROY外）如何发生变化，ViewModel都不会被清理掉。


​		

```java
//ComponentActivity.java
public ComponentActivity() {
        Lifecycle lifecycle = getLifecycle();
        //noinspection ConstantConditions
        if (lifecycle == null) {
            throw new IllegalStateException("getLifecycle() returned null in ComponentActivity's "
                    + "constructor. Please make sure you are lazily constructing your Lifecycle "
                    + "in the first call to getLifecycle() rather than relying on field "
                    + "initialization.");
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source,
                        @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_STOP) {
                        Window window = getWindow();
                        final View decor = window != null ? window.peekDecorView() : null;
                        if (decor != null) {
                            decor.cancelPendingInputEvents();
                        }
                    }
                }
            });
        }
        getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source,
                    @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (!isChangingConfigurations()) {
                        getViewModelStore().clear();
                    }
                }
            }
        });

        if (19 <= SDK_INT && SDK_INT <= 23) {
            getLifecycle().addObserver(new ImmLeaksCleaner(this));
        }
    }
```



### 5、存储ViewModel的数据结构

```java
//ViewModelStore.java
public class ViewModelStore {

    private final HashMap<String, ViewModel> mMap = new HashMap<>();

    final void put(String key, ViewModel viewModel) {
        ViewModel oldViewModel = mMap.put(key, viewModel);
        if (oldViewModel != null) {
            oldViewModel.onCleared();
        }
    }

    final ViewModel get(String key) {
        return mMap.get(key);
    }

    Set<String> keys() {
        return new HashSet<>(mMap.keySet());
    }

    /**
     *  Clears internal storage and notifies ViewModels that they are no longer used.
     */
    public final void clear() {
        for (ViewModel vm : mMap.values()) {
            vm.clear();
        }
        mMap.clear();
    }
}


```



### 6、Hashmap存放ViewModel的key的生成规则

		从这里看出来ViewModel对应key的唯一性

```kotlin
//ViewModelProvider.kt

internal const val DEFAULT_KEY = "androidx.lifecycle.ViewModelProvider.DefaultKey"

public open operator fun <T : ViewModel> get(modelClass: Class<T>): T {
  
  		//这里需要注意下这个canonicalName是个什么东西
        val canonicalName = modelClass.canonicalName
            ?: throw IllegalArgumentException("Local and anonymous classes can not be ViewModels")
        return get("$DEFAULT_KEY:$canonicalName", modelClass)
    }


//Class.java 
public String getCanonicalName() {
        if (isArray()) {
            String canonicalName = getComponentType().getCanonicalName();
            if (canonicalName != null)
                return canonicalName + "[]";
            else
                return null;
        }
        if (isLocalOrAnonymousClass())
            return null;
        Class<?> enclosingClass = getEnclosingClass();
        if (enclosingClass == null) { // top level class
            return getName();
        } else {
            String enclosingName = enclosingClass.getCanonicalName();
            if (enclosingName == null)
                return null;
            return enclosingName + "." + getSimpleName();
        }
    }

```



## 四、总结



ViewModel工作原理的核心技术点：

			观察者模式、工程模式、反射、Hashmap数据结构



       ViewModel在MVVM架构模型中，与DataBinding结合使用，会让你有起飞的感觉。后续会进一步加深使用。本篇仅以学会使用、了解原理为重点。
