# TimeTask
 TimeTask是一个轻量简洁的定时任务队列框架,专注处理多组任务分发工作
TimeTask内部的实现时基于AlarmManager+广播，在任务与系统api中间做了较好的封装

![](./img.png)

简单来说满足以下应用场景：
- 1.当你需要为任务定时启动和结束
- 2.你有多组任务,时间线上可能存在重叠的情况


目前线上正式环境的使用：
- 1.电视机顶盒媒体分发
- 2.android大屏幕广告机任务轮播


## 引入

根gradle上添加
```
	repositories {
			...
			maven { url 'https://jitpack.io' }
		}
```

```	
dependencies {
    compile 'com.github.BolexLiu:TimeTask:1.1'
}

```
	
## 简单使用


1.定义一个Task为你的任务对象，注意基类Task对象已经包含了任务的启动时间和结束时间

```java
    class  MyTask extends Task {
        //// TODO: 这里可以放置你自己的资源,务必继承Task对象
        String name;
    }

```

2.定义一个任务接收器
```java
   TimeHandler<MyTask> timeHandler = new TimeHandler<MyTask>() {
        @Override
        public void exeTask(MyTask mTask) {
               //准时执行
              // 一般来说，在exeTask方法中处理你的逻辑就好可以，过期和未来的都不需要关注 
        }

        @Override
        public void overdueTask(MyTask mTask) {
                 ///已过期的任务
        }

        @Override
        public void futureTask(MyTask mTask) {
              //未来将要执行的任务
        }
    };

```

3.定义一个任务分发器，并添加接收器
```java
 
        TimeTask<MyTask> myTaskTimeTask = new TimeTask<>(MainActivity.this,ACTION); // 创建一个任务处理器
        myTaskTimeTask.addHandler(timeHandler); //添加时间回掉
```


4.配置你的任务时间间隔，（启动时间，结束时间）
```java
    private List<MyTask> creatTasks() {
        return  new ArrayList<MyTask>() {{
            MyTask BobTask = new MyTask();
                        //******测试demo请务必修改时间******
                      BobTask.setStarTime(dataOne("2017-11-08 21:57:00"));   //当前时间
                      BobTask.setEndTime(dataOne("2017-11-08 21:57:05"));  //5秒后结束
                      BobTask.name="Bob";
                      add(BobTask);

                      MyTask benTask = new MyTask();
                      benTask.setStarTime(dataOne("2017-11-08 21:57:10")); //10秒开始
                      benTask.setEndTime(dataOne("2017-11-08 21:57:15")); //15秒后结束
                      benTask.name="Ben";
                      add(benTask);
        }};
    }
```

5.添加你的任务队列，跑起来.
```java
        
        myTaskTimeTask.setTasks(creatTasks());//创建时间任务资源 把资源放进去处理
        myTaskTimeTask.startLooperTask();//  启动

```

这样下来，当调用 myTaskTimeTask.startLooperTask()后，会先分发给timeHandler名称为Bob的任务。
随后10秒分发Ben名称的任务。 任务处理器会根据我们配置的启动时间和结束时间进行分发工作。


完整代码参考app中的列子。



## Api


**TimeTask**
- TimeTask(Context mContext,String actionName);//初始化
- setTasks(List<T> mES);//设置任务列表
- addHandler(TimeHandler<T> mTH);//添加任务监听器
- startLooperTask();//启动任务
- stopLooper();//停止任务
- spotsTask(List<T> mSpotsTask);//插播任务
- onColse();//关闭 防止内存泄漏

代码中已有详细注释,代码不是很复杂看原理读最好了。

## 注意:

- 1.务必确保你的任务队列中的任务时已经按照时间排序的。
- 2.务必使用泛型继承Task任务。
- 3.如果你需要用到多组TimeTask，要保证actionName不要重复，就是自己给取一个名字。


原理解析地址：![动手写Android内的计划任务定时框架](http://dajipai.cc/archives/a9dda310.html)

