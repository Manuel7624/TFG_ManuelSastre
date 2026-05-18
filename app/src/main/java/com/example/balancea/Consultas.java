package com.example.balancea;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

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

}
