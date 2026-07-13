/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import Models.GoogleAccount;
import Models.GoogleTokenResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.fluent.Request;
import java.io.IOException;
import java.net.URLEncoder;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;

/**
 *
 * @author TranVTH
 */

public class GoogleLogin {

    public String buildAuthorizationUrl(String state) {
        StringBuilder url = new StringBuilder(Iconstant.GOOGLE_AUTH_URI)
                .append("?client_id=").append(encode(Iconstant.GOOGLE_CLIENT_ID))
                .append("&redirect_uri=").append(encode(Iconstant.GOOGLE_REDIRECT_URI))
                .append("&response_type=code")
                .append("&scope=").append(encode(Iconstant.GOOGLE_SCOPE))
                .append("&include_granted_scopes=true")
                .append("&prompt=select_account");
        if (state != null && !state.trim().isEmpty()) {
            url.append("&state=").append(encode(state));
        }
        return url.toString();
    }

    public GoogleTokenResponse getToken(String code) throws ClientProtocolException, IOException {
        String response = Request.Post(Iconstant.GOOGLE_LINK_GET_TOKEN)
                .bodyForm(
                        Form.form()
                                .add("client_id", Iconstant.GOOGLE_CLIENT_ID)
                                .add("client_secret", Iconstant.GOOGLE_CLIENT_SECRET)
                                .add("redirect_uri", Iconstant.GOOGLE_REDIRECT_URI)
                                .add("code", code)
                                .add("grant_type", Iconstant.GOOGLE_GRANT_TYPE)
                                .build()
                )
                .execute().returnContent().asString();
        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        if (jobj == null || !jobj.has("access_token")) {
            String error = jobj != null && jobj.has("error") ? jobj.get("error").getAsString() : "unknown_error";
            throw new IOException("Google token exchange failed: " + error);
        }
        return new Gson().fromJson(response, GoogleTokenResponse.class);
    }

    public GoogleAccount getUserInfo(final String accessToken) throws ClientProtocolException, IOException {
        String response = Request.Get(Iconstant.GOOGLE_LINK_GET_USER_INFO)
                .addHeader("Authorization", "Bearer " + accessToken)
                .execute()
                .returnContent()
                .asString();
        return new Gson().fromJson(response, GoogleAccount.class);
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception ex) {
            throw new IllegalStateException("UTF-8 is not supported on this JVM.", ex);
        }
    }
}
