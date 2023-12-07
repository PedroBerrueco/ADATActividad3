import entity.Autor;
import entity.Libro;
import org.hibernate.Session;

import java.util.List;


public class Main {

    private static Session session = null;

    public static void main(String[] args) {

        try {
            session = HibernateUtil.getSession();

            crearAutor("1000000A", "Paco");
            crearAutor("3000000C", "Pepe");
            crearAutor("4000000D", "Carlos");
            mostrarAutor("1000000A");
            actualizarAutor("1000000A", "Paquito" );
            eliminarAutor("3000000C");

            Autor paquito = obtenerAutor("Paquito");
            crearLibro("Las cronicas de Paquito", paquito);

            Autor carlos = obtenerAutor("Carlos");
            crearLibro("Las cronicas de Carlos", carlos);
            crearLibro("Yo, yo mismo y Carlos", carlos);
            crearLibro("Carlos, mi vida", carlos);


            Integer isbn = obtenerISBN("Las cronicas de Paquito");
            mostrarLibro(isbn);
            actualizarLibro(isbn, "Las flores azules", carlos);
            eliminarLibro(isbn);

            mostarTodosLosAutores();
            obtenerLibrosDeUnAutor("4000000D");
            encontrarAutoresConMasDeDosLibros();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession(session);
        }
    }



    private static void mostarTodosLosAutores() {
        session.beginTransaction();
        List<Autor> dataList = session.createQuery("FROM Autor ", Autor.class).list();
        session.getTransaction().commit();

        for (Autor author : dataList) {
            System.out.println("DNI: " + author.getDni() + ", Nombre: " + author.getNombre());
        }
    }

    private static void obtenerLibrosDeUnAutor(String dni) {
        // Consulta del autor
        Autor author = session.get(Autor.class, dni);

        if (author != null) {
            // Consulta de los libros del autor
            List<Libro> librosDelAutor = session.createQuery("FROM Libro WHERE autor = :autor", Libro.class)
                    .setParameter("autor", author)
                    .list();

            if (!librosDelAutor.isEmpty()) {
                System.out.println("Libros del autor " + author.getNombre() + ":");
                for (Libro libro : librosDelAutor) {
                    System.out.println("ISBN: " + libro.getIsbn() + ", Título: " + libro.getTitulo());
                }
            } else {
                System.out.println("El autor con DNI " + dni + " no tiene libros registrados.");
            }
        } else {
            System.out.println("No se encontró el autor con el DNI: " + dni);
        }
    }


    private static void encontrarAutoresConMasDeDosLibros() {
        List<Autor> autores = session.createQuery(
                        "SELECT DISTINCT a FROM Libro l JOIN l.autor a GROUP BY a.dni HAVING COUNT(l) > 2",
                        Autor.class)
                .list();

        if (!autores.isEmpty()) {
            System.out.println("Autores con más de dos libros registrados:");
            for (Autor autor : autores) {
                System.out.println("DNI: " + autor.getDni() + ", Nombre: " + autor.getNombre());
            }
        } else {
            System.out.println("No se encontraron autores con más de dos libros registrados.");
        }
    }

    //METODOS PARA EL AUTOR
    public static void crearAutor(String dni, String nombre) {
        session.beginTransaction();

        // Verificar si ya existe un autor con el mismo DNI
        Autor existingAuthor = session.get(Autor.class, dni);
        if (existingAuthor != null) {
            System.out.println("Ya existe un autor con el DNI: " + dni + ". No se creará uno nuevo.");
            session.getTransaction().commit();
            return; // Salir del método si ya existe el autor
        }

        // Crear un nuevo autor si no existe
        Autor author = new Autor();
        author.setDni(dni);
        author.setNombre(nombre);
        System.out.println(author.toString());
        session.persist(author);

        session.getTransaction().commit();

        System.out.println("Autor creado");
    }


    public static void mostrarAutor(String dni) {
        // Definir la consulta
        String hql = "FROM Autor WHERE dni = :dni";

        // Ejecutar la consulta
        Autor author = session.createQuery(hql, Autor.class)
                .setParameter("dni", dni)
                .uniqueResult();

        if (author != null) {
            System.out.println("Se mostrará al Autor, DNI: " + author.getDni() + ", Nombre: " + author.getNombre());
        } else {
            System.out.println("No se encontro el autor con el dnie: " + dni);
        }
    }

    public static void actualizarAutor(String dni, String nuevoNombre) {
        // Cargar la entidad que deseas actualuzar
        Autor author = session.get(Autor.class, dni);

        if (author != null) {
            // Actualizamos la propiedad que querramos
            author.setNombre(nuevoNombre);

            // Iniciar la transaccion y eliminar la entidad
            session.beginTransaction();
            session.merge(author);
            session.getTransaction().commit();

            System.out.println("Autor actualizado correctamente.");
        } else {
            System.out.println("No se encontro el autor con el ID: " + dni);
        }
    }

    public static void eliminarAutor(String dni) {
        // Cargar la entidad que deseas eliminar
        Autor author = session.get(Autor.class, dni);

        if (author != null) {
            // Iniciar la transaccio y eliminar la entidad
            session.beginTransaction();
            session.remove(author);
            session.getTransaction().commit();

            System.out.println("Autor eliminado correctamente.");
        } else {
            System.out.println("No se encontro el autor con el ID: " + dni);
        }
    }

    public static Autor obtenerAutor(String nombre) {
        return session.createQuery("FROM Autor WHERE nombre = :nombre", Autor.class)
                .setParameter("nombre", nombre)
                .uniqueResult();
    }

    //METODOS PARA EL LIBRO
    public static void crearLibro(String titulo, Autor author) {
        session.beginTransaction();

        Libro book = new Libro();
        book.setTitulo(titulo);
        book.setAutor(author);
        session.persist(book);

        session.getTransaction().commit();

        System.out.println("Libro creado");
    }


    public static void mostrarLibro(Integer isbn) {
        // Definir la consulta
        String hql = "FROM Libro WHERE isbn = :isbn";

        // Ejecutar la consulta
        Libro book = session.createQuery(hql, Libro.class)
                .setParameter("isbn", isbn)
                .uniqueResult();

        if (book != null) {
            System.out.println("ISBN: " + book.getIsbn() + ", Título: " + book.getTitulo() +
                    ", Autor: " + book.getAutor().getNombre()); // Acceder a la propiedad del autor
        } else {
            System.out.println("No se encontró el libro con el ISBN: " + isbn);
        }
    }


    public static void actualizarLibro(Integer isbn, String nuevoTitulo, Autor nuevoAutor) {
        // Cargar la entidad que deseas actualizar
        Libro book = session.get(Libro.class, isbn);

        if (book != null) {
            // Actualizamos la propiedad que querramos
            book.setTitulo(nuevoTitulo);
            book.setAutor(nuevoAutor);

            // Iniciar la transacción y actualizar la entidad
            session.beginTransaction();
            session.merge(book);
            session.getTransaction().commit();

            System.out.println("Libro actualizado correctamente.");
        } else {
            System.out.println("No se encontró el libro con el ISBN: " + isbn);
        }
    }


    public static void eliminarLibro(Integer  isbn) {
        // Cargar la entidad que deseas eliminar
        Libro book = session.get(Libro.class, isbn);

        if (book != null) {
            // Iniciar la transaccio y eliminar la entidad
            session.beginTransaction();
            session.remove(book);
            session.getTransaction().commit();

            System.out.println("Libro eliminado correctamente.");
        } else {
            System.out.println("No se encontro el libro con el ID: " + isbn);
        }
    }

    public static int obtenerISBN(String titulo) {
        Libro libro = session.createQuery("FROM Libro WHERE titulo = :titulo", Libro.class)
                .setParameter("titulo", titulo)
                .uniqueResult();

        if (libro != null) {
            return libro.getIsbn();
        } else {
            // Si el libro no se encuentra devuelve el valor 0 que no existe en BD
            return 0;
        }
    }


}


