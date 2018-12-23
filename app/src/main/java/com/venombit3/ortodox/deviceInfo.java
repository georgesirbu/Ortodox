package com.venombit3.ortodox;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class deviceInfo {

    public String tokenCloudMessaging;
    public String idIstanzaInAppMessaging;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public deviceInfo() {
    }

    public deviceInfo(String tokenCloudMessaging, String idIstanzaInAppMessaging) {
        this.tokenCloudMessaging = tokenCloudMessaging;
        this.idIstanzaInAppMessaging = idIstanzaInAppMessaging;
    }

}
