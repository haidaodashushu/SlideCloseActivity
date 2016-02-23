package wzk.com.slidecloseactivity;

import android.app.Activity;

import java.util.LinkedList;

/**
 * @author WangZhengkui on 2016-02-22 11:00
 */
public class Config {
    public static LinkedList<Activity> activitiesTasks = new LinkedList<>();
    public static void addActivity(Activity activity) {
        activitiesTasks.add(activity);
    }
    public static void removeActivity(Activity activity) {
        activitiesTasks.remove(activity);
    }

    public static Activity getBeforeLastActivity() {
        if (activitiesTasks.size() < 2) {
            return null;
        }
        return activitiesTasks.get(activitiesTasks.size() - 2);
    }
}
