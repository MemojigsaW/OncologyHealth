package com.example.uhealth;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;


public class PermissionHelper {
    private Activity mActivity;
    private PermissionInterface mPermissionInterface;

    public PermissionHelper(@NonNull Activity activity, @NonNull PermissionInterface permissionInterface) {
        mActivity = activity;
        mPermissionInterface = permissionInterface;
    }


    public void requestPermissions(String[] permissions) {
        String[] deniedPermissions = PermissionUtil.getDeniedPermissions(mActivity, permissions);
        if (deniedPermissions != null && deniedPermissions.length > 0) {
            PermissionUtil.requestPermissions(mActivity, deniedPermissions, mPermissionInterface.getPermissionsRequestCode());
        } else {
            mPermissionInterface.requestPermissionsSuccess();
        }
    }


    public boolean requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == mPermissionInterface.getPermissionsRequestCode()) {
            boolean isAllGranted = true;//是否全部权限已授权
            for(int result : grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    isAllGranted = false;
                    break;
                }
            }
            if(isAllGranted){
                //已全部授权
                mPermissionInterface.requestPermissionsSuccess();
            }else{
                //权限有缺失
                mPermissionInterface.requestPermissionsFail();
            }
            return true;
        }
        return false;
    }
}
