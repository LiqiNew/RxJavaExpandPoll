package com.liqi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.liqi.extend.ObservableExpand;
import com.liqi.operators.OnObserverEventListener;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    int expandNumber, rxJavaNumber;
    private TextView mExpandPollText, mRxjavaPollText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExpandPollText = (TextView) findViewById(R.id.expand_poll_text);
        mRxjavaPollText = (TextView) findViewById(R.id.rxjava_poll_text);
        expandPoll("自定义扩展轮询");
        rxJavaPoll("RxJava原生轮询");
    }

    /**
     * 运行针对rxjava源码扩展的轮询
     *
     * @param transitionList 传输进被观察者行为数据
     */
    private void expandPoll(String transitionList) {
        ObservableExpand.intervalPolling(3000, 3000, TimeUnit.MILLISECONDS,
                //被观察者行为监听器 ->正在处理
                new OnObserverEventListener<String, String>() {
                    @Override
                    public String onObserverEvent(String transferValue) {
                        try {
                            //模拟耗时操作
                            Thread.sleep(3 * 1000);
                            expandNumber++;
                            transferValue = "扩展轮询次数：" + expandNumber + "\n传输进来的值：" + transferValue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //此处可以放置你要处理的数据或者逻辑。
                        return transferValue;
                    }
                })
                //指定被观察者行为监听器执行线程。传输对象进被观察者行为监听器
                .subscribeOn(Schedulers.io(), transitionList)
                //设置拦截器
                .takeUntil(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String untilData) {
                        //执行10次自动停止轮询，也可根据untilData对象值去判断是否停止轮询
                        if (expandNumber >= 10) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                //指定观察者触发监听器执行线程
                .observeOn(AndroidSchedulers.mainThread())
                //观察者触发监听器
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        String toString = mExpandPollText.getText().toString();
                        mExpandPollText.setText(s + "\n\n" + toString);
                    }
                });
    }

    /**
     * rxjava原生轮询
     *
     * @param transitionList 传输被转换的对象
     */
    private void rxJavaPoll(final String transitionList) {
        //创建轮询器
        Observable.interval(3000, 3000, TimeUnit.MILLISECONDS)
                //数据处理行动监听器--->>此处不可以线程切换
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        Log.e("RxJavaPoll", "map-->call--->Run");

                        try {
                            //模拟耗时操作
                            Thread.sleep(3 * 1000);
                            rxJavaNumber++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "RxJava轮询次数：" + rxJavaNumber + "\n传输进来的值：" + transitionList;
                    }
                })
                //轮询拦截器
                .takeUntil(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        //执行10次自动停止轮询
                        if (rxJavaNumber >= 10) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                //订阅者事件处理器线程切换
                .observeOn(AndroidSchedulers.mainThread())
                //订阅者事件处理监听器
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String transferValue) {
                        String toString = mRxjavaPollText.getText().toString();
                        mRxjavaPollText.setText(transferValue + "\n\n" + toString);
                    }
                });
    }
}
