package com.example.sunamihttp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {


    public static final String USGS = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2012-01-01&endtime=2012-12-01&minmagnitude=6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sunamiAsyncTask sunamiAsyncTask = new sunamiAsyncTask();
        sunamiAsyncTask.execute();
    }

    //Update UI Method
    private void updateUI(Event sunami) {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(sunami.title);
        TextView date = (TextView) findViewById(R.id.time);
        date.setText(sunami.time);
        TextView alert = (TextView) findViewById(R.id.alert);
        alert.setText(sunami.alert);
    }


    private class sunamiAsyncTask extends AsyncTask<URL, Void, Event> {
        @Override
        protected Event doInBackground(URL... urls) {
            URL url = createURL(USGS);


            String JSONResponce = null;
            try {
                JSONResponce = makeHTTPConnection(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Event sunami = extractFeaturesfromJSON(JSONResponce);
            return sunami;
        }


        @Override
        protected void onPostExecute(Event sunami) {
            if (sunami == null) {
                return;
            }
             updateUI(sunami);
        }


        ///HTTP Connection establishmnt method
        public String makeHTTPConnection(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputstream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                inputstream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputstream);
            } catch (IOException e) {

            }

            return jsonResponse;
        }
        //HTTP Ends


        //readFromStram method used in HTTP req method
        private String readFromStream(InputStream inputstream) {
            StringBuilder output = new StringBuilder();
            if (inputstream != null) {
                InputStreamReader streamReader = new InputStreamReader(inputstream, Charset.forName("UTF-8"));
                BufferedReader bufferReader = new BufferedReader(streamReader);
                try {
                    String line = bufferReader.readLine();
                    while (line != null) {
                        output.append(line);
                        line = bufferReader.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return output.toString();
        }

        //Create URL method
        private URL createURL(String stringurl) {
            URL url = null;
            try {
                url = new URL(stringurl);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return url;
        }

        private Event extractFeaturesfromJSON(String jsonResponce) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonResponce);
                JSONArray featureArray = jsonObject.getJSONArray("Features");
                if (featureArray.length() > 0) {

                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject properties = firstFeature.getJSONObject("properties");


                    String title = properties.getString("title");
                    String time = properties.getString("time");
                    String alert = properties.getString("tsunami");

                    return new Event(title, time, alert);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }}

