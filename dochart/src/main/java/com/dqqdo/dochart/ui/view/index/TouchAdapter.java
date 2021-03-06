package com.dqqdo.dochart.ui.view.index;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.dqqdo.dochart.util.LogUtil;


/**
 * touch事件 适配器
 * 作者：duqingquan
 * 时间：2017/7/31 11:05
 */
public class TouchAdapter {

    // 当前的策略对象
    private IndexStrategy indexStrategy;

    // 手势滑动过程中最小移动量
    private static final int minMoveNum = 10;
    // 用来判断滑动手势，记录本次手势的起始坐标
    private float lastX;
    // 用来判断点击手势，记录本地点击动作的落下坐标
    private float downX;
    private float downY;
    // 发生点击事件时点对象
    private PointF clickPoint;


    TouchAdapter(IndexStrategy strategy){
        indexStrategy = strategy;
    }


    interface TouchCallback{

        void onCandleIndexChanged(int startCandleIndex,int endCandleIndex);

        void onClick(PointF pointF);

        void onSelectChange(PointF pointF);
    }


    /**
     * 响应点击事件
     * @param event
     * @param viewModel
     * @param startCandleIndex
     * @param endCandleIndex
     * @param callback
     * @return
     */
    public boolean doTouchEvent(MotionEvent event,StockIndexView.ViewModel viewModel,int startCandleIndex,int endCandleIndex,TouchCallback callback){

        clickPoint = new PointF();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                // 判断左滑还是右滑
                lastX = event.getX();
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断左滑还是右滑
                float moveX = event.getX() - lastX;

                // 浏览模式
                if (viewModel == StockIndexView.ViewModel.SHOW_MODEL) {


                    if (moveX > minMoveNum) {

                        if (startCandleIndex <= 0) {
                            // 已经最大了。不做处理
                            startCandleIndex = 0;
                        } else {
                            startCandleIndex -= 1;
                            endCandleIndex -= 1;
                            lastX = event.getX();
                        }

                        callback.onCandleIndexChanged(startCandleIndex,endCandleIndex);


                    } else if (moveX < -minMoveNum) {
                        int dataSize;
                        if(indexStrategy.getData() == null){
                            dataSize = 0;
                        }else {
                            dataSize = indexStrategy.getData().size();
                        }


                        if (endCandleIndex >= dataSize) {
                            // 已经最大了。不做处理
                            endCandleIndex = dataSize;

                        } else {
                            startCandleIndex += 1;
                            endCandleIndex += 1;
                            lastX = event.getX();
                        }

                        callback.onCandleIndexChanged(startCandleIndex,endCandleIndex);

                    } else {
                        // 未形成有效动作
                    }

                } else {
                    // 选择模式
                    clickPoint.set(event.getX(), event.getY());
                    callback.onSelectChange(clickPoint);
                }


                break;
            case MotionEvent.ACTION_UP:

                // 抬起和落点是一个坐标，则认为是一次点击事件
                if (downX == event.getX() && downY == event.getY()) {
                    clickPoint.set(downX, downY);
                    callback.onClick(clickPoint);
                } else {
                    LogUtil.d("不是点击事件");
                }

                // 判断左滑还是右滑
                lastX = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                // 判断左滑还是右滑
                lastX = 0;
                break;
        }

        return true;
    }

}
