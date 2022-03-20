package com.dunn.instrument.crash.sample;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.dunn.instrument.crash.local.CrashHandlerListener;
import com.dunn.instrument.crash.local.NativeCrashMonitor;

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setOnClickListener(this);

        NativeCrashMonitor nativeCrashMonitor = new NativeCrashMonitor();
        nativeCrashMonitor.init(new CrashHandlerListener() {
            @Override
            public void onCrash(String threadName, Error error) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        NativeCrashMonitor.nativeCrash();
    }
}
