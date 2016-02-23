package wzk.com.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import wzk.com.library.model.SlideConfig;
import wzk.com.library.model.SlideEdgeDirect;

/**
 * @author WangZhengkui on 2016-02-20 10:35
 */
public class SlideFrame extends FrameLayout {
    private static final int MIN_FLING_VELOCITY = 400; // dips per second
    View contentView;
    ViewDragHelper viewDragHelper;
    SlideConfig mConfig;
    OnFrameSlideListener mListener;
    /**
     * 当前手指的按下点是否是在边界
     */
    private boolean isTouchEdge;
    private int mScreenWidth;
    private int mScreenHeight;
    private View mDimView;

    /**
     * 因为ViewDragHelper的getEdgesTouched()方法确定的触摸边界范围(mEdgeSize)太小,而mEdgeSize的值又不能改变，所以我们自己在onTouchEvent的down事件来判断触摸边界。
     */
    private int mEdgeTouched;

    /**边界值，这个值=*/
    private double mEdgeSize;
    /**
     * 判断是否将要关闭还是打开，true表示为打开，false表示为关闭
     */
    private boolean stateWillTo;

    public SlideFrame(Context context) {
        super(context);
    }

    public SlideFrame(Context context, SlideConfig config, View decorView) {
        super(context);
        this.contentView = decorView;
        this.mConfig = (config == null ? new SlideConfig.Builder().build() : config);
        this.mListener = mConfig.getOnFrameSlideListener();
        //添加背景
        // Setup the dimmer view
        mDimView = new View(getContext());
        mDimView.setBackgroundColor(Color.BLACK);
        mDimView.getBackground().setAlpha((int) (mConfig.getDimColor() * 255));
        // Add the dimmer view to the layout
        addView(mDimView);
        //将decorView添加到该View
        addView(decorView);

        viewDragHelper = ViewDragHelper.create(this, mConfig.getSensitivity(), mCallBack);
        //设置最小速度
        final float density = getResources().getDisplayMetrics().density;
        float minVelocity = mConfig.getMinVelocity() * density;
        viewDragHelper.setMinVelocity(minVelocity);
        viewDragHelper.setEdgeTrackingEnabled(mConfig.getEdgeDirect());

        ViewGroupCompat.setMotionEventSplittingEnabled(this, false);


        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        post(new Runnable() {
            @Override
            public void run() {
                mScreenHeight = getHeight();
            }
        });

    }

    /**
     * Clamp Integer values to a given range
     *
     * @param value the value to clamp
     * @param min   the minimum value
     * @param max   the maximum value
     * @return the clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void applyDimColor(float percent) {
        mDimView.getBackground().setAlpha((int) (percent * mConfig.getDimColor() * 255));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //当前是否是触摸到边界
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mEdgeTouched = 0;
            mEdgeTouched = getEdgesTouched(ev.getX(),ev.getY());
            if (mConfig.isEdgeOnly()) {
                //如果没有触摸到边界，则isTouchEdge为false;
                isTouchEdge = mEdgeTouched != 0;
            } else {
                //表示全屏滑动
                isTouchEdge = true;
            }
        }

        boolean interceptForDrag;
        interceptForDrag = viewDragHelper.shouldInterceptTouchEvent(ev);
        return interceptForDrag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //根据viewDragHelper,继续完成动画效果
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {
        /**=0为水平方向 ，=1为竖直方向*，=-1则表示方向未确定*/
        int direct = -1;
        int left,top;
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //根据ID和触摸边界来判断是否可以捕获该child.
            if (child.getId() == contentView.getId() && isTouchEdge) {
                return true;
            }
            return false;
        }
        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
        }
        //该函数的返回值意味着CaptureView在X方向的位置
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            this.left = left;
            //如果触摸的边界不是在左边或者右边则返回0
            //全屏滑动则略过判断
            if (mConfig.isEdgeOnly()&&(((mEdgeTouched & SlideEdgeDirect.EDGE_LEFT) == 0) && (mEdgeTouched & SlideEdgeDirect.EDGE_RIGHT) == 0)) {
                return 0;
            }
            //一旦方向确定，则direct的值不再判断
            if (direct == -1) {
                direct = checkDirect(left,top);
            }
            if (direct == 0) {
                return left;
            }
            //如果是水平方向，则屏蔽竖直方向的滑动
            return 0;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mScreenWidth;
        }
        //该函数的返回值意味着CaptureView在Y方向的位置
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            this.top  = top;
            //如果触摸的边界不是在上边或者下边则返回0
            //如果支持全屏滑动则略过判断
            if (mConfig.isEdgeOnly()&&(((mEdgeTouched & SlideEdgeDirect.EDGE_TOP) == 0) && (mEdgeTouched & SlideEdgeDirect.EDGE_BOTTOM) == 0)) {
                return 0;
            }
            //一旦方向确定，则direct的值不再判断
            if (direct == -1) {
                direct = checkDirect(left,top);
            }
            if (direct == 1) {
                return top;
            }
            //如果是竖直方向，则屏蔽水平方向的滑动
            return 0;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mScreenHeight;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            int mMaxLength = 0;
            int distance = 0;
            int mEdgeDrag = 0;
            if (direct == 0) {
                mMaxLength = mScreenWidth;
                distance = Math.abs(left);
                //判断边界拖动
                if (left > 0) {
                    mEdgeDrag = SlideEdgeDirect.EDGE_LEFT;
                } else {
                    mEdgeDrag = SlideEdgeDirect.EDGE_RIGHT;
                }
            } else {
                mMaxLength = mScreenHeight;
                distance = Math.abs(top);
                //判断边界拖动
                if (top > 0) {
                    mEdgeDrag = SlideEdgeDirect.EDGE_TOP;
                } else {
                    mEdgeDrag = SlideEdgeDirect.EDGE_BOTTOM;
                }
            }
            float percent = distance * 1.0f / mMaxLength;
            if (mListener != null) mListener.onSlideChange(mEdgeDrag,percent);
            //控制dimView的透明度
            applyDimColor(1 - percent);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //根据滑动的结果来做边界检测
            if (releasedChild.getLeft() > 0) {
                releaseLeft(releasedChild, xvel);
            } else if (releasedChild.getLeft() < 0) {
                releaseRight(releasedChild, xvel);
            } else if (releasedChild.getTop() > 0) releaseTop(releasedChild, yvel);
              else releaseBottom(releasedChild, yvel);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mListener != null) mListener.onStateChanged(state);
            switch (state) {
                //终止时的状态
                case ViewDragHelper.STATE_IDLE:
                    if (mListener != null) {
                        //根据滑动结果回调
                        if (!stateWillTo) mListener.onClosed();
                        else mListener.onOpened();
                    }
                    //将direct设置为默认值
                    direct = -1;
                    break;
            }

        }
        /**
         * 判断滑动方向
         * @param left
         * @param top
         * @return
         */
        public int checkDirect(int left,int top) {
            //在50的范围内检测
            if (Math.abs(left) <= 50 && Math.abs(top) <= 50) {
                if (Math.abs(left) >= Math.abs(top)) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return direct;
            }
        }
    };

    /**
     * 当触摸边界为左边时的手指松开后的判断
     * @param releasedChild
     * @param vel
     * @return
     */
    public boolean releaseLeft(View releasedChild, float vel) {
        //判断在松开手指时的view滑动的位置
        int settleLeft = 0;
        int left = releasedChild.getLeft();
        int distanceForRelease = (int) (mConfig.getDistanceForRelease() * getWidth());
        if (vel > 0) {
            if (left > distanceForRelease) {
                settleLeft = mScreenWidth;
            } else if (vel > mConfig.getMinVelocity()) {
                settleLeft = mScreenWidth;
            }
        } else if (vel == 0) {
            if (left > distanceForRelease) {
                settleLeft = mScreenWidth;
            }
        }
        if (left != 0) {
            //启动动画，将view设置到判定的值
            //这里一旦启动，则意味着边界检测结束
            viewDragHelper.settleCapturedViewAt(settleLeft, releasedChild.getTop());
            invalidate();
        }

        return stateWillTo = settleLeft != 0;
    }

    /**
     * 当触摸边界为右边时的手指松开后的判断
     * @param releasedChild
     * @param vel
     * @return
     */
    public boolean releaseRight(View releasedChild, float vel) {
        //判断在松开手指时的view滑动的位置
        int settleRight = 0;
        //当触摸边界在右边时，getleft的值为负，则这里前面加负号。这样可以保证如果getLeft()>0时，将view复位而不会触发动画。
        //如果用Math.abs(releasedChild.getLeft())的话，当getLeft()>0时，也会触发动画从而将关闭Activty。
        int right = -releasedChild.getLeft();
        //根据配置的系数，来确定临界值的大小。
        int distanceForRelease = (int) (mConfig.getDistanceForRelease() * getWidth());
        if (vel > 0) {
            //大于临界值
            if (right > distanceForRelease) {
                settleRight = -mScreenWidth;
            } else if (vel > mConfig.getMinVelocity()) {
                settleRight = -mScreenWidth;
            }
        } else if (vel == 0) {
            if (right > distanceForRelease) {
                settleRight = -mScreenWidth;
            }
        }
        if (right != 0) {
            //启动动画，将view设置到判定的值，正为右方向，负为左方向。
            viewDragHelper.settleCapturedViewAt(settleRight, releasedChild.getTop());
            invalidate();
        }
        //确定在松开手指时，该View是关闭还是打开
       return stateWillTo = settleRight != 0;
    }

    /**
     * 当触摸边界为上边时的手指松开后的判断
     * @param releasedChild
     * @param yvel
     * @return
     */
    public boolean releaseTop(View releasedChild, float yvel) {
        //判断在松开手指时的view滑动的位置
        int top = releasedChild.getTop();
        int distanceForRelease = (int) (mConfig.getDistanceForRelease() * mScreenHeight);
        int settleTop = 0;
        if (yvel > 0) {
            if (top > distanceForRelease) {
                settleTop = mScreenHeight;
            } else if (yvel > mConfig.getMinVelocity()) {
                settleTop = mScreenHeight;
            }
        } else if (yvel == 0) {
            if (top > distanceForRelease) {
                settleTop = mScreenHeight;
            }
        }
        if (top != 0) {
            viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), settleTop);
            invalidate();
        }
        //确定在松开手指时，该View是关闭还是打开
        return stateWillTo = settleTop != 0;
    }

    /**
     * * 当触摸边界为下边时的手指松开后的判断
     * @param releasedChild
     * @param yvel
     * @return
     */
    public boolean releaseBottom(View releasedChild, float yvel) {
        //判断在松开手指时的view滑动的位置
        //注释请看releaseRight()方法
        int bottom = -releasedChild.getTop();
        int distanceForRelease = (int) (mConfig.getDistanceForRelease() * mScreenHeight);
        int settleBottom = 0;
        if (yvel > 0) {
            if (bottom > distanceForRelease) {
                settleBottom = -mScreenHeight;
            } else if (yvel > mConfig.getMinVelocity()) {
                settleBottom = -mScreenHeight;
            }
        } else if (yvel == 0) {
            if (bottom > distanceForRelease) {
                settleBottom = -mScreenHeight;
            }
        }
        if (bottom != 0) {
            //上为负，下为正
            viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), settleBottom);
            invalidate();
        }
        //确定在松开手指时，该View是关闭还是打开
        return stateWillTo = settleBottom != 0;
    }

    /**
     * 获取触摸的边界
     * @param x
     * @param y
     * @return
     */
    private int getEdgesTouched(float x, float y) {
        int result = 0;
        if (x < this.getLeft() + mConfig.getEdgeSize(mScreenWidth)) result |= SlideEdgeDirect.EDGE_LEFT;
        if (y < this.getTop() + mConfig.getEdgeSize(mScreenHeight)) result |= SlideEdgeDirect.EDGE_TOP;
        if (x > this.getRight() - mConfig.getEdgeSize(mScreenWidth)) result |= SlideEdgeDirect.EDGE_RIGHT;
        if (y > this.getBottom() - mConfig.getEdgeSize(mScreenHeight)) result |= SlideEdgeDirect.EDGE_BOTTOM;

        //将边界判断的值和我们指定的方向值做与运算，得出最终的边界值
        result &=mConfig.getEdgeDirect();
        return result;
    }
    /**
     * The panel sliding interface that gets called
     * whenever the panel is closed or opened
     */
    public interface OnFrameSlideListener {

        void onStateChanged(int state);
        void onClosed();

        void onOpened();

        /**
         *
         * @param mEdgeTouched 手指触摸的边界
         * @param percent
         */
        void onSlideChange(int mEdgeTouched,float percent);
    }

    public static class OnSimpleFrameSlideListener implements OnFrameSlideListener {

        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onClosed() {

        }

        @Override
        public void onOpened() {

        }

        @Override
        public void onSlideChange(int edgeDrag, float percent) {

        }

    }
}
