package wzk.com.slidecloseactivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;

import wzk.com.library.SlideClose;
import wzk.com.library.model.SlideConfig;
import wzk.com.library.model.SlideEdgeDirect;
import wzk.com.library.widget.SlideFrame;

public class BaseActivity extends AppCompatActivity {
    private boolean isSlideClose = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.addActivity(this);
        if (isSlideClose) {
            initSlideClose();
        }
    }

    public void initSlideClose() {
        final View childAt0;
        Activity beforeActivity = Config.getBeforeLastActivity();
        if (beforeActivity != null) {
            ViewGroup decorView = (ViewGroup) beforeActivity.getWindow().getDecorView();
            childAt0 = decorView.getChildAt(0);
        } else {
            childAt0 = null;
        }
        SlideConfig config = new SlideConfig.Builder()
                .setSensitivity(1)
                .setEdgeDirect(SlideEdgeDirect.EDGE_TOP|SlideEdgeDirect.EDGE_LEFT|SlideEdgeDirect.EDGE_RIGHT)
                .setEdgeOnly(false)
                .setDistanceForRelease(0.4f)
                .setMinVelocity(2000)
                .setOnFrameSlideListener(new SlideFrame.OnSimpleFrameSlideListener(){
                    @Override
                    public void onOpened() {
                        super.onOpened();
                        finish();
                        overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onStateChanged(int state) {
                        super.onStateChanged(state);
                        switch (state) {
                            case ViewDragHelper.STATE_IDLE:
                                if (childAt0 != null) {
                                    ViewHelper.setTranslationX(childAt0, 0);
                                    ViewHelper.setTranslationY(childAt0, 0);
                                }
                                break;
                        }
                    }

                    @Override
                    public void onSlideChange(int mEdgeDrag,float percent) {
                        super.onSlideChange(mEdgeDrag, percent);
//                        Log.i("BaseActivity", "mEdgeDrag = "+mEdgeDrag+",percent = "+percent);
                        if (childAt0 != null) {
                            //这里改变了前一个view的位置，记得在滑动结束后将view复位
                            switch (mEdgeDrag) {
                                case SlideEdgeDirect.EDGE_LEFT:
                                    ViewHelper.setTranslationX(childAt0, -300 * (1 - percent));
                                    break;
                                case SlideEdgeDirect.EDGE_TOP:
                                    ViewHelper.setTranslationY(childAt0, -300 * (1 - percent));
                                    break;
                                case SlideEdgeDirect.EDGE_RIGHT:
                                    ViewHelper.setTranslationX(childAt0, 300 * (1 - percent));
                                    break;
                                case SlideEdgeDirect.EDGE_BOTTOM:
                                    ViewHelper.setTranslationY(childAt0, 300 * (1 - percent));
                                    break;
                            }
                        }
                    }
                }).build();
        SlideClose.initSlideClose(this, config);
    }

    public boolean isSlideClose() {
        return isSlideClose;
    }

    public BaseActivity setIsSlideClose(boolean isSlideClose) {
        this.isSlideClose = isSlideClose;
        return this;
    }

    @Override
    protected void onDestroy() {
        Config.removeActivity(this);
        super.onDestroy();
    }
}
