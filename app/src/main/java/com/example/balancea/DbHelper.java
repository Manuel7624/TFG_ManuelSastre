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
    public static final int VERSION_DB = 3;
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
        sqLiteDatabase.execSQL(consulta_usuarios);
        sqLiteDatabase.execSQL(consulta_categorias);
        sqLiteDatabase.execSQL(consulta_movimientos);

        sqLiteDatabase.execSQL("INSERT INTO " + TABLA_USUARIOS +
                " (nombre, email, password) VALUES " +
                "('admin', 'admin@gmail.com', 'admin');");

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
                " (id_usuario, id_categoria, tipo, cantidad, descripcion, fecha) VALUES " +

                "(1,  3, 'gasto',    45.00, 'Cine con amigos',         '2026-01-14')," +
                "(1,  9, 'ingreso', 200.00, 'Nómina de enero',         '2026-01-01')," +
                "(1,  2, 'gasto',    38.00, 'Tarjeta transporte',      '2026-03-12')," +
                "(1, 10, 'ingreso', 150.00, 'Beca estudios',           '2026-04-10')," +
                "(1,  1, 'gasto',    90.00, 'Supermercado',            '2026-01-20')," +
                "(1,  9, 'ingreso', 220.00, 'Nómina de febrero',       '2026-02-01')," +
                "(1,  5, 'gasto',    50.00, 'Dentista revisión',       '2026-06-12')," +
                "(1, 11, 'ingreso',  60.00, 'Regalo cumpleaños',       '2026-02-18')," +
                "(1,  7, 'gasto',    64.00, 'Ropa temporada',          '2026-05-15')," +
                "(1,  9, 'ingreso', 210.00, 'Nómina de marzo',         '2026-03-01')," +
                "(1,  3, 'gasto',    40.00, 'Concierto',               '2026-05-22')," +
                "(1, 12, 'ingreso',  80.00, 'Venta artículo',          '2026-03-25')," +
                "(1,  6, 'gasto',    35.00, 'Libro Android Studio',    '2026-02-10')," +
                "(1,  9, 'ingreso', 200.00, 'Nómina de abril',         '2026-04-01')," +
                "(1,  1, 'gasto',    75.00, 'Supermercado',            '2026-04-18')," +
                "(1, 10, 'ingreso', 150.00, 'Beca estudios',           '2026-02-15')," +
                "(1,  2, 'gasto',    30.00, 'Gasolina',                '2026-06-03')," +
                "(1,  9, 'ingreso', 215.00, 'Nómina de mayo',          '2026-05-01')," +
                "(1,  8, 'gasto',    25.00, 'Suscripciones',           '2026-01-28')," +
                "(1, 11, 'ingreso',  50.00, 'Regalo graduación',       '2026-06-13')," +
                "(1,  5, 'gasto',    22.00, 'Farmacia',                '2026-03-08')," +
                "(1,  9, 'ingreso', 200.00, 'Nómina de junio',         '2026-06-01')," +
                "(1,  3, 'gasto',    55.00, 'Cena restaurante',        '2026-04-25')," +
                "(1, 12, 'ingreso', 100.00, 'Venta ropa segunda mano', '2026-05-10')," +
                "(1,  7, 'gasto',    48.00, 'Zapatillas',              '2026-02-22')," +
                "(1, 10, 'ingreso', 150.00, 'Beca estudios',           '2026-06-10')," +
                "(1,  1, 'gasto',    82.00, 'Supermercado',            '2026-05-28')," +
                "(1,  6, 'gasto',    40.00, 'Curso online',            '2026-04-14')," +
                "(1,  8, 'gasto',    25.00, 'Suscripciones',           '2026-03-28')," +
                "(1,  2, 'gasto',    35.00, 'Tarjeta transporte',      '2026-06-08');");
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
