package com.crypton.crypton_flutter_modules;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import io.flutter.plugin.common.MethodChannel;

import com.geetest.sdk.GT3ConfigBean;
import com.geetest.sdk.GT3ErrorBean;
import com.geetest.sdk.GT3GeetestUtils;
import com.geetest.sdk.GT3Listener;
import com.geetest.sdk.GT3LoadImageView;
import com.geetest.sdk.utils.GT3ServiceNode;

import org.json.JSONObject;

public class GeeTest {
    private Activity mActivity;
    private static final String TAG = "Geetest";

    private GT3GeetestUtils gt3GeetestUtils;
    private GT3ConfigBean gt3ConfigBean;

    String api1 = "https://www.geetest.com/demo/gt/register-slide";
    String api2 = "https://www.geetest.com/demo/gt/validate-slide";

    public GeeTest(Activity activity) {
        mActivity = activity;
    }

    public void geetestInit(String api1, String api2, final MethodChannel.Result result) {
        this.api1 = api1;
        this.api2 = api2;
        //TODO пожалуйста в onCreate Инициализируйте метод, чтобы получить достаточно данных о жестах, чтобы гарантировать успешность первого раунда проверки.
        gt3GeetestUtils = new GT3GeetestUtils(mActivity);
        // Конфигурация GT3ConfigBean Файл, также может вызываться в startCustomFlow Предварительная обработка метода
        gt3ConfigBean = new GT3ConfigBean();
        // Установить режим проверки, 1: bind, 2: unbind
        gt3ConfigBean.setPattern(1);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.mActivity);
        // Установите, будет ли клавиша возврата DismissDialog, дефолт Dismiss
        gt3ConfigBean.setUnCanceledOnTouchKeyCodeBack(preferences.getBoolean("settings_switch_key_back", true));
        // Укажите, следует ли щелкать серую область DismissDialog, дефолт Dismiss
        gt3ConfigBean.setCanceledOnTouchOutside(preferences.getBoolean("settings_switch_background", true));
        gt3ConfigBean.setReleaseLog(preferences.getBoolean("settings_switch_release_log", true));
        // Установите язык, если он нулевой, используйте Activity язык по умолчанию
        String language = preferences.getString("settings_language_input", null);
        if (TextUtils.isEmpty(language)) {
            language = preferences.getString("settings_language", null);
        }
        gt3ConfigBean.setLang("default".equals(language) ? null : language);
        // Настройка загрузки WebView сверхурочная работа, Ед. изм ms, дефолт 10000, Только и WebView Истекло время загрузки статических файлов，Исключая предыдущие https просить
        gt3ConfigBean.setTimeout(Integer.parseInt(preferences.getString("settings_timeout_load_web", "60000")));
        // настраивать WebView внутренний H5 Время запроса страницы истекло, Ед. изм ms, default 10000
        gt3ConfigBean.setWebviewTimeout(Integer.parseInt(preferences.getString("settings_timeout_h5", "60000")));
        // Установите узел кластера службы проверки, по умолчанию - китайский узел, для использования других узлов требуется соответствующая конфигурация, в противном случае проверка не может быть использована.
        String node = preferences.getString("settings_node", "default");
        switch (node) {
            case "na":
                gt3ConfigBean.setGt3ServiceNode(GT3ServiceNode.NODE_NORTH_AMERICA);
                break;
            case "ng":
                gt3ConfigBean.setGt3ServiceNode(GT3ServiceNode.NODE_NORTH_GOOGLE);
                break;
            case "ipv6":
                gt3ConfigBean.setGt3ServiceNode(GT3ServiceNode.NODE_IPV6);
                break;
            default:
                gt3ConfigBean.setGt3ServiceNode(GT3ServiceNode.NODE_CHINA);
        }
        // Настроить индивидуальный LoadingView
        GT3LoadImageView gt3LoadImageView = new GT3LoadImageView(this.mActivity);
        gt3LoadImageView.setLoadViewWidth(48); // Единица dp
        gt3LoadImageView.setLoadViewHeight(48); // Единица dp
        gt3ConfigBean.setLoadImageView(gt3LoadImageView);
        int radius = preferences.getInt("settings_switch_corners_radius", 0);
        gt3ConfigBean.setCorners(Math.abs(radius));
        gt3ConfigBean.setDialogOffsetY(radius);
        // Настроить прослушиватель обратного вызова
        gt3ConfigBean.setListener(new GT3Listener() {

            /**
             * Код подтверждения загружен
             * @param duration Такая информация, как время загрузки и версия, в формате json.
             */
            @Override
            public void onDialogReady(String duration) {
                Log.e(TAG, "GT3BaseListener-->onDialogReady-->" + duration);

            }

            /**
             * Обратный вызов результата графической проверки
             * @param code 1 нормально 0 не удалось
             */
            @Override
            public void onReceiveCaptchaCode(int code) {
                Log.e(TAG, "GT3BaseListener-->onReceiveCaptchaCode-->" + code);
            }

            /**
             * Пользовательский обратный вызов api2
             * @param result，api2 Запросить параметры загрузки
             */
            @Override
            public void onDialogResult(String result) {
                Log.e(TAG, "GT3BaseListener-->onDialogResult-->" + result);
                // Включите настраиваемую логику api2
                new RequestAPI2().execute(result);
            }

            /**
             * Статистика, см. Документ доступа
             * @param
             */
            @Override
            public void onStatistics(String result) {
                Log.e(TAG, "GT3BaseListener-->onStatistics-->" + result);
            }

            /**
             * Код подтверждения закрыт
             * @param num 1 Нажмите кнопку закрытия проверочного кода, чтобы закрыть проверочный код, 2 щелкните экран, чтобы закрыть проверочный код, 3 нажмите кнопку возврата, чтобы закрыть проверочный код
             */
            @Override
            public void onClosed(int num) {
                Log.e(TAG, "GT3BaseListener-->onClosed-->" + num);
            }

            /**
             * Обратный вызов успешной проверки
             * @param
             */
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "GT3BaseListener-->onSuccess--> [" + result + "]");
            }

            /**
             * Обратный вызов при ошибке проверки
             * @param errorBean Номер версии, код ошибки, описание ошибки и другая информация
             */
            @Override
            public void onFailed(GT3ErrorBean errorBean) {
                Log.e(TAG, "GT3BaseListener-->onFailed-->" + errorBean.toString());
            }

            /**
             * Пользовательский обратный вызов api1
             */
            @Override
            public void onButtonClick() {
                new RequestAPI1().execute();
            }
        });
        gt3GeetestUtils.init(gt3ConfigBean);
        gt3GeetestUtils.startCustomFlow();
    }

    /**
     * просить api1
     */
    class RequestAPI1 extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonObject = null;
            try {
                String result = NetRequestUtils.requestGet(AddressUtils.getRegister(mActivity, api1));
                jsonObject = new JSONObject(result);
                Log.i(TAG, "doInBackground : " + jsonObject);
            } catch (Exception e) {
                Log.i(TAG, "doInBackground Error : " + e.toString());
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject params) {
//            params.get("challenge");
            Log.i(TAG, "onPostExecute: " + params);
            try {
                params.put("success", 1);
                params.put("new_captcha", true);
                // SDK Узнаваемый формат
                // {"success":1,"challenge":"06fbb267def3c3c9530d62aa2d56d018","gt":"019924a82c70bb123aae90d483087f94","new_captcha":true}
                // TODO Установить для возврата данных api1, даже если они равны нулю, они должны быть установлены, что было внутренне обработано SDK.
                gt3ConfigBean.setApi1Json(params);
            } catch (Exception e) {
                Log.i(TAG, "Error : " + e.toString());
                gt3ConfigBean.setApi1Json(null);
            }
            // 继续验证
            gt3GeetestUtils.getGeetest();
        }
    }

    /**
     * просить api2
     */
    class RequestAPI2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return NetRequestUtils.requestPostByForm(AddressUtils.getValidate(mActivity, api2), params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.optString("result");
                if (TextUtils.isEmpty(status)) {
                    status = jsonObject.optString("status");
                }
                if ("success".equals(status)) {
                    gt3GeetestUtils.showSuccessDialog();
                } else {
                    gt3GeetestUtils.showFailedDialog();
                }
            } catch (Exception e) {
                gt3GeetestUtils.showFailedDialog();
            }
        }
    }

    public void onDestroy() {
        // TODO Уничтожьте ресурсы, обязательно добавьте
        if (gt3GeetestUtils != null) {
            gt3GeetestUtils.destory();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged: " + newConfig.orientation);
        // TODO Переключение между горизонтальным и вертикальным экранами
        if (gt3GeetestUtils != null) {
            gt3GeetestUtils.changeDialogLayout();
        }
    }
}
