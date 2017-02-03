package dispatch.digital.fareestimator.googleApi;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

class UrlSigner {
    // Google Maps API for Work
    final static String CLIENT_ID = "gme-digitaldispatch";
    private final static String CRYPTO_KEY = "cu-hCsv4xZUpySVcGrMJGHunzZw=";

    // This variable stores the binary key, which is computed from the string (Base64) key
    private final byte[] key;

    UrlSigner() throws IOException {
        this(CRYPTO_KEY);
    }

    private UrlSigner(String keyString) throws IOException {
        // Convert the key from 'web safe' base 64 to binary
        keyString = keyString.replace('-', '+');
        keyString = keyString.replace('_', '/');
//        System.out.println("Key: " + keyString);
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        this.key = Base64.decode(keyString, Base64.DEFAULT);
    }

    /**
     * See https://developers.google.com/maps/documentation/geocoding/get-api-key#digital-signature-premium
     **/
    String signUrl(final String urlString) throws NoSuchAlgorithmException,
            InvalidKeyException, UnsupportedEncodingException, URISyntaxException {

        String strippedUrlString = urlString.replace("https://maps.googleapis.com/", "/");
        Timber.v("URL Portion to Sign: %s", strippedUrlString);

        // Get an HMAC-SHA1 signing key from the raw key bytes
        SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

        // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(sha1Key);

        // compute the binary signature for the request
        byte[] sigBytes = mac.doFinal(strippedUrlString.getBytes());

        // base 64 encode the binary signature
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        String signature = Base64.encodeToString(sigBytes, Base64.DEFAULT);
        // convert the signature to 'web safe' base 64
        signature = signature.replace('+', '-');
        signature = signature.replace('/', '_');

        return "&signature=" + signature;
    }
}