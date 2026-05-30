package com.example.balancea;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.balancea.Modelos.Usuario;

import java.util.ArrayList;

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

    // Funciones ventana principal

    public static String obtenerNombreUsuario(Context context, String correo) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String nombre = "";

        String consulta =
                "SELECT nombre FROM usuarios WHERE email = ?";

        Cursor cursor = db.rawQuery(
                consulta,
                new String[]{correo}
        );

        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return nombre;
    }

    public static double obtenerSaldoActual(Context context, String correo) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        double saldo = 0;

        String consulta =
                "SELECT " +
                        "SUM(CASE WHEN tipo='ingreso' THEN cantidad ELSE -cantidad END) " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                        "WHERE u.email = ?";

        Cursor cursor = db.rawQuery(
                consulta,
                new String[]{correo}
        );

        if(cursor.moveToFirst()){

            saldo = cursor.getDouble(0);

        }

        cursor.close();
        db.close();

        return saldo;
    }

    public static double obtenerIngresos(Context context, String correo) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        double ingresos = 0;

        String consulta =
                "SELECT SUM(cantidad) " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u " +
                        "ON m.id_usuario = u.id_usuario " +
                        "WHERE u.email = ? " +
                        "AND tipo = 'ingreso'";

        Cursor cursor = db.rawQuery(
                consulta,
                new String[]{correo}
        );

        if(cursor.moveToFirst()){
            ingresos = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return ingresos;
    }

    public static double obtenerGastos(Context context, String correo) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        double gastos = 0;

        String consulta =
                "SELECT SUM(cantidad) " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u " +
                        "ON m.id_usuario = u.id_usuario " +
                        "WHERE u.email = ? " +
                        "AND tipo = 'gasto'";

        Cursor cursor = db.rawQuery(
                consulta,
                new String[]{correo}
        );

        if(cursor.moveToFirst()){
            gastos = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return gastos;
    }

    public static double obtenerIngresosMesActual(Context context, String correoUsuario) {

        double ingresos = 0;

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        String consulta =
                "SELECT SUM(m.cantidad) " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u " +
                        "ON m.id_usuario = u.id_usuario " +
                        "WHERE u.email = ? " +
                        "AND m.tipo = 'ingreso' " +
                        "AND strftime('%Y-%m', m.fecha) = strftime('%Y-%m', 'now')";

        Cursor cursor = db.rawQuery(
                consulta,
                new String[]{correoUsuario}
        );

        if(cursor.moveToFirst()){

            ingresos = cursor.getDouble(0);
        }

        cursor.close();

        db.close();

        return ingresos;
    }

    public static double obtenerGastosMesActual(Context context, String correoUsuario) {

        double gastos = 0;

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        String consulta =
                "SELECT SUM(m.cantidad) " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u " +
                        "ON m.id_usuario = u.id_usuario " +
                        "WHERE u.email = ? " +
                        "AND m.tipo = 'gasto' " +
                        "AND strftime('%Y-%m', m.fecha) = strftime('%Y-%m', 'now')";

        Cursor cursor = db.rawQuery(
                consulta,
                new String[]{correoUsuario}
        );

        if(cursor.moveToFirst()){

            gastos = cursor.getDouble(0);
        }

        cursor.close();

        db.close();

        return gastos;
    }

    public static Cursor obtenerUltimoMovimiento(Context context, String correoUsuario) {

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        String consulta =
                "SELECT " +
                        "m.tipo, " +
                        "m.descripcion, " +
                        "m.cantidad, " +
                        "m.fecha, " +
                        "m.id_categoria, " +
                        "c.nombre " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u " +
                        "ON m.id_usuario = u.id_usuario " +
                        "INNER JOIN categorias c " +
                        "ON m.id_categoria = c.id_categoria " +
                        "WHERE u.email = ? " +
                        "ORDER BY m.id_movimiento DESC " +
                        "LIMIT 2";

        return db.rawQuery(consulta, new String[]{correoUsuario});
    }

    public static Cursor obtenerMovimientos(Context context, String correoUsuario, String filtro, String busqueda, String orden) {

        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String consulta = "SELECT m.tipo, m.descripcion, m.cantidad, m.fecha, " +
                        "m.id_categoria, c.nombre " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                        "INNER JOIN categorias c ON m.id_categoria = c.id_categoria " +
                        "WHERE u.email = ? ";

        ArrayList<String> parametros = new ArrayList<>();
        parametros.add(correoUsuario);

        if (!filtro.equals("todos")) {

            consulta += "AND m.tipo = ? ";

            parametros.add(filtro);
        }

        if (busqueda != null && !busqueda.isEmpty()) {

            consulta += "AND (c.nombre LIKE ? OR m.descripcion LIKE ?) ";

            parametros.add("%" + busqueda + "%");
            parametros.add("%" + busqueda + "%");
        }

        if(orden.equals("fecha")){

            consulta += "ORDER BY m.fecha DESC, m.id_movimiento DESC";

        }else{

            consulta += "ORDER BY m.id_movimiento DESC";
        }

        return db.rawQuery(consulta, parametros.toArray(new String[0]));
    }

    public static boolean insertarMovimiento(Context context, String correoUsuario, int idCategoria, String tipo, double cantidad, String descripcion, String fecha) {

        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        boolean resultado = false;

        try {
            Cursor cursor = db.rawQuery("SELECT id_usuario FROM usuarios WHERE email = ?", new String[]{correoUsuario});

            if(cursor.moveToFirst()){

                int idUsuario = cursor.getInt(0);

                ContentValues values = new ContentValues();

                values.put("id_usuario", idUsuario);
                values.put("id_categoria", idCategoria);
                values.put("tipo", tipo);
                values.put("cantidad", cantidad);
                values.put("descripcion", descripcion);
                values.put("fecha", fecha);

                long res = db.insert(TABLA_MOVIMIENTOS, null, values);

                resultado = res != -1;
            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            db.close();
        }

        return resultado;
    }


    public static ArrayList<String> obtenerCategorias(Context context, String tipo) {

        ArrayList<String> lista = new ArrayList<>();

        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT nombre FROM categorias " + "WHERE tipo = ? " + "AND id_categoria != 14 " + "AND id_categoria != 15", new String[]{tipo});

        while(cursor.moveToNext()){

            lista.add(cursor.getString(0));
        }

        cursor.close();

        db.close();

        return lista;
    }

    public static int obtenerIdCategoria(Context context, String nombreCategoria) {

        int id = 0;

        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id_categoria FROM categorias WHERE nombre = ?", new String[]{nombreCategoria});

        if(cursor.moveToFirst()){

            id = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return id;
    }



    // VENTANA GRAFICAS


    public static Cursor obtenerGastosPorCategoria(Context context, String correoUsuario) {

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        String consulta =
                "SELECT c.nombre, SUM(m.cantidad) total " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                        "INNER JOIN categorias c ON m.id_categoria = c.id_categoria " +
                        "WHERE u.email = ? " +
                        "AND m.tipo = 'gasto' " +
                        "GROUP BY c.nombre " +
                        "ORDER BY total DESC";

        return db.rawQuery(
                consulta,
                new String[]{correoUsuario}
        );
    }


    public static Cursor obtenerIngresosPorCategoria(Context context, String correoUsuario) {

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        String consulta =
                "SELECT c.nombre, SUM(m.cantidad) total " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                        "INNER JOIN categorias c ON m.id_categoria = c.id_categoria " +
                        "WHERE u.email = ? " +
                        "AND m.tipo = 'ingreso' " +
                        "GROUP BY c.nombre " +
                        "ORDER BY total DESC";

        return db.rawQuery(
                consulta,
                new String[]{correoUsuario}
        );
    }


    public static String obtenerMayorGasto(Context context, String correoUsuario) {

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        String resultado = "Sin datos";

        String consulta =
                "SELECT c.nombre, MAX(m.cantidad) " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                        "INNER JOIN categorias c ON m.id_categoria = c.id_categoria " +
                        "WHERE u.email = ? " +
                        "AND m.tipo = 'gasto'";

        Cursor cursor = db.rawQuery(
                consulta,
                new String[]{correoUsuario}
        );

        if (cursor.moveToFirst() &&
                cursor.getString(0) != null) {

            String categoria = cursor.getString(0);

            double cantidad = cursor.getDouble(1);

            resultado = categoria + " · " + cantidad + "€";
        }

        cursor.close();

        db.close();

        return resultado;
    }


    public static String obtenerMayorIngreso(Context context, String correoUsuario) {

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        String resultado = "Sin datos";

        String consulta =
                "SELECT c.nombre, MAX(m.cantidad) " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                        "INNER JOIN categorias c ON m.id_categoria = c.id_categoria " +
                        "WHERE u.email = ? " +
                        "AND m.tipo = 'ingreso'";

        Cursor cursor = db.rawQuery(consulta, new String[]{correoUsuario});

        if (cursor.moveToFirst() && cursor.getString(0) != null) {

            String categoria = cursor.getString(0);

            double cantidad = cursor.getDouble(1);

            resultado = categoria + " · " + cantidad + "€";
        }

        cursor.close();

        db.close();

        return resultado;
    }


    public static Cursor obtenerEvolucionMensual(Context context, String correoUsuario) {

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        String consulta =
                "SELECT " +
                        "strftime('%m', fecha) mes, " +
                        "SUM(CASE WHEN tipo='ingreso' THEN cantidad ELSE 0 END) ingresos, " +
                        "SUM(CASE WHEN tipo='gasto' THEN cantidad ELSE 0 END) gastos " +
                        "FROM movimientos m " +
                        "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                        "WHERE u.email = ? " +
                        "AND strftime('%Y', fecha) = strftime('%Y', 'now') " +
                        "GROUP BY mes " +
                        "ORDER BY mes ASC";

        return db.rawQuery(consulta, new String[]{correoUsuario});
    }

    public static boolean cambiarPassword(Context context, String email, String nuevaPassword) {

        DbHelper helper = new DbHelper(context);

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("password", nuevaPassword);

        int filas = db.update(TABLA_USUARIOS, values, "email = ?", new String[]{email});

        db.close();

        return filas > 0;
    }

}
