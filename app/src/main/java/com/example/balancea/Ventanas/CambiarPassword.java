package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.Consultas;
import com.example.balancea.R;

public class CambiarPassword extends AppCompatActivity {

    ImageView botonVolver;

    EditText editPasswordActual;
    EditText editNuevaPassword;
    EditText editConfirmarPassword;

    TextView botonGuardarPassword;

    TextView textErrorCambiarPassword;

    ImageView imagenVePassAcCambiarPassword;
    ImageView imagenVePassNueCambiarPassword;
    ImageView imagenVePassConCambiarPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_cambiar_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });


        botonVolver =
                findViewById(R.id.botonVolver);

        editPasswordActual =
                findViewById(R.id.editPasswordActual);

        editNuevaPassword =
                findViewById(R.id.editNuevaPassword);

        editConfirmarPassword =
                findViewById(R.id.editConfirmarPassword);

        botonGuardarPassword =
                findViewById(R.id.botonGuardarPassword);

        textErrorCambiarPassword =
                findViewById(R.id.textErrorCambiarPassword);

        imagenVePassAcCambiarPassword =
                findViewById(R.id.imagenVePassAcCambiarPassword);

        imagenVePassNueCambiarPassword =
                findViewById(R.id.imagenVePassNueCambiarPassword);

        imagenVePassConCambiarPassword =
                findViewById(R.id.imagenVePassConCambiarPassword);


        botonVolver.setOnClickListener(v -> finish());


        botonGuardarPassword.setOnClickListener(v -> {

            textErrorCambiarPassword.setVisibility(View.GONE);


            String passwordActual =
                    editPasswordActual.getText().toString().trim();

            String nuevaPassword =
                    editNuevaPassword.getText().toString().trim();

            String confirmarPassword =
                    editConfirmarPassword.getText().toString().trim();


            if (passwordActual.isEmpty() ||
                    nuevaPassword.isEmpty() ||
                    confirmarPassword.isEmpty()) {

                mostrarError("Completa todos los campos");

                return;
            }


            Consultas consultas =
                    new Consultas(this);


            boolean passwordCorrecta =
                    consultas.acceder(
                            CorreoActual,
                            passwordActual
                    );


            if (!passwordCorrecta) {

                mostrarError(
                        "La contraseña actual es incorrecta"
                );

                editPasswordActual.setText("");

                return;
            }


            if (nuevaPassword.length() < 8) {

                mostrarError(
                        "La contraseña debe tener mínimo 8 caracteres"
                );

                return;
            }


            if (!nuevaPassword.equals(confirmarPassword)) {

                mostrarError(
                        "Las contraseñas no coinciden"
                );

                editConfirmarPassword.setText("");

                return;
            }


            if (passwordActual.equals(nuevaPassword)) {

                mostrarError(
                        "La nueva contraseña no puede ser igual"
                );

                return;
            }


            boolean cambiado =
                    Consultas.cambiarPassword(
                            this,
                            CorreoActual,
                            nuevaPassword
                    );


            if (cambiado) {

                finish();

            } else {

                mostrarError(
                        "Error al cambiar la contraseña"
                );
            }
        });

        imagenVePassAcCambiarPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {

                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

                    editPasswordActual.setTransformationMethod(null);
                    imagenVePassAcCambiarPassword.setImageResource(R.drawable.ojo_normal);

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP ||
                        event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {

                    editPasswordActual.setTransformationMethod(
                            android.text.method.PasswordTransformationMethod.getInstance()
                    );

                    imagenVePassAcCambiarPassword.setImageResource(R.drawable.ojo_off);
                }

                editPasswordActual.setSelection(
                        editPasswordActual.getText().length()
                );

                return true;
            }
        });

        imagenVePassNueCambiarPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {

                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

                    editNuevaPassword.setTransformationMethod(null);
                    imagenVePassNueCambiarPassword.setImageResource(R.drawable.ojo_normal);

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP ||
                        event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {

                    editNuevaPassword.setTransformationMethod(
                            android.text.method.PasswordTransformationMethod.getInstance()
                    );

                    imagenVePassNueCambiarPassword.setImageResource(R.drawable.ojo_off);
                }

                editNuevaPassword.setSelection(
                        editNuevaPassword.getText().length()
                );

                return true;
            }
        });

        imagenVePassConCambiarPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {

                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

                    editConfirmarPassword.setTransformationMethod(null);
                    imagenVePassConCambiarPassword.setImageResource(R.drawable.ojo_normal);

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP ||
                        event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {

                    editConfirmarPassword.setTransformationMethod(
                            android.text.method.PasswordTransformationMethod.getInstance()
                    );

                    imagenVePassConCambiarPassword.setImageResource(R.drawable.ojo_off);
                }

                editConfirmarPassword.setSelection(
                        editConfirmarPassword.getText().length()
                );

                return true;
            }
        });
    }


    private void mostrarError(String mensaje) {

        textErrorCambiarPassword.setText(mensaje);

        textErrorCambiarPassword.setVisibility(View.VISIBLE);
    }
}