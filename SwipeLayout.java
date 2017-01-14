package com.itheima.swipedelete97;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by lxj on 2017/1/4.
 */

public class SwipeLayout extends FrameLayout {

    private View content;
    private View delete;

    ViewDragHelper dragHelper;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        dragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content = getChildAt(0);
        delete = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        content.layout(0, 0, content.getMeasuredWidth(), content.getMeasuredHeight());
        int L = content.getRight();

        delete.layout(L, 0, L + delete.getMeasuredWidth(), delete.getMeasuredHeight());
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = dragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    float downX,downY;
    long downTime;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                //计算移动的距离
                float dx = moveX - downX;
                float dy = moveY - downY;
                //判断到底偏向于哪个方向
                if(Math.abs(dx)>Math.abs(dy)){
                    //说明是偏向水平方向，那么就认为用户想滑动条目，此时应该让listview不要拦截
                    requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_UP:
                //1.计算按下抬起的时间
                long duration = System.currentTimeMillis() - downTime;
                //2.计算按下抬起的距离
                float deltaX = event.getX() - downX;
                float deltaY = event.getY() - downY;
                float distance = (float) Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2));
                //3.如果duration小于500，并且distance小于8px
                ViewConfiguration viewCfg = ViewConfiguration.get(getContext());
                if(duration< viewCfg.getLongPressTimeout() && distance<viewCfg.getScaledTouchSlop()){
                    //满足了点击的条件
                    performClick();//作用就是执行OnClickListener的onClick方法
                }

                break;
        }

        dragHelper.processTouchEvent(event);

        return true;
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        /**
         * 鸡肋的方法
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //限制content
            if(child==content){
                if(left>0){
                    left = 0;
                }else if(left<-delete.getMeasuredWidth()){
                    left = -delete.getMeasuredWidth();
                }
            }else if(child==delete){
                //限制delete
                if(left>content.getMeasuredWidth()){
                    left = content.getMeasuredWidth();
                }else if(left<(content.getMeasuredWidth()-delete.getMeasuredWidth())){
                    left = (content.getMeasuredWidth()-delete.getMeasuredWidth());
                }
            }

            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //如果移动的是content，那么让delete伴随移动
            if(changedView==content){
//                int newLeft = delete.getLeft()+dx;
//                delete.layout(newLeft,0,newLeft+delete.getMeasuredWidth(),delete.getMeasuredHeight());

                ViewCompat.offsetLeftAndRight(delete,dx);
            }else if(changedView==delete){
                //让content进行伴随移动
                ViewCompat.offsetLeftAndRight(content,dx);
            }

            //回调接口的方法
            if(listener!=null){
                if(content.getLeft()==0){
                    listener.onClose(SwipeLayout.this);
                }else if(content.getLeft()==-delete.getMeasuredWidth()){
                    listener.onOpen(SwipeLayout.this);
                }
            }

        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(content.getLeft()>-delete.getMeasuredWidth()/2){
                //关闭
                closeLayout();
            }else {
                //打开
                openLayout();
            }

        }
    };

    /**
     * 打开
     */
    public void openLayout() {
        dragHelper.smoothSlideViewTo(content,-delete.getMeasuredWidth(),0);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 关闭
     */
    public void closeLayout() {
        dragHelper.smoothSlideViewTo(content,0,0);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private OnSwipeListener listener;
    public void setOnSwipeListener(OnSwipeListener listener){
        this.listener = listener;
    }
    public interface OnSwipeListener{
        void onOpen(SwipeLayout currentLayout);
        void onClose(SwipeLayout currentLayout);
    }
}
