package com.crypton.crypton_flutter_modules;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public final class NetRequestUtils {

    private static final String TAG = "NetRequestUtils";

    private NetRequestUtils() {
    }

    private static OkHttpClient okHttpClient;

    static {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new MyX509TrustManager()}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new PersistenceCookieJar())
                .sslSocketFactory(sslContext.getSocketFactory())
                .addNetworkInterceptor(logInterceptor)
                .build();
    }

    public static String requestPostByForm(String url, String param) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("t", System.currentTimeMillis() + "");
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody requestBody = RequestBody.create(mediaType, jsonToForm(param));
        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(urlBuilder.build())
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String requestGet(String urlString) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlString).newBuilder();
        urlBuilder.addQueryParameter("t", System.currentTimeMillis() + "");
        Request request = new Request.Builder().url(urlBuilder.build())
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String GEETEST_VALIDATE = "geetest_validate";
    private static final String GEETEST_SECCODE = "geetest_seccode";
    private static final String GEETEST_CHALLENGE = "geetest_challenge";

    private static String jsonToForm(String param) {
        try {
            JSONObject jsonObject = new JSONObject(param);
            String seccode = jsonObject.getString(GEETEST_SECCODE);
            String validate = jsonObject.getString(GEETEST_VALIDATE);
            String challenge = jsonObject.getString(GEETEST_CHALLENGE);
            return GEETEST_VALIDATE + "=" + validate + "&" + GEETEST_SECCODE + "=" + seccode + "&" + GEETEST_CHALLENGE + "=" + challenge;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            for (X509Certificate certificate : chain) {
                try {
                    certificate.checkValidity();
                } catch (CertificateExpiredException e) {
                    e.printStackTrace();
                } catch (CertificateNotYetValidException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] x509Certificates = new X509Certificate[0];
            return x509Certificates;
        }
    }

    private static class PersistenceCookieJar implements CookieJar {
        List<Cookie> cache = new ArrayList<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cache.addAll(cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> invalidCookies = new ArrayList<>();
            List<Cookie> validCookies = new ArrayList<>();
            for (Cookie cookie : cache) {
                if (cookie.expiresAt() < System.currentTimeMillis()) {
                    invalidCookies.add(cookie);
                } else if (cookie.matches(url)) {
                    validCookies.add(cookie);
                }
            }
            cache.removeAll(invalidCookies);
            return validCookies;
        }
    }

    private static class HttpLogger implements HttpLoggingInterceptor.Logger {

        @Override
        public void log(String message) {
            Log.d(TAG, "Logger: " + message);
        }
    }

}
