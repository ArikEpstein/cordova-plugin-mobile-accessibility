/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

package com.phonegap.plugin.mobileaccessibility;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.webkit.WebView;

import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class provides information on the status of native accessibility services to JavaScript.
 */
public class MobileAccessibility extends CordovaPlugin {
    private AbstractMobileAccessibilityHelper mMobileAccessibilityHelper;
    private CallbackContext mCallbackContext = null;
    private float mFontScale = 1;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mMobileAccessibilityHelper = new KitKatMobileAccessibilityHelper();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMobileAccessibilityHelper = new JellyBeanMobileAccessibilityHelper();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mMobileAccessibilityHelper = new IceCreamSandwichMobileAccessibilityHelper();
        } else {
            mMobileAccessibilityHelper = new DonutMobileAccessibilityHelper();
        }
        mMobileAccessibilityHelper.initialize(this);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if(action.equals("getTextZoom")) {
                getTextZoom(callbackContext);
                return true;
            } else if(action.equals("setTextZoom")) {
                if (args.length() > 0) {
                    double textZoom = args.getDouble(0);
                    if (textZoom > 0) {
                        setTextZoom(textZoom, callbackContext);
                    }
                }
                return true;
            } else if(action.equals("updateTextZoom")) {
                updateTextZoom(callbackContext);
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
        }
        return false;
    }

    private void getTextZoom(final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final double textZoom = mMobileAccessibilityHelper.getTextZoom();
                if (callbackContext != null) {
                    callbackContext.success((int) textZoom);
                }
            }
        });
    }

    private void setTextZoom(final double textZoom, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mMobileAccessibilityHelper.setTextZoom(textZoom);
                if (callbackContext != null) {
                    callbackContext.success((int) mMobileAccessibilityHelper.getTextZoom());
                }
            }
        });
    }

    public void setTextZoom(final double textZoom) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mMobileAccessibilityHelper.setTextZoom(textZoom);
            }
        });
    }

    private void updateTextZoom(final CallbackContext callbackContext) {
        float fontScale = cordova.getActivity().getResources().getConfiguration().fontScale;
        if (fontScale != mFontScale) {
            mFontScale = fontScale;
        }
        final double textZoom = Math.round(mFontScale * 100);
        setTextZoom(textZoom, callbackContext);
    }

}
