package com.example.deptosecure

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject // <-- Importante agregar esta librería

class MainActivity : AppCompatActivity() {

    // Declaración de variables
    private lateinit var usu: EditText
    private lateinit var clave: EditText
    private lateinit var btn: Button
    private lateinit var datos: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vinculamos las vistas
        usu = findViewById(R.id.usuario)
        clave = findViewById(R.id.clave)
        btn = findViewById(R.id.btningresar)
        val btnIrRegistro = findViewById<Button>(R.id.btnIrRegistro)

        // Inicializamos Volley
        datos = Volley.newRequestQueue(this)

        // Botón para ir al registro
        btnIrRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        // Botón de Login
        btn.setOnClickListener {
            val sUsu = usu.text.toString().trim()
            val sPass = clave.text.toString().trim()

            if (sUsu.isEmpty() || sPass.isEmpty()) {
                Toast.makeText(this, "Por favor complete los campos", Toast.LENGTH_SHORT).show()
            } else {
                consultarDatos(sUsu, sPass)
            }
        }
    }

    // ⭐ FUNCIÓN CORREGIDA ⭐
    private fun consultarDatos(usuario: String, pass: String) {
        // 1. URL limpia (sin parámetros ?usu=...)
        val url = "http://3.208.190.223/apiconsultausu.php"

        // 2. Crear el JSON Body
        val jsonParams = JSONObject()
        try {
            jsonParams.put("usu", usuario)
            jsonParams.put("pass", pass)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // 3. Cambiar a POST y enviar jsonParams
        val request = JsonObjectRequest(
            Request.Method.POST, // <-- CAMBIO IMPORTANTE: POST
            url,
            jsonParams,
            { response ->
                try {
                    // Leemos la respuesta del PHP (que debe devolver "estado" y "msg")
                    val estado = response.optString("estado")
                    val mensaje = response.optString("msg") // Mensaje del PHP

                    if (estado == "1") {
                        // Login Exitoso
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Principal::class.java)
                        startActivity(intent)
                        finish() // Cierra el login para que no vuelvan atrás
                    } else {
                        // Error (clave mal, usuario no existe, etc.)
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
                error.printStackTrace()
            }
        )

        datos.add(request)
    }
}