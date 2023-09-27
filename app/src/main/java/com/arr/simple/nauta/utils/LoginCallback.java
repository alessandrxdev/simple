package com.arr.simple.nauta.utils;

import androidx.navigation.NavController;
import cu.suitetecsa.sdk.nauta.framework.model.NautaConnectInformation;

public interface LoginCallback {

    void navController(NavController navigation, NautaConnectInformation info);

    void handlerException(Exception e);
}
