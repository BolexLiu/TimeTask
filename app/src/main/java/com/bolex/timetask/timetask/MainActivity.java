package com.bolex.timetask.timetask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bolex.timetask.lib.timed.Task;
import com.bolex.timetask.lib.timed.TimeHandler;
import com.bolex.timetask.lib.timed.TimeTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TimeHandler<MyTask> timeHandler = new TimeHandler<MyTask>() {
        @Override
        public void exeTask(MyTask mTask) {
            Log.d("Task", mTask.name);

        }

        @Override
        public void overdueTask(MyTask mTask) {

        }

        @Override
        public void futureTask(MyTask mTask) {

        }
    };
    final String ACTION = "timeTask.action";
    private TimeTask<MyTask> myTaskTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO: 2017/11/8  创建一个任务处理器
        myTaskTimeTask = new TimeTask<>(MainActivity.this, ACTION);

        // TODO: 2017/11/8   添加时间回掉
        myTaskTimeTask.addHandler(timeHandler);

        // TODO: 2017/11/8  创建时间任务资源
        List<MyTask> myTasks = creatTasks();

        // TODO: 2017/11/8 把资源放进去处理
        myTaskTimeTask.setTasks(myTasks);
        myTaskTimeTask.startLooperTask();

    }

    private List<MyTask> creatTasks() {
        return new ArrayList<MyTask>() {{
            MyTask BobTask = new MyTask();
            BobTask.setStarTime(dataOne("2017-11-08 21:57:00"));   //当前时间
            BobTask.setEndTime(dataOne("2017-11-08 21:57:05"));  //5秒后结束
            BobTask.name = "Bob";
            add(BobTask);

            MyTask benTask = new MyTask();
            benTask.setStarTime(dataOne("2017-11-08 21:57:10")); //10秒开始
            benTask.setEndTime(dataOne("2017-11-08 21:57:15")); //15秒后结束
            benTask.name = "Ben";
            add(benTask);
        }};
    }


    static class MyTask extends Task {
        //// TODO: 2017/11/8 这里可以放置你自己的资源,务必继承Task对象
        String name;

    }

    public static long dataOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Long.parseLong(times) * 1000;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTaskTimeTask.onColse();
    }
}
