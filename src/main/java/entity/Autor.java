package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Autor {
    @Id
    @Column(name = "dni")
    private String dni;
    @Basic
    @Column(name = "nombre")
    private String nombre;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    private List<Libro> libros = new ArrayList<>();


    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Autor autor = (Autor) o;
        return Objects.equals(dni, autor.dni) && Objects.equals(nombre, autor.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni, nombre);
    }

    @Override
    public String toString() {
        return "Autor{" +
                "dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", libros=" + libros +
                '}';
    }
}
