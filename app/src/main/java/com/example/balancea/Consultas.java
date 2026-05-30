package com.example.balancea;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.balancea.Modelos.Usuario;

import java.util.ArrayList;

/**
 * Clase encargada de gestionar todas las operaciones de lectura y escritura
 * en la base de datos SQLite de la aplicación.
 */
public class Consultas extends DbHelper{

    Context context;

    /**
     * Constructor de la clase Consultas.
     * @param context Contexto de la aplicación necesario para inicializar el DbHelper.
     */
    public Consultas(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Verifica las credenciales de un usuario para permitir el acceso.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return true si las credenciales coinciden con un registro, false en caso contrario.
     */
    public boolean acceder(String email, String password) {
        String consulta = "Select email, password from " + TABLA_USUARIOS + " where email=? AND password=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor fila = db.rawQuery(consulta, new String[]{email, password});
        boolean existe = fila.getCount()>0;
        fila.close();
        db.close();
        return existe;
    }

    /**
     * Comprueba si un correo electrónico ya está registrado en la base de datos.
     * @param email Correo a verificar.
     * @return true si el correo existe, false si está disponible.
     */
    public boolean correoExistente(String email) {
        String consulta = "Select email from " + TABLA_USUARIOS + " where email=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor fila = db.rawQuery(consulta, new String[]{email});
        boolean existe = fila.getCount()>0;
        fila.close();
        db.close();
        return existe;
    }

    /**
     * Registra un nuevo usuario en la tabla de usuarios.
     * @param u Objeto Usuario con la información de registro.
     * @return true si la inserción fue exitosa.
     */
    public boolean registrarUsuario(Usuario u) {
        SQLiteDatabase db = getWritableDatabase();
        boolean insercionCorrecta = false;
        try {
            ContentValues values = new ContentValues();
            values.put("nombre", u.getNombre());
            values.put("email", u.getEmail());
            values.put("password", u.getPassword());
            long resultado = db.insert(TABLA_USUARIOS, null, values);
            insercionCorrecta = resultado != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return insercionCorrecta;
    }

    /**
     * Inserta el saldo inicial del usuario como un movimiento de tipo ingreso o gasto.
     * @param saldo Cantidad inicial.
     * @param email Correo del usuario asociado.
     * @return true si se registró el movimiento correctamente.
     */
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

    /**
     * Obtiene el nombre de un usuario a partir de su correo.
     * @param context Contexto para la conexión.
     * @param correo Email del usuario.
     * @return Nombre del usuario o cadena vacía si no se encuentra.
     */
    public static String obtenerNombreUsuario(Context context, String correo) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String nombre = "";
        String consulta = "SELECT nombre FROM usuarios WHERE email = ?";
        Cursor cursor = db.rawQuery(consulta, new String[]{correo});
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return nombre;
    }

    /**
     * Calcula el saldo total actual del usuario (Ingresos - Gastos).
     * @param context Contexto para la conexión.
     * @param correo Email del usuario.
     * @return Saldo total calculado.
     */
    public static double obtenerSaldoActual(Context context, String correo) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double saldo = 0;
        String consulta = "SELECT SUM(CASE WHEN tipo='ingreso' THEN cantidad ELSE -cantidad END) FROM movimientos m " +
                "INNER JOIN usuarios u ON m.id_usuario = u.id_usuario WHERE u.email = ?";
        Cursor cursor = db.rawQuery(consulta, new String[]{correo});
        if(cursor.moveToFirst()){
            saldo = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return saldo;
    }

    /**
     * Obtiene la suma total de ingresos históricos del usuario.
     * @param context Contexto para la conexión.
     * @param correo Email del usuario.
     * @return Suma total de ingresos.
     */
    public static double obtenerIngresos(Context context, String correo) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double ingresos = 0;
        String consulta = "SELECT SUM(cantidad) FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "WHERE u.email = ? AND tipo = 'ingreso'";
        Cursor cursor = db.rawQuery(consulta, new String[]{correo});
        if(cursor.moveToFirst()){
            ingresos = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return ingresos;
    }

    /**
     * Obtiene la suma total de gastos históricos del usuario.
     * @param context Contexto para la conexión.
     * @param correo Email del usuario.
     * @return Suma total de gastos.
     */
    public static double obtenerGastos(Context context, String correo) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double gastos = 0;
        String consulta = "SELECT SUM(cantidad) FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "WHERE u.email = ? AND tipo = 'gasto'";
        Cursor cursor = db.rawQuery(consulta, new String[]{correo});
        if(cursor.moveToFirst()){
            gastos = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return gastos;
    }

    /**
     * Obtiene la suma de ingresos del mes actual.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @return Suma de ingresos del mes en curso.
     */
    public static double obtenerIngresosMesActual(Context context, String correoUsuario) {
        double ingresos = 0;
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String consulta = "SELECT SUM(m.cantidad) FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "WHERE u.email = ? AND m.tipo = 'ingreso' AND strftime('%Y-%m', m.fecha) = strftime('%Y-%m', 'now')";
        Cursor cursor = db.rawQuery(consulta, new String[]{correoUsuario});
        if(cursor.moveToFirst()){
            ingresos = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return ingresos;
    }

    /**
     * Obtiene la suma de gastos del mes actual.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @return Suma de gastos del mes en curso.
     */
    public static double obtenerGastosMesActual(Context context, String correoUsuario) {
        double gastos = 0;
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String consulta = "SELECT SUM(m.cantidad) FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "WHERE u.email = ? AND m.tipo = 'gasto' AND strftime('%Y-%m', m.fecha) = strftime('%Y-%m', 'now')";
        Cursor cursor = db.rawQuery(consulta, new String[]{correoUsuario});
        if(cursor.moveToFirst()){
            gastos = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return gastos;
    }

    /**
     * Recupera los dos últimos movimientos realizados por el usuario.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @return Cursor con los datos de los últimos 2 movimientos.
     */
    public static Cursor obtenerUltimoMovimiento(Context context, String correoUsuario) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String consulta = "SELECT m.tipo, m.descripcion, m.cantidad, m.fecha, m.id_categoria, c.nombre " +
                "FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "INNER JOIN categorias c ON m.id_categoria = c.id_categoria WHERE u.email = ? ORDER BY m.id_movimiento DESC LIMIT 2";
        return db.rawQuery(consulta, new String[]{correoUsuario});
    }

    /**
     * Obtiene la lista de movimientos filtrada, buscada y ordenada según los parámetros.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @param filtro Tipo de movimiento ('todos', 'ingreso', 'gasto').
     * @param busqueda Texto a buscar en categoría o descripción.
     * @param orden Criterio de ordenación.
     * @return Cursor con los movimientos resultantes.
     */
    public static Cursor obtenerMovimientos(Context context, String correoUsuario, String filtro, String busqueda, String orden) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String consulta = "SELECT m.tipo, m.descripcion, m.cantidad, m.fecha, m.id_categoria, c.nombre " +
                "FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "INNER JOIN categorias c ON m.id_categoria = c.id_categoria WHERE u.email = ? ";
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

    /**
     * Inserta un nuevo movimiento financiero en la base de datos.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @param idCategoria ID de la categoría del movimiento.
     * @param tipo Tipo de movimiento ('ingreso' o 'gasto').
     * @param cantidad Importe.
     * @param descripcion Descripción opcional.
     * @param fecha Fecha del movimiento.
     * @return true si la inserción fue correcta.
     */
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

    /**
     * Obtiene la lista de nombres de categorías según el tipo indicado.
     * @param context Contexto para la conexión.
     * @param tipo Tipo de categoría ('ingreso' o 'gasto').
     * @return ArrayList con los nombres de las categorías.
     */
    public static ArrayList<String> obtenerCategorias(Context context, String tipo) {
        ArrayList<String> lista = new ArrayList<>();
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM categorias WHERE tipo = ? AND id_categoria != 14 AND id_categoria != 15", new String[]{tipo});
        while(cursor.moveToNext()){
            lista.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Obtiene el ID numérico de una categoría a partir de su nombre.
     * @param context Contexto para la conexión.
     * @param nombreCategoria Nombre de la categoría.
     * @return ID de la categoría o 0 si no existe.
     */
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

    /**
     * Obtiene los gastos totales agrupados por categoría.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @return Cursor con los nombres de categoría y el total gastado en cada una.
     */
    public static Cursor obtenerGastosPorCategoria(Context context, String correoUsuario) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String consulta = "SELECT c.nombre, SUM(m.cantidad) total FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "INNER JOIN categorias c ON m.id_categoria = c.id_categoria WHERE u.email = ? AND m.tipo = 'gasto' GROUP BY c.nombre ORDER BY total DESC";
        return db.rawQuery(consulta, new String[]{correoUsuario});
    }

    /**
     * Obtiene los ingresos totales agrupados por categoría.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @return Cursor con los nombres de categoría y el total ingresado en cada una.
     */
    public static Cursor obtenerIngresosPorCategoria(Context context, String correoUsuario) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String consulta = "SELECT c.nombre, SUM(m.cantidad) total FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "INNER JOIN categorias c ON m.id_categoria = c.id_categoria WHERE u.email = ? AND m.tipo = 'ingreso' GROUP BY c.nombre ORDER BY total DESC";
        return db.rawQuery(consulta, new String[]{correoUsuario});
    }

    /**
     * Identifica el mayor gasto individual del usuario.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @return Cadena con la categoría y el importe del mayor gasto.
     */
    public static String obtenerMayorGasto(Context context, String correoUsuario) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String resultado = "Sin datos";
        String consulta = "SELECT c.nombre, MAX(m.cantidad) FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "INNER JOIN categorias c ON m.id_categoria = c.id_categoria WHERE u.email = ? AND m.tipo = 'gasto'";
        Cursor cursor = db.rawQuery(consulta, new String[]{correoUsuario});
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            resultado = cursor.getString(0) + " · " + cursor.getDouble(1) + "€";
        }
        cursor.close();
        db.close();
        return resultado;
    }

    /**
     * Identifica el mayor ingreso individual del usuario.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @return Cadena con la categoría y el importe del mayor ingreso.
     */
    public static String obtenerMayorIngreso(Context context, String correoUsuario) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String resultado = "Sin datos";
        String consulta = "SELECT c.nombre, MAX(m.cantidad) FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "INNER JOIN categorias c ON m.id_categoria = c.id_categoria WHERE u.email = ? AND m.tipo = 'ingreso'";
        Cursor cursor = db.rawQuery(consulta, new String[]{correoUsuario});
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            resultado = cursor.getString(0) + " · " + cursor.getDouble(1) + "€";
        }
        cursor.close();
        db.close();
        return resultado;
    }

    /**
     * Obtiene la evolución de ingresos y gastos por mes del año actual.
     * @param context Contexto para la conexión.
     * @param correoUsuario Email del usuario.
     * @return Cursor con el mes y las sumas de ingresos y gastos.
     */
    public static Cursor obtenerEvolucionMensual(Context context, String correoUsuario) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String consulta = "SELECT strftime('%m', fecha) mes, SUM(CASE WHEN tipo='ingreso' THEN cantidad ELSE 0 END) ingresos, " +
                "SUM(CASE WHEN tipo='gasto' THEN cantidad ELSE 0 END) gastos FROM movimientos m INNER JOIN usuarios u ON m.id_usuario = u.id_usuario " +
                "WHERE u.email = ? AND strftime('%Y', fecha) = strftime('%Y', 'now') GROUP BY mes ORDER BY mes ASC";
        return db.rawQuery(consulta, new String[]{correoUsuario});
    }

    /**
     * Actualiza la contraseña del usuario en la base de datos.
     * @param context Contexto para la conexión.
     * @param email Correo electrónico del usuario.
     * @param nuevaPassword Nueva contraseña a establecer.
     * @return true si se actualizó correctamente el registro.
     */
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
