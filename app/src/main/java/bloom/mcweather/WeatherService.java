package bloom.mcweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("data/2.5/weather?units=metric")
    Call<WeatherResponse> getCurrentWeatherData(@Query("q") String city, @Query("APPID") String app_id);


}
