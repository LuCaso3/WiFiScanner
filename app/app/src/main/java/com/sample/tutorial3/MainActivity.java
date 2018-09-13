package com.sample.tutorial3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.os.AsyncTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * WIFI Scanner
 *
 * @author Seon
 *
 */
public class MainActivity extends Activity implements OnClickListener {

    //private ImageView image01;
    private static final String TAG = "WIFIScanner";
    private static String IP_ADDRESS = "10.20.253.103";
    private UsersAdapter mAdapter;
    private RecyclerView mRecylerView ;
    private String mJsonString;
    private ArrayList<Locatedata> mArrayList;
    int i=0;
    // WifiManager variable
    WifiManager wifimanager;
    // UI variable
    TextView textStatus;
    Button btnScanStart;
    Button btnScanlocate;
    Button btnScanStop;
    Button btnLocateStop;
    private int scanCount = 0;
    String text = "";
    String result = "";
    String serv_result = "";
    private List<ScanResult> mScanResult; // ScanResult List

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                getWIFIScanResult(); // get WIFISCanResult
                wifimanager.startScan(); // for refresh
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    private BroadcastReceiver LReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                getLocateScanResult(); // get WIFISCanResult
                wifimanager.startScan(); // for refresh
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    public void getWIFIScanResult() {

        mScanResult = wifimanager.getScanResults(); // ScanResult
        // Scan count
        String rssi1_name = "AP1";
        String rssi2_name = "AP2";
        String rssi3_name = "AP3";
        String rs1 = "";
        String rs2 = "";
        String rs3 = "";
        textStatus.setText("Scan count is \t" + ++scanCount + " times \n");

        textStatus.append("=======================================\n");
        for (int i = 0; i < mScanResult.size(); i++) {
            ScanResult result = mScanResult.get(i);
            textStatus.append((i + 1) + ". SSID : " + result.SSID
                    + "\t\t RSSI : " + result.level + " dBm\n");
            /* ssid = result.SSID.toString();
            bssid = result.BSSID.toString();*/

            if (result.SSID.toString().equals(rssi1_name)) {
                rs1 = String.valueOf(result.level);
            }
            if (result.SSID.toString().equals(rssi2_name)) {
                rs2 = String.valueOf(result.level);
            }
            if (result.SSID.toString().equals(rssi3_name)) {
                rs3 = String.valueOf(result.level);
            }
        }
        if (rs1.equals("")) rs1 = "X";
        if (rs2.equals("")) rs2 = "Y";
        if (rs3.equals("")) rs3 = "Z";
        // textStatus.append("1  : " + rs1 + " 2 : " + rs2 + " 3 : " + rs3 +"\n");
        InsertData task = new InsertData();
        task.execute("http://" + IP_ADDRESS + "/Project/php/insertDB.php", rs1, rs2, rs3);
        textStatus.append("=======================================\n");

    }

    public void getLocateScanResult() {

        mScanResult = wifimanager.getScanResults(); // ScanResult
        // Scan count
        String rssi1_name = "AP1";
        String rssi2_name = "AP2";
        String rssi3_name = "AP3";
        String rs1 = "";
        String rs2 = "";
        String rs3 = "";
        textStatus.setText("Scan count is \t" + ++scanCount + " times \n");

        textStatus.append("=======================================\n");
        for (int i = 0; i < mScanResult.size(); i++) {
            ScanResult result = mScanResult.get(i);
            textStatus.append((i + 1) + ". SSID : " + result.SSID
                    + "\t\t RSSI : " + result.level + " dBm\n");
            if (result.SSID.toString().equals(rssi1_name)) {
                rs1 = String.valueOf(result.level);
            }
            if (result.SSID.toString().equals(rssi2_name)) {
                rs2 = String.valueOf(result.level);
            }
            if (result.SSID.toString().equals(rssi3_name)) {
                rs3 = String.valueOf(result.level);
            }
        }

        if (rs1.equals("")) rs1 = "A";
        if (rs2.equals("")) rs2 = "B";
        if (rs3.equals("")) rs3 = "C";
        // textStatus.append("1  : " + rs1 + " 2 : " + rs2 + " 3 : " + rs3 +"\n");
        InsertData task = new InsertData();
        GetData task2 = new GetData();
        task.execute("http://" + IP_ADDRESS + "/Project/php/LocateDB.php", rs1, rs2, rs3);
        task2.execute("http://" + IP_ADDRESS + "/Project/php/getjson.php","");
        textStatus.append("=======================================\n");
        mRecylerView = (RecyclerView) findViewById(R.id.listView_main_list);
        mRecylerView.setLayoutManager(new LinearLayoutManager(this));

        mArrayList = new ArrayList<>();

        mAdapter = new UsersAdapter(this,mArrayList);
        mRecylerView.setAdapter(mAdapter);
    }

    public void initWIFIScan() {
        // init WIFISCAN
        scanCount = 0;
        text = "";
        final IntentFilter filter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        wifimanager.startScan();
        Log.d(TAG, "initWIFIScan()");
    }

    public void locWiFiScan() {
        scanCount = 0;
        text = "";
        final IntentFilter filter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(LReceiver, filter);
        wifimanager.startScan();
        Log.d(TAG, "locateWIFIScan()");
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //image01 = (imageView) findViewById()

        // Setup UI
        textStatus = (TextView) findViewById(R.id.textStatus);
        btnScanStart = (Button) findViewById(R.id.btnScanStart);
        btnScanlocate = (Button) findViewById(R.id.btnScanlocate);
        btnScanStop = (Button) findViewById(R.id.btnScanStop);
        btnLocateStop = (Button) findViewById(R.id.btnLocateStop);

        // Setup OnClickListener
        btnScanStart.setOnClickListener(this);
        btnScanlocate.setOnClickListener(this);
        btnScanStop.setOnClickListener(this);
        btnLocateStop.setOnClickListener(this);

        // Setup WIFI
        wifimanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        Log.d(TAG, "Setup WIfiManager getSystemService");

        // if WIFIEnabled
        if (wifimanager.isWifiEnabled() == false)
            wifimanager.setWifiEnabled(true);

    }

    public void printToast(String messageToast) {
        Toast.makeText(this, messageToast, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnScanStart) {
            Log.d(TAG, "OnClick() btnScanStart()");
            printToast("WIFI scan for DB !!!");
            initWIFIScan(); // start WIFIScan
        }
        if (v.getId() == R.id.btnScanlocate) {
            Log.d(TAG, "OnClick() btnScanlocate()");
            printToast("Wifi scan for locate !!!");
            locWiFiScan(); // start wifiscan and locate
        }
        if (v.getId() == R.id.btnScanStop) {
            Log.d(TAG, "OnClick() btnScanStop()");
            printToast("WIFI STOP !!!");
            unregisterReceiver(mReceiver); // stop WIFISCan
        }
        if (v.getId() == R.id.btnLocateStop) {
            Log.d(TAG, "OnClick() btnScanStop()");
            printToast("WIFI STOP !!!");
            unregisterReceiver(LReceiver); // stop WIFISCan
        }

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String Result) {
            super.onPostExecute(Result);

            progressDialog.dismiss();
            textStatus.setText(Result);
            Log.d(TAG, "POST response  - " + Result);
        }


        @Override
        protected String doInBackground(String... params) {

            String values1 = (String) params[1];
            String values2 = (String) params[2];
            String values3 = (String) params[3];

            String serverURL = (String) params[0];
            String postParameters = "values1=" + values1 + "&values2=" + values2 + "&values3=" + values3;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();
                serv_result = sb.toString();

                return serv_result;


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
           // textStatus.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null) {

                textStatus.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "values2=" + params[1];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult(){

        String TAG_JSON="values";
        String TAG_ID = "values2";



        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);



                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);


                Locatedata locatedata = new Locatedata();

                locatedata.setLocate_values(id);


                mArrayList.add(locatedata);
                mAdapter.notifyDataSetChanged();
            i++;
               /* if(id.equals("1")){
                    image01 = (ImageView) findViewById(R.id.imageView01);
                    image01.setImageResource(R.drawable.image1);
                }
                else if(id.equals("2")){
                    image01 = (ImageView) findViewById(R.id.imageView01);
                    image01.setImageResource(R.drawable.image2);
                }
                else if(id.equals("3")){
                    image01 = (ImageView) findViewById(R.id.imageView01);
                    image01.setImageResource(R.drawable.image3);
                }
                else if(id.equals("4")){
                    image01 = (ImageView) findViewById(R.id.imageView01);
                    image01.setImageResource(R.drawable.image4);
                }
                else if(id.equals("5")){
                    image01 = (ImageView) findViewById(R.id.imageView01);
                    image01.setImageResource(R.drawable.image5);
                }
                else if(id.equals("6")){
                    image01 = (ImageView) findViewById(R.id.imageView01);
                    image01.setImageResource(R.drawable.image6);
                }
                else if(id.equals("7")){
                    image01 = (ImageView) findViewById(R.id.imageView01);
                    image01.setImageResource(R.drawable.image7);
                }
                else {
                    image01 = (ImageView) findViewById(R.id.imageView01);
                    image01.setImageResource(R.drawable.image8);
                }*/

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
