package com.example.uhealth;

public interface PermissionInterface {


    int getPermissionsRequestCode();

    void requestPermissionsSuccess();

    void requestPermissionsFail();
}
