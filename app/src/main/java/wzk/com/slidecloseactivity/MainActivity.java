package wzk.com.slidecloseactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    TextView goNextActivityTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setIsSlideClose(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goNextActivityTv = (TextView)findViewById(R.id.goNextActivityTv);
        goNextActivityTv.setText("跳转到下一个Activity");
        goNextActivityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
    }
}
