package com.arr.simple.nauta.utils;

import androidx.navigation.NavController;
import cu.suitetecsa.sdk.nauta.domain.model.NautaUser;

public interface PortalCallback {

    void handlerException(Exception e);
    void portalResult(NavController nav, NautaUser result);
}
