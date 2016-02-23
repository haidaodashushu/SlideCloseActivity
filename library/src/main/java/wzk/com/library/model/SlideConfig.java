package wzk.com.library.model;

import android.support.annotation.FloatRange;

import wzk.com.library.widget.SlideFrame;

/**
 * @author WangZhengkui on 2016-02-20 10:55
 */
public class SlideConfig {

    /**
     * 能否滑开的最小滑动速率
     */
    private int mMinVelocity = 2000;

    /**
     *ViewDragHelper的sensitivity
     */
    private float mSensitivity = 1.0f;

    /**
     * 背景view的默认透明度
     */
    private float mDimColor = 0.6f;
    /**
     * 是从哪个边界滑动关闭
     */
    private int mEdgeDirect = SlideEdgeDirect.EDGE_LEFT;

    /**
     * 是否只能从边界滑动,false则表示全屏滑动
     */
    private boolean mEdgeOnly = true;

    /**
     * 边界滑动的距离（从0~1,基数为view的宽度）
     */
    private float mEdgeSize = 0.18f;

    /**
     * 松开手指时的最小距离（从0~1，基数为view的宽度）
     */
    private float mDistanceForRelease = 0.4f;

    /**
     * 监听器
     */
    private SlideFrame.OnFrameSlideListener onFrameSlideListener;


    public int getMinVelocity() {
        return mMinVelocity;
    }

    public SlideConfig setMinVelocity(int mMinVelocity) {
        this.mMinVelocity = mMinVelocity;
        return this;
    }

    public float getSensitivity() {
        return mSensitivity;
    }

    public SlideConfig setSensitivity(float mSensitivity) {
        this.mSensitivity = mSensitivity;
        return this;
    }

    public float getDimColor() {
        return mDimColor;
    }

    public SlideConfig setDimColor(float mDimColor) {
        this.mDimColor = mDimColor;
        return this;
    }

    public int getEdgeDirect() {
        return mEdgeDirect;
    }

    public SlideConfig setEdgeDirect(int mEdgeDirect) {
        this.mEdgeDirect = mEdgeDirect;
        return this;
    }

    public boolean isEdgeOnly() {
        return mEdgeOnly;
    }

    public SlideConfig setEdgeOnly(boolean mEdgeOnly) {
        this.mEdgeOnly = mEdgeOnly;
        return this;
    }

    public float getEdgeSize(int screenWidth) {
        return mEdgeSize*screenWidth;
    }

    public SlideConfig setEdgeSize(float mEdgeSize) {
        this.mEdgeSize = mEdgeSize;
        return this;
    }


    public float getDistanceForRelease() {
        return mDistanceForRelease;
    }

    public SlideConfig setDistanceForRelease(float mDistanceForRelease) {
        this.mDistanceForRelease = mDistanceForRelease;
        return this;
    }

    public SlideFrame.OnFrameSlideListener getOnFrameSlideListener() {
        return onFrameSlideListener;
    }

    public SlideConfig setOnFrameSlideListener(SlideFrame.OnFrameSlideListener onFrameSlideListener) {
        this.onFrameSlideListener = onFrameSlideListener;
        return this;
    }

    public static class Builder {
        private SlideConfig config;
        public Builder() {
            config = new SlideConfig();
        }
        public Builder setMinVelocity(int mMinVelocity) {
            config.mMinVelocity = mMinVelocity;
            return this;
        }

        public Builder setSensitivity(float sensitivity) {
            config.mSensitivity = sensitivity;
            return this;
        }
        public Builder setEdgeDirect(int mEdgeFlags) {
            config.mEdgeDirect = mEdgeFlags;
            return this;
        }
        public Builder setEdgeOnly(boolean mEdgeOnly) {
            config.mEdgeOnly = mEdgeOnly;
            return this;
        }
        public Builder setEdgeSize(@FloatRange(from = 0f, to = 1f) float edgeSize){
            config.mEdgeSize = edgeSize;
            return this;
        }
        public Builder setDistanceForRelease(float mDistanceForRelease) {
            config.mDistanceForRelease = mDistanceForRelease;
            return this;
        }
        public Builder setDimColor(float mDimColor) {
            config.mDimColor = mDimColor;
            return this;
        }
        public Builder setOnFrameSlideListener(SlideFrame.OnFrameSlideListener onFrameSlideListener) {
            config.onFrameSlideListener = onFrameSlideListener;
            return this;
        }
        public SlideConfig build() {
            return config;
        }
    }
}
