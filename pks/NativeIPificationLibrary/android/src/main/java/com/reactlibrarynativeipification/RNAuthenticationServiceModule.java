package com.reactlibrarynativeipification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Promise;

import com.ipification.mobile.sdk.android.CellularService;
import com.ipification.mobile.sdk.android.callback.CellularCallback;
import com.ipification.mobile.sdk.android.exception.CellularException;
import com.ipification.mobile.sdk.android.request.AuthRequest;
import com.ipification.mobile.sdk.android.response.AuthResponse;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RNAuthenticationServiceModule extends ReactContextBaseJavaModule {
    private Context context;
    private String configFileName;
    RNAuthenticationServiceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "RNAuthenticationService";
    }

    @ReactMethod
    public void setAuthorizationServiceConfiguration(String fileName) {
        this.configFileName = fileName;
    }

    @ReactMethod
    public void doAuthorizationNoParam(final Callback callback) {
        Log.d("DEBUG", "doAuthorization");
        doAuthorization(new CellularCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse authResponse) {
                Log.d("DEBUG", "onSuccess " + authResponse.getCode());
                callback.invoke(null, authResponse.getCode(), authResponse.getState(), authResponse.getResponseData());
            }

            @Override
            public void onError(@NotNull CellularException e) {
                Log.d("DEBUG", "onError " + e.getErrorMessage());
                callback.invoke(e.getErrorMessage(), null);
            }
        });


    }
    @ReactMethod
    public void doAuthorization(ReadableMap params, final Callback callback) {
        Log.d("DEBUG", "checkCoverage");
        doAuthorizationWithParams(params, new CellularCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse authResponse) {
                Log.d("DEBUG", "onSuccess " + authResponse.getCode());
                callback.invoke(null, authResponse.getCode(), authResponse.getState(), authResponse.getResponseData());
            }

            @Override
            public void onError(CellularException e) {
                Log.d("DEBUG", "onError " + e.getErrorMessage());
                callback.invoke(e.getErrorMessage(), null);
            }
        });


    }
    @ReactMethod
    public void unregisterNetwork() {
        CellularService.Companion.unregisterNetwork(this.context);
    }

    private void doAuthorization(CellularCallback<AuthResponse> cb) {
        CellularService<AuthResponse> doAuthService = new CellularService<>(context);
        if(configFileName != null){
            doAuthService.setAuthorizationServiceConfiguration(configFileName);
        }
        doAuthService.performAuth(cb);
    }
    private void doAuthorizationWithParams(ReadableMap params,final CellularCallback<AuthResponse> cb) {
        CellularService<AuthResponse> doAuthService = new CellularService<>(context);
        AuthRequest.Builder authRequestBuilder = new AuthRequest.Builder();
        try {

            HashMap data = params.toHashMap();
            for(Object key : data.keySet()){
                Log.d("DEBUG","map: " + key + " "+data.get(key));
                if(!key.toString().equals("state") && !key.toString().equals("scope")){
                	authRequestBuilder.addQueryParam(key.toString(), data.get(key).toString());
                } else if(key.toString().equals("state")){
                	authRequestBuilder.setState(data.get(key).toString());
                } else if(key.toString().equals("scope")){
                	authRequestBuilder.setScope(data.get(key).toString());
                }               
            }
            AuthRequest authRequest = authRequestBuilder.build();
            doAuthService.performAuth(authRequest, cb);
        }catch(Exception e){
            cb.onError(new CellularException(e));
        }

    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String getConfigurationByNameSync(String name) {  
        CellularService<AuthResponse> doAuthService = new CellularService<>(context);
        if(configFileName != null){
            doAuthService.setAuthorizationServiceConfiguration(configFileName);
        }
        return doAuthService.getConfiguration(name);
    }
    @ReactMethod
    public void getConfigurationByName(String name, Promise p) { p.resolve(getConfigurationByNameSync(name)); }
}
