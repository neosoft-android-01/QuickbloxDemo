package com.webwerks.quickbloxdemo.chat.location;

import java.util.HashMap;
import java.util.List;

/**
 * Created by webwerks on 4/5/17.
 */

public interface OnNearByPlacesCallback {

    void onNearByPlacesReceived(List<HashMap<String, String>> list);
}
