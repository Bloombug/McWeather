package bloom.mcweather;


import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.ajts.androidmads.fontutils.FontUtils;

import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;

public class MainActivity extends AppCompatActivity {
    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "e512b557dd621662489f230647e94d83";
    public static String city;
    //public static String lon;
    public View mainView;
    public View root;
    public EditText firstEditLatitude;
    //public EditText secondEditLongitude;
    private TextView weatherData;

    public InputStream imageStream;
    public InputStream imageStream1;

    public ImageView weatherImage;
    public AssetManager manager;

    public Bitmap rainyBitmap;
    public Bitmap sunnyBitmap;

    /*
    @GET("data/2.5/weather?")
    Call<WeatherResponse> getCurrentWeatherData(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String app_id);
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstEditLatitude = (EditText) findViewById(R.id.firstEdit);

        weatherData = findViewById(R.id.textView);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentData();
            }
        });

        //Transparent StatusBar code: copy for other projects!!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public float convertFahrenheitToCelcius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    void getCurrentData() {

        city = firstEditLatitude.getText().toString();
        mainView = findViewById(R.id.mainScreen);
        root = mainView.getRootView();

        manager = getAssets();

        weatherImage = findViewById(R.id.weather_map_view);

        try {
            imageStream = manager.open("rainy.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        rainyBitmap = BitmapFactory.decodeStream(imageStream);


        try {
            imageStream1 = manager.open("sunny.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        sunnyBitmap = BitmapFactory.decodeStream(imageStream1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(city, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {

                    weatherData.setText("Loading data...");

                    if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    String stringBuilder = "City : " +
                            city +
                            "\n\n" +
                            "Country : " +
                            weatherResponse.sys.country +
                            "\n\n" +
                            "Temperature : " +
                            String.format("%.01f", Float.valueOf((convertFahrenheitToCelcius(weatherResponse.main.temp) / 10))) +
                            " ºC\n" +
                            "Temperature (Min) : " +
                            String.format("%.01f", Float.valueOf(convertFahrenheitToCelcius(weatherResponse.main.temp_min) / 10)) +
                            " ºC\n" +
                            "Temperature (Max) : " +
                            String.format("%.01f", Float.valueOf(convertFahrenheitToCelcius( weatherResponse.main.temp_max) / 10)) +
                            " ºC\n\n" +
                            "Humidity : " + weatherResponse.main.humidity +
                            " %\n\n";

                    if (weatherResponse.main.humidity >= 85) {
                        weatherData.setTextColor(Color.parseColor("#FFFFFF"));
                        weatherImage.setImageBitmap(rainyBitmap);
                    } else {
                        weatherData.setTextColor(Color.parseColor("#000000"));
                        weatherImage.setImageBitmap(sunnyBitmap);
                    }

                    weatherData.setText(stringBuilder);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                weatherData.setText(t.getMessage());
            }
        });
    }

}

