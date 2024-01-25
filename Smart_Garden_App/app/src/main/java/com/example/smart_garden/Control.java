package com.example.smart_garden;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smart_garden.jsonControl.JsonControl;
import com.example.smart_garden.jsonControl.Params;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//{
//        "1":"false",
//        "2":"false",
//        "3":"true"
//        }
//http://192.168.60.5:8080/api/v1/huong/attributes

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Control#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Control extends Fragment{

    SwitchCompat switchCompatCtrFan, switchCompatCtrPum,
            switchCompatCtrDripp, switchCompatCtrLight;



//    public void setToken1(String token1) {
//        this.token1 = token1;
//    }

        private static final String CONTROL_URL = "http://192.168.43.61:8080/api/plugins/rpc/twoway/415ec8d0-7eb3-11ed-a22b-793e093a9920";
//    private static final String CONTROL_URL = "http://192.168.60.6:8080/api/plugins/rpc/twoway/415ec8d0-7eb3-11ed-a22b-793e093a9920";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Control() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Control.
     */
    // TODO: Rename and change types and number of parameters
    public static Control newInstance(String param1, String param2) {
        Control fragment = new Control();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_control, container, false);


//    }

        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchCompatCtrFan = view.findViewById(R.id.control_fan);
        switchCompatCtrPum = view.findViewById(R.id.control_npump);
        switchCompatCtrDripp = view.findViewById(R.id.control_drippump);
        switchCompatCtrLight = view.findViewById(R.id.control_light);

//        private A a;
//    public B(A a) {
//            this.a = a;
//        }
//        public void printData() {
//            System.out.println(a.getData());
//        }
//



        String token = getActivity().getIntent().getStringExtra("token");
        Log.d("token", token);
        String auth = "Bearer " + token;
        switchCompatCtrFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                String statusFan = switchCompatCtrFan.getText().toString();
                boolean fanStatus = isChecked;


                if (fanStatus) {
                    Toast.makeText(getContext(), "Quạt đã được bật", Toast.LENGTH_SHORT).show();
                    String method = "setGpioStatus";
                    long timeout = 500;
                    JSONObject params = new JSONObject();

                    try {
                        params.put("pin", 4);
                        params.put("enabled",true );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", method);
                        json.put("params", params);
                        json.put("timeout", timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, CONTROL_URL, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    startActivity(new Intent(getContext(), Index.class));
                                    String result = null;
                                    try {
                                        result = response.getString("4");
                                        Log.d("Response", result);
                                        Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", auth);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(loginRequest);
                } else {
                    Toast.makeText(getContext(), "Quạt đã được tắt", Toast.LENGTH_SHORT).show();
                    String method = "setGpioStatus";
                    long timeout = 500;
                    JSONObject params = new JSONObject();

                    try {
                        params.put("pin", 4);
                        params.put("enabled",false );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", method);
                        json.put("params", params);
                        json.put("timeout", timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, CONTROL_URL, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    startActivity(new Intent(getContext(), Index.class));
                                    String result = null;
                                    try {
                                        result = response.getString("4");
                                        Log.d("Response", result);
                                        Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", auth);
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(loginRequest);

                    Log.d("JSON", json.toString());
                }

            }
        });

// bơm tia
        switchCompatCtrPum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean pumpStatus = isChecked;
                if (pumpStatus) {
                    Toast.makeText(getContext(), "Máy bơm phun tia đã được bật", Toast.LENGTH_SHORT).show();
                    String method = "setGpioStatus";
                    long timeout = 500;
                    JSONObject params = new JSONObject();

                    try {
                        params.put("pin", 3);
                        params.put("enabled",true );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", method);
                        json.put("params", params);
                        json.put("timeout", timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, CONTROL_URL, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    startActivity(new Intent(getContext(), Index.class));
                                    String result = null;
                                    try {
                                        result = response.getString("3");
                                        Log.d("Response", result);
                                        Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", auth);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(loginRequest);

                    Log.d("JSON", json.toString());


                } else {
                    Toast.makeText(getContext(), "Máy bơm phun tia đã được tắt", Toast.LENGTH_SHORT).show();
                    String method = "setGpioStatus";
                    long timeout = 500;
                    JSONObject params = new JSONObject();

                    try {
                        params.put("pin", 3);
                        params.put("enabled",false );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", method);
                        json.put("params", params);
                        json.put("timeout", timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, CONTROL_URL, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    startActivity(new Intent(getContext(), Index.class));
                                    String result = null;
                                    try {
                                        result = response.getString("3");
                                        Log.d("Response", result);
                                        Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", auth);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(loginRequest);

                    Log.d("JSON", json.toString());
                }
            }
        });

// bơm nhỏ giọt
        switchCompatCtrDripp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean drippStatus = isChecked;
                if (drippStatus) {
                    Toast.makeText(getContext(), "Máy bơm nhỏ giọt đã được bật", Toast.LENGTH_SHORT).show();
                    String method = "setGpioStatus";
                    long timeout = 500;
                    JSONObject params = new JSONObject();

                    try {
                        params.put("pin", 2);
                        params.put("enabled",true );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", method);
                        json.put("params", params);
                        json.put("timeout", timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, CONTROL_URL, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    startActivity(new Intent(getContext(), Index.class));
                                    String result = null;
                                    try {
                                        result = response.getString("2");
                                        Log.d("Response", result);
                                        Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", auth);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(loginRequest);

                    Log.d("JSON", json.toString());


                } else {
                    Toast.makeText(getContext(), "Máy bơm nhỏ giọt đã được tắt", Toast.LENGTH_SHORT).show();
                    String method = "setGpioStatus";
                    long timeout = 500;
                    JSONObject params = new JSONObject();

                    try {
                        params.put("pin", 2);
                        params.put("enabled",false );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", method);
                        json.put("params", params);
                        json.put("timeout", timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, CONTROL_URL, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    startActivity(new Intent(getContext(), Index.class));
                                    String result = null;
                                    try {
                                        result = response.getString("2");
                                        Log.d("Response", result);
                                        Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", auth);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(loginRequest);

                    Log.d("JSON", json.toString());
                }
            }
        });

//        Đèn

        switchCompatCtrLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean lifhtStatus = isChecked;
                if (lifhtStatus) {
                    Toast.makeText(getContext(), "Đèn đã được bật", Toast.LENGTH_SHORT).show();
                    String method = "setGpioStatus";
                    long timeout = 500;
                    JSONObject params = new JSONObject();

                    try {
                        params.put("pin", 5);
                        params.put("enabled",true );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", method);
                        json.put("params", params);
                        json.put("timeout", timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, CONTROL_URL, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    startActivity(new Intent(getContext(), Index.class));
                                    String result = null;
                                    try {
                                        result = response.getString("5");
                                        Log.d("Response", result);
                                        Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", auth);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(loginRequest);

                    Log.d("JSON", json.toString());


                } else {
                    Toast.makeText(getContext(), "Đèn đã được tắt", Toast.LENGTH_SHORT).show();
                    String method = "setGpioStatus";
                    long timeout = 500;
                    JSONObject params = new JSONObject();

                    try {
                        params.put("pin", 5);
                        params.put("enabled",false );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json = new JSONObject();
                    try {
                        json.put("method", method);
                        json.put("params", params);
                        json.put("timeout", timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, CONTROL_URL, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    startActivity(new Intent(getContext(), Index.class));
                                    String result = null;
                                    try {
                                        result = response.getString("5");
                                        Log.d("Response", result);
                                        Toast.makeText(getContext(), "5: " + result, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", auth);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(loginRequest);

                    Log.d("JSON", json.toString());
                }
            }
        });

//        {
//            "method":"setGpioStatus",
//                "params":
//            {
//                "pin":5,"enabled":true
//            },
//            "timeout":500
//        }

    }

}