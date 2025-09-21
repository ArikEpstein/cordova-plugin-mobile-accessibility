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

import android.annotation.TargetApi;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Method;

@TargetApi(36)
public class BaklavaMobileAccessibilityHelper extends
    OreoMobileAccessibilityHelper {

    @Override
    public void initialize(MobileAccessibility mobileAccessibility) {
        super.initialize(mobileAccessibility);
    }

    @Override
    public double getTextZoom() {
        double zoom = 100;
        try {
            if (mView instanceof WebView) {
                WebView webView = (WebView) mView;
                WebSettings settings = webView.getSettings();
                zoom = settings.getTextZoom();
            } else {
                Method getSettings = mView.getClass().getMethod("getSettings");
                Object wSettings = getSettings.invoke(mView);
                Method getTextZoom = wSettings.getClass().getMethod("getTextZoom");
                zoom = Double.valueOf(getTextZoom.invoke(wSettings).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zoom;
    }

    @Override
    public void setTextZoom(double textZoom) {
        try {
            if (mView instanceof WebView) {
                WebView webView = (WebView) mView;
                WebSettings settings = webView.getSettings();
                settings.setTextZoom((int) textZoom);
            } else {
                Method getSettings = mView.getClass().getMethod("getSettings");
                Object wSettings = getSettings.invoke(mView);
                Method setTextZoom = wSettings.getClass().getMethod("setTextZoom", int.class);
                setTextZoom.invoke(wSettings, (int) textZoom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
