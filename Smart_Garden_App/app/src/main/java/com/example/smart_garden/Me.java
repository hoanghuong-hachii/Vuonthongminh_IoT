package com.example.smart_garden;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.widget.TextView;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import android.widget.ProgressBar;
import android.widget.Toast;

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
 * A simple {@link Fragment} subclass.
 * Use the {@link Me#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Me extends Fragment {
    Button logout;
    private static final int REUEST_CODE = 22;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Me() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Me newInstance(String param1, String param2) {
        Me fragment = new Me();
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

    TextView username, email, phone, password, birthday;

    private String strJson, apiUrl = "http://192.168.43.148:8080/loginregister/fetch_user.php";
//    private String strJson, apiUrl = "http://192.168.60.5:8080/loginregister/fetch_user.php";
    private OkHttpClient client;
    private Response response;
    private RequestBody requestBody;
    private Request request;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logout = (Button) view.findViewById(R.id.log_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
            }
        });

        //initialize your view here for use view.findViewById("your view id")

        username = (TextView) view.findViewById(R.id.username);
        email = (TextView) view.findViewById(R.id.email);
        phone = (TextView) view.findViewById(R.id.phone);
        password = (TextView) view.findViewById(R.id.password);
//        birthday = (TextView) view.findViewById(R.id.birthday);

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
                new Me.GetUserDataRequest().execute();
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
            String User =child.getString("username");
            String Eml =child.getString("email");
            String Phn =child.getString("phone");
            String Pad =child.getString("password");
//            String Btd =child.getString("birthday");


            username.setText(User);
            email.setText(Eml);
            phone.setText(Phn);
            password.setText(Pad);
//            birthday.setText(Btd);
            progressDialog.hide();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }





}