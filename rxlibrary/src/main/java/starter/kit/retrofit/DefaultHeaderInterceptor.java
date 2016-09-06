package starter.kit.retrofit;

import android.text.TextUtils;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import starter.kit.util.Strings;
import support.ui.app.SupportApp;
import support.ui.utilities.AppInfo;

/**
 * @author <a href="mailto:smartydroid.com@gmail.com">Smartydroid</a>
 */
public class DefaultHeaderInterceptor implements HeaderInterceptor {

  String mToken;
  /**
   * example: application/vnd.xxx.v1+json
   */
  String mApiVersionAccept;

  public DefaultHeaderInterceptor(String token, String apiAccept) {
    mToken = token;
    mApiVersionAccept = apiAccept;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    Headers.Builder builder = new Headers.Builder();
    final AppInfo appInfo = SupportApp.appInfo();

    builder.add("Content-Encoding", "gzip")
        .add("X-Client-Build", String.valueOf(appInfo.versionCode))
        .add("X-Client-Version", appInfo.version)
        .add("X-Client", appInfo.deviceId)
        .add("X-Language-Code", appInfo.languageCode)
        .add("X-Client-Type", "android");

    final String channel = appInfo.channel;
    if (! TextUtils.isEmpty(channel)) {
      builder.add("X-Client-Channel", channel);
    }

    if (!Strings.isBlank(mToken)) {
      builder.add("Authorization", "Bearer " + mToken);
    }

    if (!Strings.isBlank(mApiVersionAccept)) {
      builder.add("Accept", mApiVersionAccept);
    }

    Request compressedRequest = originalRequest
        .newBuilder()
        .headers(builder.build())
        .build();

    return chain.proceed(compressedRequest);
  }
}
