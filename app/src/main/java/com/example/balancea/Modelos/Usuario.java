package com.example.balancea.Modelos;

/**
 * Modelo que representa a un usuario de la aplicación.
 * Contiene la información básica del perfil y las credenciales.
 */
public class Usuario {

    private String nombre, email, password;

    /**
     * Constructor para crear un nuevo usuario.
     * @param nombre Nombre del usuario.
     * @param email Correo electrónico único.
     * @param password Contraseña de acceso.
     */
    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    /** @return Nombre del usuario. */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre Nuevo nombre a establecer. */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return Correo electrónico del usuario. */
    public String getEmail() {
        return email;
    }

    /** @param email Nuevo correo a establecer. */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return Contraseña del usuario. */
    public String getPassword() {
        return password;
    }

    /** @param password Nueva contraseña a establecer. */
    public void setPassword(String password) {
        this.password = password;
    }
}
