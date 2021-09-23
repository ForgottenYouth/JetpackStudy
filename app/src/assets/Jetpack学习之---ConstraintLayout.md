# AndroidX学习笔记之---ConstraintLayout扁平布局



[官方文档](https://developer.android.google.cn/training/constraint-layout)

[API文档](https://developer.android.google.cn/reference/android/support/constraint/package-summary)

ConstraintLayout可以让您使用扁平的视图层次结构来创建复杂的大型布局，该布局与IOS开发中的xib文件的布局约束雷同。

**android app性能优化中的布局优化，尽可能的减少布局层次，使用include ,ViewSub ,Merge等标签来减少层次，提升绘制界面的速度。在AndroidX 提供的库中，提供了ConstraintLayout布局可以直接让用户通过扁平化的结构来进行布局，由此可见ConstraintLayout在UI绘制的效率上是比较高的。**

但是不一定是所有的布局都使用ConstraintLayout布局都会效率很高，比如一些简单的线性布局、相对布局等就没必须要使用ConstraintLayout来完成。



### 1、添加依赖

```groovy
 repositories {
        google()
    }


dependencies{
    implementation "androidx.constraintlayout:constraintlayout:2.1.0"
    // To use constraintlayout in compose
    implementation "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02"
}
```



### 2、添加ConstraintLayout的根布局

```
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/linearLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".constraintlayout.ConstraintLayoutMainActivity">

</androidx.constraintlayout.widget.ConstraintLayout>
```



### 3、基本属性

**1）相对位置属性**

表示当前的view相对于目标view的位置（是在目标view的左、上、右、下）

> ```xml
> <!--left 关系->
> app:layout_constraintStart_toStartOf
> app:layout_constraintStart_toEndOf 
> app:layout_constraintLeft_toLeftOf
> app:layout_constraintLeft_toRightOf
> 
> <!--top关系->
> app:layout_constraintTop_toTopOf
> app:layout_constraintTop_toBottomOf
> 
> <!--right 关系->
> app:layout_constraintRight_toLeftOf
> app:layout_constraintRight_toRightOf
> 
> <!--bottom 关系->
> app:layout_constraintBottom_toTopOf
> app:layout_constraintBottom_toBottomOf
> ```

如：

```xml
    <TextView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="视图1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:id="@+id/view1"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="视图2"
        app:layout_constraintStart_toEndOf="@+id/view1"
        app:layout_constraintTop_toTopOf="parent"
        android:textSize="20sp"
        android:id="@+id/view2"/>
```



**2）间隔margin: **

这就和LinearLayout 、RelativeLayout等间隔一样的。分别是指左上右下的外间距

> ```xml
> android:layout_marginStart
> android:layout_marginEnd
> android:layout_marginLeft
> android:layout_marginTop
> android:layout_marginRight
> android:layout_marginBottom
> ```

如：

```xml
 <TextView
        android:id="@+id/view1"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="#ff0000"
        android:padding="10dp"
        android:layout_marginRight="10dp"
        android:layout_marginLeft="10dp"
        android:text="视图1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/view2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="20dp"
        android:layout_marginTop="10dp"
        android:background="#ff0fff"
        android:padding="10dp"
        android:text="视图2"
        android:textSize="20sp"
        app:layout_constraintStart_toEndOf="@+id/view1"
        app:layout_constraintTop_toTopOf="parent" />
```



**3）goneMargin视图不可见时的间隔**

在实际工作中，当数据达到某种条件时，该view1显示，这时另外一个view2会设置与当前view的间距；当数据不满足条件时，该view1需要隐藏，这时就view2就会因为view1的隐藏，而不需要当前这个间距了。这时候用goneMargin就非常方便简单的解决了该问题。

> ```xml
> layout_goneMarginStart
> layout_goneMarginEnd
> layout_goneMarginLeft
> layout_goneMarginTop
> layout_goneMarginRight
> layout_goneMarginBottom
> ```

```xml
<TextView
        android:id="@+id/view1"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="#ff0000"
        android:padding="10dp"
        android:visibility="gone"
        android:layout_marginRight="10dp"
        android:layout_marginLeft="10dp"
        android:text="视图1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/view2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="20dp"
        android:layout_marginTop="10dp"
        android:background="#ff0fff"
        android:padding="10dp"
        android:text="视图2"
        android:textSize="20sp"
        app:layout_goneMarginLeft="0dp"
        app:layout_constraintStart_toEndOf="@+id/view1"
        app:layout_constraintTop_toTopOf="parent" />

<!--这里添加了goneMarginLeft 表示当左侧的视图不可见时，当前视图距离左侧的间距为0dp ,当如果可见时，距离左侧的间距为android:layout_marginLeft="20dp" -->
```



**4) 弧形位置**

弧形位置是指当前视图的中心点相对于目标视图的中心点在指定半径指定角度的位置来绘制当前的视图。

> 1 、layout_constraintCircle 指定目标视图
>
> 2 、layout_constraintCircleAngle 指定角度值0~360 ，在目标视图的正上方是0  ，向右依次加大
>
> 3、layout_constraintCircleRadius 弧形半径的大小
>
> 4、弧形位置的视图设置margin的间隔是无效的

```xml
    <TextView
        android:id="@+id/view3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#00ff00"
        android:padding="10dp"
        android:text="弧角位置的视图"
        app:layout_constraintCircle="@id/view2"
        app:layout_constraintCircleAngle="140"
        app:layout_constraintCircleRadius="100dp"
        tools:ignore="MissingConstraints" />
```



**5） 大小属性**

视图大小的三种值：

​	1、明确指定大小

​	2、wrap_content 这个需要测量才会得到其大小

​	3、0dp  这个值就相当于是match_constraint



相对可占布局空间百分百大小：

​	1、相对于值为0-1的float值，此时对应的width 或 height必须为0dp



视图自身的大小比例：

​	1、需要指定width 或height其中一个的值，这样未指定的会按照这个比例进行计算结果

​	2、此属性的在图片的展示上使用非常方便

> ```xml
> android:minWidth 
> android:minHeight 
> android:maxWidth 
> android:maxHeight 
> 
> <!--
> 关于宽高的值有如下三种情况：
> 1. 指定大小
> 2.wrap_content 这个需要计算他的大小
> 3.0dp  这个值就相当于是match_constraint
> -->
> android:layout_width
> android:layout_height
> 
> <!--百分百大小
> 1.值为0-1的float值
> -->
> app:layout_constraintWidth_percent
> app:layout_constraintHeight_percent
> 
> 
> <!--视图自己的宽高比例
> 1.需要指定width 或height其中一个的值，这样未指定的会按照这个比例进行计算出结果
> 2.
> -->
> app:layout_constraintDimensionRatio="1:1"
> 
> ```
>
> 官方推荐使用: 0dp



### 4、对齐方式

ConstraintLayout中对于多个视图左侧对齐或者右侧对齐等，可以在xml的design标签下进行操作，也可以使用相同方向的同一个目标视图来进行对齐。并不存在Align_Left等属性。



### 5、BaseLine文本基线对齐

当两个视图的高度或者宽度不一致，这时期望在水平或垂直方向上内容进行对齐，那么就使用layout_constraintBaseline_toBaselineOf 属性进行基线对齐，其实就是对齐文本的底部

```xml
 <TextView
        android:id="@+id/hello"
        android:layout_width="wrap_content"
        android:layout_height="30dp"
        android:text="hello"
        android:textColor="#00ff0f"
        app:layout_constraintBaseline_toBaselineOf="@+id/constraintlayout"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/constraintlayout"
        android:layout_width="0dp"
        android:layout_height="50dp"
        android:layout_marginLeft="20dp"
        android:text="constraintLayout"
        android:textColor="#ff0000"
        android:textSize="20sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/hello"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_goneMarginLeft="0dp" />
```





### 6、Guideline引导线约束

> 引导线是不会在屏幕上显示的，因为在确定了他的方向后，另外一个方向的值是0 ，所已不会显示，主要是用来区分布局区域的。
>
> orientation 属性指明分割线是横向还是纵向
>
> layout_constraintGuide_begin 指明分割线距离容器左（垂直线）、上（水平线）的距离
>
> layout_constraintGuide_end  指明分割线距离容器在右（垂直线）、下（水平线）的距离
>
> layout_constraintGuide_percent 指明分割线在纵向（水平线），横向（垂直线）的百分百位置

```xml
 <androidx.constraintlayout.widget.Guideline
        android:id="@+id/guideline"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layout_constraintGuide_begin="10dp"
        app:layout_constraintGuide_end="35dp"
        app:layout_constraintGuide_percent="0.5" />
```



### 7、Barrier屏障约束

屏障也是一条用户不可见的线，他会随着其所含视图的位置而移动，使用场景：

将一个视图限制到一组视图中，而不是限制到一个视图中，那么屏障约束就是最好的解决方案。

可以这样理解：**屏障将容器区域分割成独立的区域，容器内的视图发生变化，屏障会同时移动位置**

> barrierDirection: 说明屏障的位置（start ,top ,end ,bottom）
>
> constraint_referenced_ids:说明屏障包含的视图有哪些，用逗号隔开即可

如果屏障包含的视图出现了GONE的情况，那么屏障会自动添加一个屏障来替换GONE的视图；如果不用自动添加的屏障来占位，那么就需要给Barrier的属性barrierAllowsGoneWidgets设置为false ，默认值为true.

```xml
 <ImageView
        android:id="@+id/img2"
        android:layout_width="50dp"
        android:layout_height="0dp"
        android:src="@drawable/ic_person"
        app:layout_constraintDimensionRatio="1:1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />


    <ImageView
        android:id="@+id/img3"
        android:layout_width="100dp"
        android:layout_height="0dp"
        android:src="@drawable/ic_home"
        app:layout_constraintDimensionRatio="1:1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/img2" />

    <ImageView
        android:id="@+id/img5"
        android:layout_width="190dp"
        android:layout_height="0dp"
        android:src="@drawable/ic_square"
        app:layout_constraintDimensionRatio="1:1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/img4" />

    <androidx.constraintlayout.widget.Barrier
        android:id="@+id/barrier"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:barrierDirection="end"
        app:constraint_referenced_ids="img2,img3,img5" />


    <ImageView
        android:id="@+id/img4"
        android:layout_width="150dp"
        android:layout_height="0dp"
        android:src="@drawable/ic_square"
        app:layout_constraintDimensionRatio="1:1"
        app:layout_constraintStart_toEndOf="@id/barrier" />
```



### 8、Chains链

链条，是只多个widgets在某一方向（水平、垂直）上形成的一个链条。（如下面的示例）

链条的style 是有链表头来设置的（水平链的链头是最左侧的，垂直的链头是在最上面），通过layout_constraintHorizontal_chainStyle或者layout_constraintVertical_chainStyle 来进行设置，支持三种类型：

1) spread ：均匀分布元素并平分间隙，如果有一个widget的宽度是0dp ,那么该widget则会占用所有的间隙。

![spread](E:\简书文章\JetPack\constraintlayout\image-20210923104651880.png)

2) spread_inside：首尾元素不参与头尾间隙平分

![spread_inside](E:\简书文章\JetPack\constraintlayout\image-20210923104617436.png)

3) packed: 将链条的元素聚集在一起，会随着元素的偏移量来改变其他元素的位置

![packed](E:\简书文章\JetPack\constraintlayout\image-20210923104737682.png)

```xml
<TextView
        android:id="@+id/text1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#c0c0c0"
        android:padding="10dp"
        android:text="我是中国人"
        android:textColor="#ff0000"
        app:layout_constraintEnd_toStartOf="@+id/text2"
        app:layout_constraintHorizontal_chainStyle="spread_inside"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />


    <TextView
        android:id="@+id/text2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#0c0c0c"
        android:padding="10dp"
        android:text="我是中国人"
        android:textColor="#00ff00"
        app:layout_constraintEnd_toStartOf="@+id/text3"
        app:layout_constraintStart_toEndOf="@+id/text1"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/text3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#a1a000"
        android:padding="10dp"
        android:text="我是中国人"
        android:textColor="#0000ff"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/text2"
        app:layout_constraintTop_toTopOf="parent" />
```



### 9、Group 组合

组是将多个widgets进行组合，方便集中管理这些widgets的可见性。这样就不需要在外面包一层layout 来实现可见性控制。这样就做到了减少布局层次的效果。

如果出现多个组都包含其中一个或者多个相同的widget id时，这样这些widget的显示与否取决于在xml布局中声明的最后一个group的可见性。

如下：

group1 包含了button1 ,button2 ,  而group1是不可见的，那么button1 button2都是不可见的；

group2 包含了button2,button3 ,group2 是可见的，那么button2 ,button3 就都是可见的，

如上：button2 在group1 ,group2中都有包含，那么button2的可见性取决于group1,group2在xml中的声明的顺序，最后声明的是group2 ,所以button2最后由group2来决定可见性。

```xml
<Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="button1"
        android:textSize="18sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

<Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="10dp"
        android:text="button2"
        android:textSize="18sp"
        app:layout_constraintStart_toEndOf="@+id/button1"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/button3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="10dp"
        android:text="button3"
        android:textSize="18sp"
        app:layout_constraintStart_toEndOf="@+id/button2"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.constraintlayout.widget.Group
        android:id="@+id/group1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:constraint_referenced_ids="button1,button2" />

    <androidx.constraintlayout.widget.Group
        android:id="@+id/group2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:constraint_referenced_ids="button3,button2" />
```



### 10 、ConstraintSet约束集

ConstraintSet是一个约束集，是为了方便用户通过编写代码的方式来完成约束的。

这样使用不直观，代码量大，建议不使用该方式。

```java
ConstraintSet set = new ConstraintSet();//创建一个对象
set.clone(this, R.layout.activity_constraint_layout_main);//获取布局中的约束

//这里可以根据xml布局添加约束一下，通过api来完成

ConstraintLayout constraintLayout = findViewById(R.id.root);
set.applyTo(constraintLayout);//将约束设置到布局上去
```



### 总结

关于ConstraintLayout的一些使用技巧，还是要在实践中多使用，才能灵活掌握。本文仅是对其基本使用做简单的学习，都非常简单，不过这种平面化布局最好加注释，否则后续维护会很吃力。



