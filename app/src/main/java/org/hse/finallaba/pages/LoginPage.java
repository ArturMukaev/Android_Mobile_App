package org.hse.finallaba.pages;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.hse.finallaba.MainViewModel;
import org.hse.finallaba.R;
import org.hse.finallaba.entities.HumanEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginPage extends AppCompatActivity {
    // Элементы с экрана
    private Button enter;
    private Button goToRegistration;
    private EditText inputLogin;
    private EditText inputPassword;

    protected MainViewModel mainViewModel;
    private static final String TAG = "myLogs";
    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";
    protected Date currentTime;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setContentView(R.layout.authorization);
        inputLogin = findViewById(R.id.log_login);
        inputPassword = findViewById(R.id.log_pass);
        enter = findViewById(R.id.btn_log);
        goToRegistration = findViewById(R.id.btn_sign);
        enter.setOnClickListener(v -> login());
        goToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegister();
            }
        });
        getTime();
    }

    // Функция для входа в приложение
    private void login() {
        String loginText = inputLogin.getText().toString();
        String loginPassword = inputPassword.getText().toString();
        List<HumanEntity> humanList = mainViewModel.login(loginText, loginPassword);

        if (humanList.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Неверные данные для входа!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Intent intent = new Intent(this, MainPage.class);
        intent.putExtra(MainPage.ARG_LOGIN, humanList.get(0).id);
        intent.putExtra(MainPage.ARG_TEAM, humanList.get(0).teamId);
        startActivity(intent);
    }

    // Переход на страницу регистрации
    private void showRegister() {
        Intent intent = new Intent(this, RegistrationPage.class);
        startActivity(intent);
    }


    protected void getTime() {
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response)
                    throws IOException {
                parseResponse(response);
            }

            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getTime", e);
            }
        });
    }

    protected void initTime(Date dateTime) {
        if (dateTime == null) {
            return;
        }
        currentTime = dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag("ru"));
        mainViewModel.getCurrentTime().setValue(simpleDateFormat.format(currentTime));
    }

    private void parseResponse(Response response) {
        Gson gson = new Gson();
        ResponseBody body = response.body();
        try {
            if (body == null) {
                return;
            }
            String string = body.string();
            Log.d(TAG, string);
            TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
            String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date dateTime = simpleDateFormat.parse(currentTimeVal);
            runOnUiThread(() -> initTime(dateTime));

        } catch (Exception e) {
            Log.e(TAG, "Не пришел ответ", e);
        }
    }

    public class TimeResponse {

        @SerializedName("time_zone")
        private TimeZone timeZone;

        public TimeZone getTimeZone() {return timeZone; }

        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }
    }

    public class TimeZone {

        @SerializedName("current_time")
        private String currentTime;

        public String getCurrentTime() {return currentTime;}

        public void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }
    }
}
