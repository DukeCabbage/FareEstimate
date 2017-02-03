package dispatch.digital.fareestimator.googleApi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;


/**
 * This interceptor will alter the request url, and append authentication information onto the url.
 * Use in conjunction with Retrofit, add onto the OkHttpClient used by the Retrofit object
 * <p>
 * Three types of authentication methods:
 * 1. NONE, not alter anything
 * 2. API_KEY, append an api key
 * 3. CLIENT_ID, append client id provided by M4B account, then generate an encrypted digital signature
 * (also includes a channel parameter for tracking)
 * <p>
 * See Google Maps API doc: https://developers.google.com/maps/documentation/geocoding/get-api-key#premium-auth
 **/

// TODO: This is directly ported from zoro with premium option is disabled
public class GoogleMapsAPIAuthInterceptor implements Interceptor {

    // Free api key
    // https://console.developers.google.com/apis/credentials/key/3?project=taxilimo-android
    private static final String API_KEY = "AIzaSyC-QCjP9MPW7j_Lu22NeIIlBDs5VJx5vxU";

    // Premium api key
    // https://console.developers.google.com/apis/credentials/key/6?project=api-project-464473895279
//    private static final String PREMIUM_API_KEY = "AIzaSyDz1nfM_Y8cUNJNbo-0kECHGmbqyaTDrpI";

//    private static final String CHANNEL = BuildConfig.DEBUG ? "Zoro-Android-Development" : "Zoro-Android-Production";

    private AuthMethod mAuthMethod;

    public GoogleMapsAPIAuthInterceptor(AuthMethod authMethod) {
        this.mAuthMethod = authMethod;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        Request request = builder.build();
        final String oldUrl = request.url().toString();
        String newUrl = oldUrl;
        Timber.v("Original url: %s", oldUrl);
        switch (mAuthMethod) {
            case API_KEY:
                Timber.v("Authenticate with api key: %s", API_KEY);
                newUrl = oldUrl + "&key=" + API_KEY;
                break;
//            case PREMIUM_API_KEY:
//                Timber.v("Authenticate with PREMIUM_API_KEY: %s", PREMIUM_API_KEY);
//                newUrl = oldUrl + "&key=" + PREMIUM_API_KEY;
//                break;
//            case CLIENT_ID:
//                try {
//                    // Append client id
//                    newUrl += "&client=" + UrlSigner.CLIENT_ID;
//                    newUrl += "&channel=" + CHANNEL;
//                    Timber.v("Authenticate with client id: %s", UrlSigner.CLIENT_ID);
//                    UrlSigner signer = new UrlSigner();
//                    // Append signature
//                    newUrl += signer.signUrl(newUrl);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    newUrl = oldUrl;
//                    Timber.v("Signing with client id failed, use original url");
//                }
//                break;
            default:
                Timber.v("No authentication method chosen");
        }

        Request newRequest = builder.url(newUrl).build();
        Timber.v("Authenticated url: %s", newRequest.url().toString());
        return chain.proceed(newRequest);
    }

    public enum AuthMethod {
        NONE, API_KEY, CLIENT_ID, PREMIUM_API_KEY
    }
}