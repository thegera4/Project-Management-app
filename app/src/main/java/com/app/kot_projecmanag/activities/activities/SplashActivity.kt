package com.app.kot_projecmanag.activities.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.app.kot_projecmanag.databinding.ActivitySplashBinding
import com.app.kot_projecmanag.firebase.FirestoreClass

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //para esconder la barra y hacer que el splash ocupe toda la pantalla
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //para usar fuentes customizadas
        val typeFace: Typeface = Typeface.createFromAsset(assets, "molecules.otf")
        binding.tvAppName.typeface = typeFace

        //Usar metodo postDelayed(Runnable, time)para enviar mensaje con retardo
        Handler().postDelayed({

            //para logear automaticamente al usuario
            var currentUserID = FirestoreClass().getCurrentUserId()
            if (currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 3000) //3000 milisegundos = 3 segundos
    }
}