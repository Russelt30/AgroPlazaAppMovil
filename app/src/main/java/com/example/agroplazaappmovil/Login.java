package com.example.agroplazaappmovil;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText campo_email, campo_password;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.login);

        campo_email = findViewById(R.id.campo_email);
        campo_password = findViewById(R.id.campo_password);

        Button btn_ingresar = findViewById(R.id.btn_ingresar);
        btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatosSesion();
            }
        });
    }

    public void validarDatosSesion() {
        String valor_email = campo_email.getText().toString();
        String valor_password = campo_password.getText().toString();

        // Instrucciones de Volley
        RequestQueue hilo = Volley.newRequestQueue(this);
        String url = "https://agroplaza.solucionsoftware.co/inicio/validarDatosIngreso";

        StringRequest solicitud = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Codigo de lo que quieran con la respuesta del servidor
                        Toast.makeText(getApplicationContext(), "Respuesta Serv: " + response, Toast.LENGTH_LONG).show();
                        Log.i("Ok Servidor: ", response);

                        if (response.trim().equalsIgnoreCase("OK##DATA##LOGIN")) {
                            campo_email.setText("");
                            campo_password.setText("");

                            Toast.makeText(getApplicationContext(), "La sesion ha sido iniciada!", Toast.LENGTH_LONG).show();
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
                parametros.put("email", valor_email);
                parametros.put("password", valor_password);
                return parametros;
            }
        };
        hilo.add(solicitud);
    }
}