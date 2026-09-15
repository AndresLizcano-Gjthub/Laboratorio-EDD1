import java.io.*;
import java.util.*;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        GestorInstructores gestorInstructores = new GestorInstructores("instructores.dat", "instructores.idx");
        GestorAprendices gestorAprendices = new GestorAprendices("aprendices.dat", "aprendices.idx");
        GestorSesiones gestorSesiones = new GestorSesiones("sesiones.dat", "sesiones.idx", gestorInstructores, gestorAprendices);

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            try {
                switch (opcion) {
                    case 1 -> crearInstructor(gestorInstructores);
                    case 2 -> crearAprendiz(gestorAprendices);
                    case 3 -> listarInstructores(gestorInstructores);
                    case 4 -> listarAprendices(gestorAprendices);
                    case 5 -> asignarSesion(gestorSesiones);
                    case 6 -> cancelarSesion(gestorSesiones);
                    case 7 -> listarSesiones(gestorSesiones);
                    case 8 -> {
                        gestorInstructores.reiniciarContadoresMensuales();
                        gestorAprendices.reiniciarContadoresMensuales();
                        System.out.println(">> Contadores mensuales reiniciados.");
                    }
                    case 9 -> eliminarInstructor(gestorInstructores);
                    case 10 -> eliminarAprendiz(gestorAprendices);
                    case 0 -> System.out.println("Hasta luego.");
                    default -> System.out.println(">> Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println(">> Error: " + e.getMessage());
            }
            System.out.println();
        } while (opcion != 0);
    }

    static void mostrarMenu() {
        System.out.println("===== ACADEMIA DE ARTES BARRANQUILLA =====");
        System.out.println("1. Crear instructor");
        System.out.println("2. Crear aprendiz");
        System.out.println("3. Listar instructores");
        System.out.println("4. Listar aprendices");
        System.out.println("5. Asignar sesión");
        System.out.println("6. Cancelar sesión");
        System.out.println("7. Listar sesiones");
        System.out.println("8. Reiniciar contadores mensuales");
        System.out.println("9. Eliminar instructor");
        System.out.println("10. Eliminar aprendiz");
        System.out.println("0. Salir");
    }

    static void crearInstructor(GestorInstructores g) throws IOException {
        System.out.print("Nombre: "); String nombre = sc.nextLine();
        System.out.print("Cédula: "); String cedula = sc.nextLine();
        System.out.print("Especialidad: "); String especialidad = sc.nextLine();
        System.out.print("Teléfono: "); String telefono = sc.nextLine();
        if (g.crear(new Instructor(nombre, cedula, especialidad, telefono, 0))) {
            System.out.println(">> Instructor creado.");
        }
    }

    static void crearAprendiz(GestorAprendices g) throws IOException {
        System.out.print("Nombre: "); String nombre = sc.nextLine();
        System.out.print("Cédula: "); String cedula = sc.nextLine();
        System.out.print("Especialidad: "); String especialidad = sc.nextLine();
        if (g.crear(new Aprendiz(nombre, cedula, especialidad, 0))) {
            System.out.println(">> Aprendiz creado.");
        }
    }

    static void listarInstructores(GestorInstructores g) throws IOException {
        List<Instructor> lista = g.listar();
        if (lista.isEmpty()) { System.out.println("(sin instructores)"); return; }
        for (Instructor i : lista) System.out.println(i);
    }

    static void listarAprendices(GestorAprendices g) throws IOException {
        List<Aprendiz> lista = g.listar();
        if (lista.isEmpty()) { System.out.println("(sin aprendices)"); return; }
        for (Aprendiz a : lista) System.out.println(a);
    }

    static void asignarSesion(GestorSesiones g) throws IOException {
        System.out.print("Cédula aprendiz: "); String cedAp = sc.nextLine();
        System.out.print("Cédula instructor: "); String cedIn = sc.nextLine();
        System.out.print("Especialidad: "); String esp = sc.nextLine();
        System.out.print("Fecha (yyyy-MM-dd): "); String fecha = sc.nextLine();
        Sesion s = g.asignarSesion(cedAp, cedIn, esp, fecha);
        if (s != null) System.out.println(">> Sesión creada: " + s);
    }

    static void cancelarSesion(GestorSesiones g) throws IOException {
        int codigo = leerEntero("Código de la sesión a cancelar: ");
        if (g.cancelarSesion(codigo)) System.out.println(">> Sesión cancelada.");
    }

    static void listarSesiones(GestorSesiones g) throws IOException {
        List<Sesion> lista = g.listar();
        if (lista.isEmpty()) { System.out.println("(sin sesiones)"); return; }
        for (Sesion s : lista) System.out.println(s);
    }

    static void eliminarInstructor(GestorInstructores g) throws IOException {
        System.out.print("Cédula del instructor a eliminar: "); String cedula = sc.nextLine();
        if (g.eliminar(cedula)) System.out.println(">> Instructor eliminado.");
    }

    static void eliminarAprendiz(GestorAprendices g) throws IOException {
        System.out.print("Cédula del aprendiz a eliminar: "); String cedula = sc.nextLine();
        if (g.eliminar(cedula)) System.out.println(">> Aprendiz eliminado.");
    }

    static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Eso no es un número. " + mensaje);
        }
        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }
}
