package com.reactlibrarynativeipification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import java.util.HashMap;
import com.ipification.mobile.sdk.android.CellularService;
import com.ipification.mobile.sdk.android.callback.CellularCallback;
import com.ipification.mobile.sdk.android.exception.CellularException;
import com.ipification.mobile.sdk.android.response.CoverageResponse;

import org.jetbrains.annotations.NotNull;

public class RNCoverageServiceModule extends ReactContextBaseJavaModule {
    private final Context context;
    private String configFileName;
    RNCoverageServiceModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
    }

 
    @Override
    public String getName() {
        return "RNCoverageService";
    }

    @ReactMethod
    public void checkCoverage(final Callback callback) {
        Log.d("DEBUG", "library checkCoverage");
        checkCoverage(new CellularCallback<CoverageResponse>() {
            @Override
            public void onSuccess(CoverageResponse coverageResponse) {
                Log.d("DEBUG", "onSuccess " + coverageResponse.isAvailable());
                callback.invoke(null, coverageResponse.isAvailable(), coverageResponse.getOperatorCode());
            }

            @Override
            public void onError(CellularException e) {
                Log.d("DEBUG", "library onError " + e.getErrorMessage());
                callback.invoke(e.getErrorMessage(), null);
            }
        });


    }
    @ReactMethod
    public void checkCoverageWithPhoneNumber(ReadableMap params, final Callback callback) {
        Log.d("DEBUG", "library checkCoverage");
        String phoneNumber = "";
        try {

            HashMap data = params.toHashMap();
            for(Object key : data.keySet()){
                if(key.toString().equals("phoneNumber")){
                    phoneNumber = data.get(key).toString();
                }             
            }
            
        }catch(Exception e){
            callback.invoke(e.getMessage(), null);
            return;
        }

        if(phoneNumber == null || phoneNumber == ""){
            callback.invoke("phone number cannot be empty", null);
            return;
        }
        checkCoverage(phoneNumber, new CellularCallback<CoverageResponse>() {
            @Override
            public void onSuccess(CoverageResponse coverageResponse) {
                Log.d("DEBUG", "onSuccess " + coverageResponse.isAvailable());
                callback.invoke(null, coverageResponse.isAvailable(), coverageResponse.getOperatorCode());
            }

            @Override
            public void onError(CellularException e) {
                Log.d("DEBUG", "library onError " + e.getErrorMessage());
                callback.invoke(e.getErrorMessage(), null);
            }
        });


    }

    @ReactMethod
    public void unregisterNetwork() {
        CellularService.Companion.unregisterNetwork(this.context);
    }
    @ReactMethod
    public void setAuthorizationServiceConfiguration(String fileName) {
        this.configFileName = fileName;
    }


    private void checkCoverage(CellularCallback<CoverageResponse> callback) {
        CellularService<CoverageResponse> checkCoverageService = new CellularService<>(this.context);
        if(configFileName != null){
            checkCoverageService.setAuthorizationServiceConfiguration(configFileName);
        }
        
        checkCoverageService.checkCoverage(callback);
    }

    private void checkCoverage(String phoneNumber, CellularCallback<CoverageResponse> callback) {
        CellularService<CoverageResponse> checkCoverageService = new CellularService<>(this.context);
        if(configFileName != null){
            checkCoverageService.setAuthorizationServiceConfiguration(configFileName);
        }
        
        checkCoverageService.checkCoverage(phoneNumber, callback);
    }
    
    @ReactMethod(isBlockingSynchronousMethod = true)
    public String getConfigurationByNameSync(String name) {  
        CellularService<CoverageResponse> checkCoverageService = new CellularService<>(this.context);
        if(configFileName != null){
            checkCoverageService.setAuthorizationServiceConfiguration(configFileName);
        }
        return checkCoverageService.getConfiguration(name);
    }
    @ReactMethod
    public void getConfigurationByName(String name, Promise p) { p.resolve(getConfigurationByNameSync(name)); }
}
