package com.bolex.timetask.lib.timed;

/**
 * Created by 香脆的大鸡排 on 2017/11/2.
 */

public interface TimeHandler<T extends Task> {
    void exeTask(T mTask);//当前执行
    void overdueTask(T mTask);//已过期
    void futureTask(T mTask);//未来预约
}
