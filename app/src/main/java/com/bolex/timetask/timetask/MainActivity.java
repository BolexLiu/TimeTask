package com.bolex.timetask.timetask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bolex.timetask.lib.timed.Task;
import com.bolex.timetask.lib.timed.TimeHandler;
import com.bolex.timetask.lib.timed.TimeTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TimeHandler<MyTask> timeHandler = new TimeHandler<MyTask>() {
        @Override
        public void exeTask(MyTask mTask) {
            Log.d("Task",mTask.name);

        }

        @Override
        public void overdueTask(MyTask mTask) {

        }

        @Override
        public void futureTask(MyTask mTask) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO: 2017/11/8  创建一个任务处理器
        TimeTask<MyTask> myTaskTimeTask = new TimeTask<>(MainActivity.this);

        // TODO: 2017/11/8   添加时间回掉
        myTaskTimeTask.addHandler(timeHandler);

        // TODO: 2017/11/8  创建时间任务资源
        List<MyTask> myTasks = creatTasks();

        // TODO: 2017/11/8 把资源放进去处理
        myTaskTimeTask.setTasks(myTasks);
        myTaskTimeTask.startLooperTask();

    }

    private List<MyTask> creatTasks() {
        ArrayList<MyTask> myTasks = new ArrayList<MyTask>() {};

        MyTask myTask1 = new MyTask();
        myTask1.setStarTime(System.currentTimeMillis());   //当前时间
        myTask1.setEndTime(System.currentTimeMillis()+5*1000);  //5秒后结束
        myTask1.name="Bob";
        myTasks.add(myTask1);

        MyTask myTask2 = new MyTask();
        myTask2.setStarTime(System.currentTimeMillis()+10*1000); //10秒开始
        myTask2.setEndTime(System.currentTimeMillis()+15*1000); //15秒后结束
        myTask2.name="Ben";
        myTasks.add(myTask2);
        return myTasks;

    }


    static class  MyTask extends Task {
        //// TODO: 2017/11/8 这里可以放置你自己的资源,务必继承Task对象
        String name;

    }




}
