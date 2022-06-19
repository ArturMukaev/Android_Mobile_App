package org.hse.finallaba.pages;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import org.hse.finallaba.BuildConfig;
import org.hse.finallaba.MainViewModel;
import org.hse.finallaba.R;
import org.hse.finallaba.entities.HumanEntity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfilePage extends AppCompatActivity {
    // Элементы с экрана
    private TextView sensorLight;
    private TextView when;
    private Button makePhoto;
    private Button save;
    private ImageView image;
    private EditText inputName;
    private EditText inputSurname;

    public static final Integer DEFAULT_ID = -1;
    private Integer humanId;
    public static final String ARG_TIME = "time";
    private SensorManager sensorManager;
    private Sensor light;
    private static final String TAG = "myLogs";
    private final int REQUEST_PERMISSION_CODE = 1;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String PERMISSION = Manifest.permission.CAMERA;
    private Uri photoUri;
    protected MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setContentView(R.layout.profile_page);

        sensorLight = findViewById(R.id.SensorLight);
        when = findViewById(R.id.when);
        makePhoto = findViewById(R.id.makePhoto);
        save = findViewById(R.id.save);
        image = findViewById(R.id.image1);
        inputName = findViewById(R.id.edit_name);
        inputSurname = findViewById(R.id.edit_surname);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        String time = getIntent().getStringExtra(ARG_TIME);
        humanId = getIntent().getIntExtra(MainPage.ARG_LOGIN, DEFAULT_ID);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        List<HumanEntity> humanList = mainViewModel.getHuman(humanId);

        inputName.setText(humanList.get(0).name);
        inputName.setText(humanList.get(0).surname);
        when.setText(pm.getValue("when", "Еще не было"));
        load();


        makePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.updateHuman(inputName.getText().toString(), inputSurname.getText().toString(), humanId);
                pm.saveValue("when", time);
                if (photoUri != null) {
                    pm.saveValue("photo", photoUri.toString());
                }
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Настройки сохранены!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void load(){
        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        String photo = pm.getValue("photo", "");
        Glide.with(this).load(photo).into(image);
    }



    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listenerLight, light, SensorManager.SENSOR_DELAY_NORMAL);
    }
    SensorEventListener listenerLight = new SensorEventListener() {
        @Override
        public final void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public final void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            sensorLight.setText(String.valueOf(lux));
//            ConstraintLayout myLayout = (ConstraintLayout) findViewById(R.id.my_layout);
//            myLayout.setBackgroundColor(0xFF00FF00);
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerLight);
    }

    private void checkPermission() {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, PERMISSION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)) {
                showExplanation("Нужно предоставить права",
                        "Для снятия фото нужно предоставить права на фото", PERMISSION, REQUEST_PERMISSION_CODE);
            } else {
                requestPermission(PERMISSION, REQUEST_PERMISSION_CODE);
            }
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(timeStamp, ".jpg", storageDir);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException exception) {
                Log.e(TAG, "Create file", exception);
            }
            if (photoFile != null) {
                photoUri = getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Start activity", e);
                }
            }
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Glide.with(this).load(photoUri).into(image);
    }


    public class PreferenceManager {
        private final static String PREFERENCE_FILE = "org.hse.android.file";

        private final SharedPreferences sharedPref;

        public PreferenceManager(Context context) {
            sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        }

        private void saveValue(String key, String value) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(key, value);
            editor.apply();
        }

        private String getValue(String key, String defaultValue) {
            return sharedPref.getString(key, defaultValue);
        }
    }
}
