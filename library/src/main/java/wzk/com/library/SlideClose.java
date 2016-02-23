package wzk.com.library;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import wzk.com.library.model.SlideConfig;
import wzk.com.library.widget.SlideFrame;

/**
 * @author WangZhengkui on 2016-02-20 10:30
 */
public class SlideClose {

    public static void initSlideClose(Activity currentActivity,SlideConfig config) {
        View decorView = currentActivity.getWindow().getDecorView();
        View contentView = ((ViewGroup)decorView).getChildAt(0);
        ((ViewGroup) decorView).removeView(contentView);

        SlideFrame slideFrame = new SlideFrame(currentActivity,config,contentView);
        slideFrame.setId(R.id.slide_frame);
        contentView.setId(R.id.slide_content);
        ((ViewGroup) decorView).addView(slideFrame, 0);
    }
}
