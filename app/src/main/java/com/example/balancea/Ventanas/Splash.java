package com.example.balancea.Ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.R;

/**
 * Pantalla de bienvenida (Splash Screen) que se muestra al iniciar la aplicación.
 * Permite mostrar el logotipo de la marca antes de redirigir al login.
 */
public class Splash extends AppCompatActivity {

    /** Duración en milisegundos de la pantalla de bienvenida. */
    private static final int TIEMPO_SPLASH = 2500;

    /**
     * Inicializa la actividad y programa la transición automática a MainActivity.
     * @param savedInstanceState Estado guardado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Temporizador para saltar a la pantalla principal de login
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIEMPO_SPLASH);
    }
}
