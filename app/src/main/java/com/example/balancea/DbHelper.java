package com.example.balancea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    Context context;

    public static final int VERSION_DB = 1;
    public static final String NOMBRE_DB = "balancea.db";
    public static final String TABLA_MOVIMIENTOS = "movimientos";
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_CATEGORIAS = "categorias";

    public DbHelper(@Nullable Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
        this.context = context;
    }

    String consulta_movimientos = "CREATE TABLE " + TABLA_MOVIMIENTOS + " (" +
            "id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER NOT NULL, " +
            "id_categoria INTEGER NOT NULL, " +
            "tipo TEXT NOT NULL CHECK(tipo IN ('ingreso','gasto')), " +
            "cantidad REAL NOT NULL, " +
            "descripcion TEXT, " +
            "fecha TEXT NOT NULL, " +
            "FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario), " +
            "FOREIGN KEY(id_categoria) REFERENCES categorias(id_categoria)" +
            ");";

    String consulta_usuarios = "CREATE TABLE " + TABLA_USUARIOS + " (" +
            "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "email TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL" +
            ");";

    String consulta_categorias = "CREATE TABLE " + TABLA_CATEGORIAS + " (" +
            "id_categoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "tipo TEXT NOT NULL" +
            ");";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(consulta_movimientos);
        sqLiteDatabase.execSQL(consulta_usuarios);
        sqLiteDatabase.execSQL(consulta_categorias);



        sqLiteDatabase.execSQL("INSERT INTO " + TABLA_USUARIOS +
                " (nombre, email, password) VALUES " +
                "('manuel', 'manus@gmail.com', 'manus')," +
                "('reynaldo', 'reynaldo@gmail.com', 'reynaldo');");

        sqLiteDatabase.execSQL("INSERT INTO " + TABLA_CATEGORIAS +
                " (nombre, tipo) VALUES " +
                "('Comida', 'gasto')," +
                "('Transporte', 'gasto')," +
                "('Ocio', 'gasto')," +
                "('Vivienda', 'gasto')," +
                "('Salud', 'gasto')," +
                "('Educación', 'gasto')," +
                "('Compras', 'gasto')," +
                "('Otros', 'gasto')," +
                "('Nómina', 'ingreso')," +
                "('Beca', 'ingreso')," +
                "('Regalo', 'ingreso')," +
                "('Venta', 'ingreso')," +
                "('Otros', 'ingreso')," +
                "('Saldo inicial', 'ingreso')," +
                "('Saldo inicial', 'gasto');");

        sqLiteDatabase.execSQL("INSERT INTO " + TABLA_MOVIMIENTOS +
                " (id_usuario, id_categoria, tipo, cantidad, descripcion, fecha)\n" +
                "VALUES\n" +
                "(1, 1, 'gasto', 1200.00, 'Comida familiar', '2026-05-26');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_MOVIMIENTOS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIOS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_CATEGORIAS);
        onCreate(sqLiteDatabase);
    }

}
