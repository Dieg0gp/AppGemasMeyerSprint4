package com.example.gemasmeyerapp_ver2.Data

import android.R
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.gemasmeyerapp_ver2.Views.MainActivity
import com.example.gemasmeyerapp_ver2.Views.ProviderType
import com.example.gemasmeyerapp_ver2.databinding.ActivityMainBinding


class Constantes {
    companion object {
        const val URL_API = "http://apijoyeriav2.somee.com/api/"
        const val URL_DEEP_AI_API = "https://api.deepai.org/"
        const val URL_IMG_BB = "https://api.imgbb.com/1/"
        const val API_KEY_DEEP_AI = "d5b5d6f9-7ac3-4373-bb1f-fc5d52e419f5"
        const val CANTIDAD_PRODUCTOS_EN_STOCK_KEY = "cantidad_productos"
        //const val API_KEY_OPENAI = "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"
        //const val API_KEY_OPENAI = "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"
        const val API_KEY_OPENAI = "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"
        const val API_KEY_IMG_BB = "3293728a9422e80a5673fdf77d45b3a4"
        //tipo alerta
        enum class TIPO_ALERTA {
            ALERTA_SIMPLE,
            ALERTA_POS_NEG,
            CARRITO,
            OTRO
        }
        //navegación
        fun pasarPantalla(actividadActual: Context, actividad: Class<*>) {
            val intencion = Intent(actividadActual,actividad)
            startActivity(actividadActual,intencion,null)
            //finish()
        }
        fun pasarPantallaHome(actividadActual:Context,actividad: Class<*>,email: String?,provider: ProviderType) {
            val intencion = Intent(actividadActual,actividad).apply {
                putExtra("email",email)
                putExtra("provider",provider.name)
            }
            startActivity(actividadActual,intencion,null)
        }
        fun pasarPantallaCompra(actividadActual:Context,actividad: Class<*>,total: Int?) {
            val intencion = Intent(actividadActual,actividad).apply {
                putExtra("total",total)
            }
            startActivity(actividadActual,intencion,null)
        }
        //validaciones
        // Validar contraseña
        fun validarClave(contrasena: String,editText: EditText): Boolean {
            // Al menos 8 caracteres, al menos una letra y un número
            val contrasenaRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}\$")
            return if(contrasena.matches(contrasenaRegex)) true else
            {
                editText.error = """
                La contraseña debe tener 8 caracteres y contener por lo menos un letra mayúscula,
                una letra minúscula, un número y un caracter especial.
            """.trimIndent()
                false
            }
        }
        // Validar correo electrónico
        fun validarCorreoElectronico(correo: String,editText: EditText): Boolean {
            return if(android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) true else
            {
                editText.error = """
                El correo electrónico debe contener almenos una @ y un dominio .com .es,etc..
            """.trimIndent()
                false
            }
        }
        fun validarCelular(correo: String,editText: EditText): Boolean {
            // Validación para números de bolivia
            val celularRegex = Regex("^[67]\\d{7}\$")
            return if(correo.matches(celularRegex)) true else {
                editText.error = "El celular debe empezar con 7 o 6 y debe tener 8 caracteres."
                false
            }
        }
        // Validar CI
        fun validarCI(ci: String,editText: EditText): Boolean {
            // Suponiendo que el número de identificación debe tener exactamente 8 dígitos
            val ciRegex = Regex("^[0-9]{7,10}\$")
            return if(ci.matches(ciRegex)) true else {
                editText.error = "El CI debe contener al menos 7 dígitos."
                false
            }
        }
        // Validar nombre
        fun validarNombre(nombre: String,editText: EditText): Boolean {
            // Suponiendo que el número de identificación debe tener exactamente 8 dígitos
            val nombreRegex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{15,50}\$")
            return if(nombre.matches(nombreRegex)) true else {
                editText.error = """
                    El nombre debe tener mínimamente 15 caracteres, 
                    no puede exceder los 50 caracteres y solo puede utilizar letras/espacio.
                """.trimIndent()
                false
            }
        }
        //componentes
        fun showAlert(actividadActual:Context,titulo:String,mensaje:String,tipoAlerta: TIPO_ALERTA,adaptador: View? = null) {
            when(tipoAlerta)
            {
                TIPO_ALERTA.ALERTA_SIMPLE -> {
                    val builder = AlertDialog.Builder(actividadActual)
                    builder.setTitle(titulo)
                    builder.setMessage(mensaje)
                    builder.setPositiveButton("Aceptar", null)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
                TIPO_ALERTA.CARRITO -> {
                    val builder = AlertDialog.Builder(actividadActual)
                    builder.setTitle(titulo)
                    builder.setView(adaptador)
                    builder.setMessage(mensaje)
                    val dialog: AlertDialog = builder.create()
                    dialog.window?.setBackgroundDrawableResource(android.R.color.holo_orange_light)
                    dialog.show()
                }
                TIPO_ALERTA.ALERTA_POS_NEG -> {
                    val builder = AlertDialog.Builder(actividadActual)
                    builder.setTitle(titulo)
                    builder.setMessage(mensaje)
                    builder.setPositiveButton("Aceptar", {dialog,which -> pasarPantalla(actividadActual,MainActivity::class.java)})
                    builder.setNegativeButton("Cancelar", null)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
                else -> {
                    val builder = AlertDialog.Builder(actividadActual)
                    builder.setTitle(titulo)
                    builder.setMessage(mensaje)
                    builder.setPositiveButton("Aceptar", null)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }
    }
}