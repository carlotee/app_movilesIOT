package com.example.deptosecure

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import org.json.JSONObject

class Registro : AppCompatActivity() {

    private lateinit var rut: EditText
    private lateinit var nombre: EditText
    private lateinit var correo: EditText
    private lateinit var telefono: EditText
    private lateinit var clave: EditText
    private lateinit var confirmClave: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var txtLogin2: TextView

    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vincular IDs
        rut = findViewById(R.id.inputRut)
        nombre = findViewById(R.id.inputNombre2)
        correo = findViewById(R.id.inputCorreo2)
        telefono = findViewById(R.id.inputTelefono)
        clave = findViewById(R.id.inputPassword2)
        confirmClave = findViewById(R.id.inputConfirmPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar2)
        txtLogin2 = findViewById(R.id.txtLogin2)

        queue = Volley.newRequestQueue(this)

        // 👉 Botón registrar
        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }

        // 👉 Texto "¿Ya tienes cuenta? Inicia sesión"
        txtLogin2.setOnClickListener {
            irAlLogin()
        }
    }

    private fun irAlLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Cierra Registro para que no se acumule en la pila
    }

    private fun registrarUsuario() {

        val sRut = rut.text.toString().trim()
        val sNombre = nombre.text.toString().trim()
        val sCorreo = correo.text.toString().trim()
        val sTelefono = telefono.text.toString().trim()
        val sClave = clave.text.toString().trim()
        val sConfirm = confirmClave.text.toString().trim()

        // Validaciones básicas
        if (sRut.isEmpty() || sNombre.isEmpty() || sCorreo.isEmpty() || sClave.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (sClave != sConfirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            return
        }

        val url = "http://3.208.190.223/registro.php"

        val json = JSONObject().apply {
            put("rut", sRut)
            put("nombre", sNombre)
            put("email", sCorreo)
            put("telefono", sTelefono)
            put("password", sClave) // Se envía plana, el PHP la hashea
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, json,
            { response ->
                try {
                    // ⭐ CORRECCIÓN AQUÍ: Usamos 'estado' y 'msg'
                    // optString es más seguro porque no crashea si el campo no existe
                    val estado = response.optString("estado")
                    val message = response.optString("msg")

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                    // Verificamos si el estado es "1" (Exitoso)
                    if (estado == "1") {
                        // Redirigir al Login (MainActivity)
                        irAlLogin()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error procesando respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(request)
    }
}