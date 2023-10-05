package com.arr.simple.log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.bugsend.BugSend;
import com.arr.simple.databinding.ActivityCrashBinding;

public class CrashActivity extends AppCompatActivity {
        
        private ActivityCrashBinding binding;
        private BugSend bugSend;
        private boolean delete = false;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                binding = ActivityCrashBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                

        // TODO: Crash Reporter
        StringBuilder builder = new StringBuilder();
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            builder.append("VERSION: ").append(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String versionName = builder.toString();
                
        // bug send 
        bugSend = new BugSend(this);
        binding.logText.setText(versionName + "\n" + bugSend.readError());
        
        binding.closeButton.setOnClickListener(view->{
                if(delete) {
                	bugSend.deleteStackTrace();
                    System.exit(0);
                }else{
                    delete = true;
                }
        });
                
        binding.reportButton.setOnClickListener(view->{
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"soporteapplify@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "ERROR/SIMPLE");
                intent.putExtra(Intent.EXTRA_TEXT, versionName + "\n" + bugSend.readError());
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:"));
                startActivity(Intent.createChooser(intent, "Send report"));
                bugSend.deleteStackTrace();
        });
        
}
        
        
  @Override
  protected void onDestroy() {
  super.onDestroy();
    bugSend.deleteStackTrace();
        
    }
}
