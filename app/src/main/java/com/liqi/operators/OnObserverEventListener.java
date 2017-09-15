package com.liqi.operators;

/**
 * 被观察者行动事件
 *
 * @param <V> 传输过来处理值类型
 * @param <T> 处理结果值类型
 *            <p>
 *            Created by LiQi on 2017/9/6.
 */

public interface OnObserverEventListener<V, T> {

    T onObserverEvent(V transferValue);
}
