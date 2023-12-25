package com.demo.example.PictureFrames;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.demo.example.R;


public class PermissionActivity extends Activity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(1024);
        requestWindowFeature(1);
        setContentView(R.layout.activity_permission);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if(Permission_Utility.checkPermission1(this, this, true)){
                startActivity(new Intent(this, StartActivity.class));
                finish();
            }
            return;
        }

        if (Permission_Utility.checkPermission(this, this, true)) {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 123) {
            if (iArr.length > 0 && iArr[0] == 0 && iArr[1] == 0) {
                startActivity(new Intent(this, StartActivity.class));
                finish();
                return;
            }
            Permission_Utility.checkPermission(getApplicationContext(), this, false);
        }

        if (i == 1231) {
            if (iArr.length >= 0 && iArr[0] == 0) {
                startActivity(new Intent(this, StartActivity.class));
                finish();
                return;
            }




            Permission_Utility.checkPermission1(getApplicationContext(), this, false);
        }
    }
}
