package com.example.smart_garden;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.ProgressBar;

import com.example.smart_garden.databinding.ActivityIndexBinding;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
/**
========================================
 */
public class Dashboard extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btn;

    public Dashboard() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    TextView nhietdo, doamkk, doamdat, anhsang;

    private String strJson, apiUrl = "http://192.168.43.148:8080/loginregister/fetch_data.php";

    private OkHttpClient client;
    private Response response;
    private RequestBody requestBody;
    private Request request;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn = (Button) view.findViewById(R.id.camera);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityCamera();
            }
        });

        //initialize your view here for use view.findViewById("your view id")

        nhietdo = (TextView) view.findViewById(R.id.nhietdo);
        doamkk = (TextView) view.findViewById(R.id.doamkk);
        doamdat = (TextView) view.findViewById(R.id.doamdat);
        anhsang = (TextView) view.findViewById(R.id.anhsang);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait.....");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();

        final Handler handler = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                // data request
                client = new OkHttpClient();
                new GetUserDataRequest().execute();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(refresh, 5000);
    }

    public class GetUserDataRequest extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            request = new Request.Builder().url(apiUrl).build();
            try {
                response = client.newCall(request).execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            try {
                strJson = response.body().string();
                updateUserData(strJson);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateUserData(String strJson) {
        try {
            JSONArray parent = new JSONArray(strJson);
            JSONObject child =parent.getJSONObject(0);
            String temp =child.getString("nhietdo");
            String hum =child.getString("doamkk");
            String soil =child.getString("doamdat");
            String light =child.getString("anhsang");
           // String access_tokendata =child.getString("access_token");


            nhietdo.setText(temp);
            doamkk.setText(hum);
            doamdat.setText(soil);
            anhsang.setText(light);
            progressDialog.hide();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void openActivityCamera(){
        Intent intent = new Intent(getActivity(), Activity_Camera.class);
        startActivity(intent);
    }




}