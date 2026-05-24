package com.example.balancea;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.balancea.Modelos.Usuario;

public class Consultas extends DbHelper{

    Context context;
    public Consultas(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public boolean acceder(String email, String password) {

        String consulta = "Select email, password from " + TABLA_USUARIOS + " where email=? AND password=?";

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor fila = db.rawQuery(consulta, new String[]{email, password});

        boolean existe = fila.getCount()>0;

        fila.close();
        db.close();

        return existe;
    }

    public boolean correoExistente(String email) {

        String consulta = "Select email from " + TABLA_USUARIOS + " where email=?";

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor fila = db.rawQuery(consulta, new String[]{email});

        boolean existe = fila.getCount()>0;

        fila.close();
        db.close();

        return existe;
    }

    public boolean registrarUsuario(Usuario u) {
        SQLiteDatabase db = getWritableDatabase();
        boolean insercionCorrecta = false;
        try {
            ContentValues values = new ContentValues();
            values.put("nombre", u.getNombre());
            values.put("email", u.getEmail());
            values.put("password", u.getPassword());
            long resultado = db.insert(TABLA_USUARIOS, null, values);
            insercionCorrecta = resultado != -1; // ← comprobar el resultado
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return insercionCorrecta;
    }

    public boolean insertarSaldoInicial(double saldo, String email) {
        SQLiteDatabase db = getWritableDatabase();
        boolean resultado = false;
        try {
            Cursor cursor = db.rawQuery("SELECT id_usuario FROM usuarios WHERE email = ?", new String[]{email});
            if (cursor.moveToFirst()) {
                int idUsuario = cursor.getInt(0);
                cursor.close();

                ContentValues values = new ContentValues();
                values.put("id_usuario", idUsuario);
                values.put("cantidad", Math.abs(saldo));
                values.put("descripcion", "Saldo inicial");
                values.put("fecha", new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date()));

                if (saldo >= 0) {
                    values.put("tipo", "ingreso");
                    values.put("id_categoria", 14);
                } else {
                    values.put("tipo", "gasto");
                    values.put("id_categoria", 15);
                }

                long res = db.insert(TABLA_MOVIMIENTOS, null, values);
                resultado = res != -1;
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return resultado;
    }

}
