/**
 * FileName: PermissionUtils
 * Author: shiwenliang
 * Date: 2021/9/26 15:30
 * Description:
 */
package com.leon.jetpack.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static final int PERMISSION_REQUEST = 100;
    private Activity context;
    private static PermissionUtils mInstance;

    private PermissionUtils(Activity activity) {
        context = activity;
    }

    public static PermissionUtils with(Activity activity) {
        synchronized (PermissionUtils.class) {
            if (mInstance == null) {
                synchronized (PermissionUtils.class) {
                    mInstance = new PermissionUtils(activity);
                }
            }
        }
        return mInstance;
    }


    /**
     * 权限检查
     *
     * @return 是否全部被允许
     */
    private boolean checkPermissions(String[] permissions) {
        String[] neededPermissions = permissions;
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(context, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    /**
     * 权限申请
     */
    public void requestPermissions(String[] permissions, PermissionGrantedCallBack...callBacks) {
        if (permissions != null && permissions.length > 0) {
            if (!checkPermissions(permissions)) {
                ActivityCompat.requestPermissions(context, permissions, PERMISSION_REQUEST);
            } else {
                if (callBacks != null && callBacks.length > 0) {
                    callBacks[0].onGranted();
                }
            }
        } else {
            if (callBacks != null && callBacks.length > 0) {
                callBacks[0].onGrantedError("请传入获取的权限");
            }
        }
    }


    public interface PermissionGrantedCallBack {
        void onGranted();

        void onGrantedError(String error);
    }
}
