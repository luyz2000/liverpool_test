package com.example.liverpooltest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    AdaptadorProduct adaptadorProduct;
    ArrayAdapter<String> adapter_history;
    ArrayList<ItemProduct> itemProductArrayList = new ArrayList<ItemProduct>();
    ArrayList<String> history = new ArrayList<String>();
    RecyclerView recyclerView;
    SwipeRefreshLayout swiper;
    LinearLayoutManager layoutManager;

    AutoCompleteTextView editText_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
        swiper = (SwipeRefreshLayout) findViewById(R.id.main_swiperefresh);
        editText_search = (AutoCompleteTextView ) findViewById(R.id.main_box_search);

        adapter_history = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, history);
        editText_search.setAdapter(adapter_history);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adaptadorProduct = new AdaptadorProduct(this, itemProductArrayList);
        recyclerView.setAdapter(adaptadorProduct);

        findViewById(R.id.main_btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetData(editText_search.getText().toString().trim());
            }
        });

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetData(editText_search.getText().toString().trim());
            }
        });
        Obtener_Preferencia();
        GetData("computadora");
    }

    private void GetData(String value) {
        if (value.isEmpty()){
            Toast.makeText(getApplicationContext(),"Ingresa el texto a buscar",Toast.LENGTH_LONG).show();
            swiper.setRefreshing(false);
        }else {
            Guardar_Preferencia(value);
            swiper.setRefreshing(true);
            itemProductArrayList.clear();
            adaptadorProduct.notifyDataSetChanged();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://shoppapp.liverpool.com.mx/appclienteservices/services/v3/plp?force-plp=true&search-string=" + value + "&page-number=1&number-of-items-per-page=150", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray records = response.getJSONObject("plpResults").getJSONArray("records");
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject jsonobject = records.getJSONObject(i);
                            itemProductArrayList.add(new ItemProduct(jsonobject.getString("productDisplayName"),
                                    jsonobject.getString("seller"),
                                    jsonobject.getString("listPrice"),
                                    jsonobject.getString("smImage")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swiper.setRefreshing(false);
                    adaptadorProduct.notifyDataSetChanged();
                    findViewById(R.id.main_noitems).setVisibility(itemProductArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swiper.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            });
            SigletonVolley.getInstance(getBaseContext()).addToRequestQueue(request);
        }
    }

    void Guardar_Preferencia(String new_history) {
        history.add(new_history);
        adapter_history.notifyDataSetChanged();
        SharedPreferences Preferencias = getApplication().getSharedPreferences("preferencias_liverpool", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Preferencias.edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i)).append(",");
        }
        editor.putString("history",  sb.toString());
        editor.apply();
    }

    void Obtener_Preferencia() {
        SharedPreferences Preferencias_Regresan = getApplicationContext().getSharedPreferences("preferencias_liverpool", Context.MODE_PRIVATE);
        String[] history_old = Preferencias_Regresan.getString("history","computadora").split(",");
        Log.d("dev",String.valueOf(history_old.length));
        if (history_old.length > 0){
            history.addAll(Arrays.asList(history_old));
            //Collections.addAll(history, history_old);
            adapter_history.notifyDataSetChanged();
        }
    }

}