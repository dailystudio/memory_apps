package com.dailystudio.memory.where.locationapi;

import android.content.Context;

import com.dailystudio.app.utils.DeviceInfoUtils;
import com.dailystudio.development.Logger;

/**
 * Created by nan on 2015/3/31.
 */
public class LocationApiManager {

    private static AbsLocationApi sApiImpl = null;

    public static final synchronized AbsLocationApi getDefaultApi(Context context) {
        if (sApiImpl == null) {
            sApiImpl = createApiImpl(context);
        }

        return sApiImpl;
    }

    private static AbsLocationApi createApiImpl(Context context) {
        if (context == null) {
            return null;
        }

        AbsLocationApi apiImpl = null;

        String countryCode = DeviceInfoUtils.getRegisteredNetwork(context);
        if ("cn".equals(countryCode)) {
            Logger.debug("In China: use BAIDU api");
            apiImpl = new BaiduLocationApi(context);
        } else {
            Logger.debug("rest of world: use Google api");
            apiImpl = new GoogleLocationApi(context);
        }


        return apiImpl;
    }

}
