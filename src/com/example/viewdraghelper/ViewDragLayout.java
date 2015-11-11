/**
 * 
 */
package com.example.viewdraghelper;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author lsx
 *
 */
public class ViewDragLayout extends LinearLayout {

    private ViewDragHelper mDragHelper;
    private View mDragView;
    private View mAutoBackView;
    private View mEdgeTrackerView;
    private Point mAutoBackOriginPos = new Point();
    
    public ViewDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this,1.1f, new ViewDragHelper.Callback() {
            
            //如果返回ture则表示可以捕获该view，你可以根据传入的第一个view参数决定哪些可以捕获
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
              //mEdgeTrackerView禁止直接移动
                return child == mDragView || child == mAutoBackView;
            }

            /* 
             *可以在该方法中对child移动的边界进行控制，left , top 分别为即将移动到的位置
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            /* 
             * 
             */
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            ////手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel,
                    float yvel) {
                //mAutoBackView手指释放可以自动回去
                if (releasedChild == mAutoBackView) {
                    mDragHelper.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            //在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                mDragHelper.captureChildView(mEdgeTrackerView, pointerId);
            }
        });
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    //否应该拦截当前的事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    //处理事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        mDragView = this.getChildAt(0);
        mAutoBackView = this.getChildAt(1);
        mEdgeTrackerView = this.getChildAt(2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        
        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getRight();
    }

    @Override
    public void computeScroll() {
       if (mDragHelper.continueSettling(true)) {
           this.invalidate();
       }
    }

}
