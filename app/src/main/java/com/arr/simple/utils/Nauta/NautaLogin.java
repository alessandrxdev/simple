package com.arr.simple.utils.Nauta;

import cu.suitetecsa.sdk.nauta.framekork.JsoupConnectPortalScraper;
import cu.suitetecsa.sdk.nauta.framekork.JsoupUserPortalScrapper;
import cu.suitetecsa.sdk.nauta.framekork.NautaApi;
import cu.suitetecsa.sdk.nauta.framework.model.NautaConnectInformation;
import cu.suitetecsa.sdk.nauta.framework.network.DefaultNautaSession;
import cu.suitetecsa.sdk.nauta.framework.network.JsoupConnectPortalCommunicator;
import cu.suitetecsa.sdk.nauta.framework.network.JsoupUserPortalCommunicator;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import kotlin.Pair;

public class NautaLogin {

    private NautaApi api;
    private String statusAccount;
    private String creditAccount;
    private String expireAccount;

    public NautaLogin(String status, String credit, String expire) {
        this.statusAccount = status;
        this.creditAccount = credit;
        this.expireAccount = expire;
    }

    public String getStatusAccount() {
        return statusAccount;
    }

    public String getCreditAccount() {
        return creditAccount;
    }

    public NautaLogin() {
        api =
                new NautaApi(
                        new JsoupConnectPortalCommunicator(new DefaultNautaSession()),
                        new JsoupConnectPortalScraper(),
                        new JsoupUserPortalCommunicator(new DefaultNautaSession()),
                        new JsoupUserPortalScrapper());
    }

    /*
     * NautaLogin tiene un constructor que inicializa la instancia de NautaApi con las dependencias existentes.
     * Luego, el método  connect crea un nuevo hilo y llama al método setCredencials dentro de ese hilo.
     */

    public void connect(String usuario, String password) {
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            try {
                                api.setCredentials(new Pair<>(usuario, password));
                                api.connect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
    }

    public void connectionInfo(String usuario, String password) {
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            try {
                                api.setCredentials(new Pair<>(usuario, password));
                                NautaConnectInformation info = api.getConnectInformation();
                                statusAccount = info.getAccountInfo().getAccountStatus();
                                creditAccount = info.getAccountInfo().getCredit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
    }
}
