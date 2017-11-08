package com.bolex.timetask.lib.timed;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by 香脆的大鸡排 on 2017/11/2.
 * TimeTask是一个定时任务框架,专注于处理复杂的定时任务分发
 * 博客地址:www.dajipai.cc
 */

public class TimeTask<T extends Task> {

    private List<TimeHandler> mTimeHandlers = new ArrayList<TimeHandler>();
    private static PendingIntent mPendingIntent;
    private List<T> mTasks= new ArrayList<T>();
    private  List<T> mTempTasks;
    String mActionName;
    private  boolean isSpotsTaskIng = false;
    private  int cursor = 0;
    private Context mContext;
    private TimeTaskReceiver receiver;

    /**
     *
     * @param mContext
     * @param actionName action不要重复
     */
    public TimeTask(Context mContext,@NonNull String actionName) {
       this.mContext=mContext;
       this.mActionName=actionName;
        initBreceiver(mContext);
    }

    private void initBreceiver(Context mContext) {
        receiver = new TimeTaskReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(mActionName);
        mContext.registerReceiver(receiver, filter);
    }


    public void setTasks(List<T> mES) {
        cursorInit();
        if (mTempTasks !=null){
            mTempTasks = mES;
        }else {
            this.mTasks = mES;
        }
    }

    /**
     * 任务计数归零
     */
    private void cursorInit() {
        cursor = 0;
    }

    /**
     * 添加任务监听
     * @param mTH
     * @return
     */
    public TimeTask addHandler(TimeHandler<T> mTH) {
        mTimeHandlers.add(mTH);
        return this;
    }

    /**
     * 开始任务
     */
    public void startLooperTask() {

        if (isSpotsTaskIng&&mTasks.size() == cursor){ //恢复普通任务
            recoveryTask();
            return;
        }

        if (mTasks.size() > cursor){
            T mTask = mTasks.get(cursor);
            long mNowtime = System.currentTimeMillis();
            //在当前区间内立即执行
            if (mTask.getStarTime() < mNowtime && mTask.getEndTime() > mNowtime) {
                for (TimeHandler mTimeHandler : mTimeHandlers) {
                    mTimeHandler.exeTask(mTask);
                }
                Log.d("TimeTask","推送cursor:" + cursor + "时间：" + new Date(mTask.getStarTime()));
            }
            //还未到来的消息 加入到定时任务
            if (mTask.getStarTime() > mNowtime && mTask.getEndTime() > mNowtime) {
                for (TimeHandler mTimeHandler : mTimeHandlers) {
                    mTimeHandler.futureTask(mTask);
                }
                Log.d("TimeTask","预约cursor:" + cursor + "时间：" + new Date(mTask.getStarTime()));
                configureAlarmManager(mTask.getStarTime());
                return;
            }
            //消息已过期
            if (mTask.getStarTime() < mNowtime && mTask.getEndTime() < mNowtime) {
                for (TimeHandler mTimeHandler : mTimeHandlers) {
                    mTimeHandler.overdueTask(mTask);
                }
                Log.d("TimeTask","过期cursor:" + cursor + "时间：" + new Date(mTask.getStarTime()));
            }
            cursor++;
            if (isSpotsTaskIng&&mTasks.size() == cursor){ //恢复普通任务
                configureAlarmManager(mTask.getEndTime());
                return;
            }
            startLooperTask();
        }
    }


    /**
     * 停止任务
     */
    public void stopLooper() {
        cancelAlarmManager();
    }

    /**
     * 装在定时任务
     * @param Time
     */
    private void configureAlarmManager(long Time) {
        AlarmManager manager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        PendingIntent pendIntent = getPendingIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Time, pendIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, Time, pendIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, Time, pendIntent);
        }
    }

    /**
     *  取消定时器
     */
    private void cancelAlarmManager() {
        AlarmManager manager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        manager.cancel(getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        if (mPendingIntent == null) {
            int requestCode = 0;
            Intent intent = new Intent();
            intent.setAction(mActionName);
            mPendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return mPendingIntent;
    }

    /**
     * 插播任务
     */
    public void spotsTask(List<T> mSpotsTask) {
        // 2017/10/16 暂停 任务分发
        isSpotsTaskIng = true;
        synchronized (mTasks) {
            if (mTempTasks == null&&mTasks!=null) {//没有发生过插播
                mTempTasks = new ArrayList<T>();
                for (T mTask : mTasks) {
                    mTempTasks.add(mTask);
                }
            }
            mTasks = mSpotsTask;
            //  2017/10/16 恢复 任务分发
            cancelAlarmManager();
            cursorInit();
            startLooperTask();
        }
    }

    /**
     * 恢复普通任务
     */
    private void recoveryTask() {
        synchronized (mTasks) {
            isSpotsTaskIng = false;
            if (mTempTasks != null) {//有发生过插播
                mTasks = mTempTasks;
                mTempTasks = null;
                cancelAlarmManager();
                cursorInit();
                startLooperTask();
            }
        }
    }

    public void onColse(){
        mContext.unregisterReceiver(receiver);
        mContext=null;
    }

    public  class TimeTaskReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            TimeTask.this.startLooperTask(); //预约下一个
        }
    }




}
