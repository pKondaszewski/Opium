package com.example.op.utils;

import android.util.Base64;

import com.github.scribejava.apis.FitbitApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.nio.charset.StandardCharsets;

import lombok.Getter;

/**
 * Class that's provide OAuth2 authorization for application.
 *
 * @see <a href=https://dev.fitbit.com/build/reference/web-api/developer-guide/authorization/>https://dev.fitbit.com/build/reference/web-api/developer-guide/authorization/</a>
 */
@Getter
public class Authorization {

    private final String authorizationToken, authorizationUrl;

    public Authorization(String clientId, String clientSecret, String scopes, String redirectUrl) {
        authorizationToken = createFitbitUserToken(clientId, clientSecret);
        authorizationUrl = createAuthorizationUrl(clientId, clientSecret, scopes, redirectUrl);
    }

    /**
     * Creates the Fitbit user’s authorization token.
     *
     * @param clientId OAuth 2.0 Client ID from Fitbit application
     * @param clientSecret Client Secret from Fitbit application
     * @return created Fitbit user’s access token
     */
    private String createFitbitUserToken(String clientId, String clientSecret) {
        String clientIdAndSecret = clientId + ":" + clientSecret;
        byte[] data = clientIdAndSecret.getBytes(StandardCharsets.UTF_8);
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }

    /**
     *
     * @param clientId OAuth 2.0 Client ID from Fitbit application
     * @param clientSecret Client Secret from Fitbit application
     * @param scopes A space-delimited list of data collections requested by the application.
     * @param redirectUrl Redirect URL from Fitbit application
     * @return Generated url value for oauth authorization
     */
    private String createAuthorizationUrl(String clientId, String clientSecret,
                                          String scopes, String redirectUrl) {
        OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope(scopes)
                .callback(redirectUrl)
                .build(FitbitApi20.instance());
        return service.getAuthorizationUrl();
    }
}
