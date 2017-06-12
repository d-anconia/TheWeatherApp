package gorchakov.danil.theweatherapp;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;


public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    Weather weather = new Weather();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Moscow,Ru");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());
                String newCity = cityPreference.getCity();

                renderWeatherData(newCity);
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.textCity);
        temp = (TextView) findViewById(R.id.tempText);
        iconView = (ImageView) findViewById(R.id.iconImage);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById(R.id.pressureText);
        sunrise = (TextView) findViewById(R.id.riseText);
        sunset = (TextView) findViewById(R.id.setText);
        updated = (TextView) findViewById(R.id.updateText);

        CityPreference cityPreference = new CityPreference(MainActivity.this);



        renderWeatherData(cityPreference.getCity());

    }

    public void renderWeatherData(String city){
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=metric"});
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);

        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadImage(strings[0]);
        }

        private Bitmap downloadImage(String code){
            final DefaultHttpClient client = new DefaultHttpClient();
            final HttpGet getRequest = new HttpGet(Utils.ICON_URL + code + ".png");

            try {
                HttpResponse response = client.execute(getRequest);
                final int statusCode = response.getStatusLine().getStatusCode();
                if(statusCode != HttpStatus.SC_OK){
                    Log.e("Download image", "Error:" + statusCode);
                    return null;
                }
                final HttpEntity entity = response.getEntity();
                if(entity != null){

                    InputStream inputStream = null;

                    inputStream = entity.getContent();

                    // decode contents from the stream
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class WeatherTask extends AsyncTask<String, Void , Weather> {

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            DateFormat df = DateFormat.getTimeInstance();
            String sunriseDate = df.format(new Date(weather.place.getSunrise()));
            String sunsetDate = df.format(new Date(weather.place.getSunset()));
            String update = df.format(new Date(weather.place.getLastUpdate()));

            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String tempFormat  = decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText("" + tempFormat + "°C");
            humidity.setText("Humidity:" + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressure:" + weather.currentCondition.getPressure() + "hPa");
            wind.setText("Wind:" + weather.wind.getSpeed() + "m/s");
            sunrise.setText("Sunrise:" +sunriseDate);
            sunset.setText("Sunset:" + sunsetDate);
            updated.setText("Last updated:" + update);
            description.setText("Condition:" + weather.currentCondition.getCondition() + "(" +
            weather.currentCondition.getDescription() + ")");
        }

        @Override
        protected Weather doInBackground(String... strings) {

            String data = ((new WeatherHttpClient().getWeatherData(strings[0])));

            weather.iconData = weather.currentCondition.getIcon();
            weather = JSONWeatherParser.getWeather(data);
            Log.v("Weather", weather.place.getCity());

            //new DownloadImageAsyncTask().execute(weather.iconData);

            return weather;
        }
    }
}
