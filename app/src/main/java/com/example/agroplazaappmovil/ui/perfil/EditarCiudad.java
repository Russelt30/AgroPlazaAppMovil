package com.example.agroplazaappmovil.ui.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.agroplazaappmovil.Login;
import com.example.agroplazaappmovil.Principal;
import com.example.agroplazaappmovil.R;
import com.example.agroplazaappmovil.RegistroUsuarios;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditarCiudad extends AppCompatActivity {

    RadioGroup lista_ciudades;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ciudad);

        pDialog = new SweetAlertDialog(EditarCiudad.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.GREEN);
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(false);
        pDialog.show();

        lista_ciudades = findViewById(R.id.lista_ciudades);

        Button regresar = findViewById(R.id.btn_atras);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarCiudad.super.onBackPressed();
            }
        });

        SharedPreferences persistencia = getSharedPreferences("datos_login", Context.MODE_PRIVATE);
        int ciudad_perfil = Integer.parseInt(persistencia.getString("id_ciudad", "0"));

        cargarCiudades(ciudad_perfil, persistencia);
    }

    public void cargarCiudades(int ciudad_perfil, SharedPreferences persistencia) {
        RequestQueue hilo = Volley.newRequestQueue(getApplicationContext());
        String url = "https://agroplaza.solucionsoftware.co/ModuloUsuarios/CargarCiudades";

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray registros = response.optJSONArray("ciudades");

                    for (int i = 0; i < registros.length(); i++) {
                        JSONObject fila = registros.getJSONObject(i);

                        RadioButton ciudad = new RadioButton(getApplicationContext());
                        ciudad.setId(fila.getInt("id"));
                        ciudad.setText(fila.getString("nombre").toUpperCase());

                        ciudad.setWidth(500);
                        ciudad.setHeight(200);

                        ciudad.setTextSize(16);
                        ciudad.setTextColor(getResources().getColor(R.color.black));

                        if (ciudad.getId() == ciudad_perfil) {
                            ciudad.setChecked(true);
                        }

                        lista_ciudades.addView(ciudad);
                    }

                    pDialog.dismiss();

                    lista_ciudades.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            actualizarCiudad(persistencia);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("Error extrayendo datos ", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Servidor: " + error.getMessage(), Toast.LENGTH_LONG).show();
                if (error.getMessage() != null) {
                    Log.i("Error Servidor: ", error.getMessage());
                } else {
                    Log.i("Error Servidor: ", "Error desconocido");
                }
            }
        });

        hilo.add(solicitud);
    }

    public void actualizarCiudad(SharedPreferences persistencia) {
        String ciudad = lista_ciudades.getCheckedRadioButtonId() + "";
        String id_perfil = persistencia.getString("id", "0");

        SweetAlertDialog pDialog = new SweetAlertDialog(EditarCiudad.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.GREEN);
        pDialog.setTitleText("Espera ...");
        pDialog.setCancelable(false);
        pDialog.show();

        // Instrucciones de Volley
        RequestQueue hilo = Volley.newRequestQueue(this);
        String url = "https://agroplaza.solucionsoftware.co/ModuloUsuarios/CambiarCiudadMovil";

        StringRequest solicitud = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.trim().equalsIgnoreCase("ERROR##UPDATE")) {
                            pDialog.dismiss();
                            new SweetAlertDialog(EditarCiudad.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Ocurrio un error en el servidor!")
                                    .show();
                        } else {
                            SharedPreferences.Editor editor = persistencia.edit();
                            editor.putString("id_ciudad", ciudad);
                            editor.commit();

                            pDialog.dismiss();
                            new SweetAlertDialog(EditarCiudad.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Datos Actualizados!")
                                    .setContentText("Su ciudad ha sido actualizada.")
                                    .setConfirmText("Hecho!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            EditarCiudad.super.onBackPressed();
                                        }
                                    })
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Codigo de error del servidor
                        // Se ejecuta cuando no llega el tipo solicitado String.
                        Toast.makeText(getApplicationContext(), "Error Servidor: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        if (error.getMessage() != null) {
                            Log.i("Error Servidor: ", error.getMessage());
                        } else {
                            Log.i("Error Servidor: ", "Error desconocido");
                        }
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_perfil", id_perfil);
                parametros.put("id_ciudad", ciudad);
                return parametros;
            }
        };
        hilo.add(solicitud);
    }
}