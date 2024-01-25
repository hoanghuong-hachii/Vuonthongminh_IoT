package com.example.smart_garden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button login;
    public String result;


    private static final String LOGIN_URL = "http://192.168.43.61:8080/api/auth/login";
//private static final String LOGIN_URL = "http://192.168.60.7:8080/api/auth/login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

            email = findViewById(R.id.email);
            password= findViewById(R.id.password);

            login = findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String txtEmail= email.getText().toString();
                    String txtPassword= password.getText().toString();

                    if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
                        Toast.makeText(Login.this,"Vui lòng nhập thông tin",Toast.LENGTH_SHORT).show();

                    }
                    else {
                        login(txtEmail,txtPassword);
                    }
                }
            });
        }

    private void login(String txtEmail,String txtPassword) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", txtEmail);
            requestBody.put("password", txtPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                         Intent intent = ;
                            startActivity(new Intent(Login.this, Index.class));
//                        String result = null;
                        try {
                            result = response.getString("token");
                            Intent intent = new Intent(Login.this, Index.class) ;
                            intent.putExtra("token",result);
                            startActivity(intent);
                            Log.d("Response", result);
                            Toast.makeText(Login.this,"Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,"Email hoặc password không tồn tại!",Toast.LENGTH_SHORT).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(loginRequest);

        }
       


}