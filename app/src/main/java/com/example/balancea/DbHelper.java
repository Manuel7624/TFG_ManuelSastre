package com.example.balancea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Clase que gestiona la creación y actualización de la base de datos SQLite.
 * Define la estructura de las tablas y los datos iniciales de la aplicación.
 */
public class DbHelper extends SQLiteOpenHelper {
    Context context;

    /** Versión actual de la base de datos. */
    public static final int VERSION_DB = 1;
    /** Nombre del archivo de la base de datos. */
    public static final String NOMBRE_DB = "balancea.db";
    /** Nombre de la tabla de movimientos financieros. */
    public static final String TABLA_MOVIMIENTOS = "movimientos";
    /** Nombre de la tabla de usuarios. */
    public static final String TABLA_USUARIOS = "usuarios";
    /** Nombre de la tabla de categorías. */
    public static final String TABLA_CATEGORIAS = "categorias";

    /**
     * Constructor de DbHelper.
     * @param context Contexto de la aplicación.
     */
    public DbHelper(@Nullable Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
        this.context = context;
    }

    /** Sentencia SQL para crear la tabla de movimientos. */
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

    /** Sentencia SQL para crear la tabla de usuarios. */
    String consulta_usuarios = "CREATE TABLE " + TABLA_USUARIOS + " (" +
            "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "email TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL" +
            ");";

    /** Sentencia SQL para crear la tabla de categorías. */
    String consulta_categorias = "CREATE TABLE " + TABLA_CATEGORIAS + " (" +
            "id_categoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "tipo TEXT NOT NULL" +
            ");";

    /**
     * Se ejecuta al crear la base de datos por primera vez.
     * Crea las tablas e inserta los datos por defecto.
     * @param sqLiteDatabase Instancia de la base de datos.
     */
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

    /**
     * Se ejecuta cuando se detecta un cambio en VERSION_DB.
     * Elimina las tablas existentes y las vuelve a crear.
     * @param sqLiteDatabase Instancia de la base de datos.
     * @param oldVersion Versión antigua.
     * @param newVersion Versión nueva.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_MOVIMIENTOS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIOS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_CATEGORIAS);
        onCreate(sqLiteDatabase);
    }

}
