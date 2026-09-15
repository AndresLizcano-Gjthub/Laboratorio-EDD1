import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GestorSesiones {
    private final String archivoDatos;
    private final String archivoIndice;
    private final Map<Integer, Long> indice = new HashMap<>();
    private final GestorInstructores gestorInstructores;
    private final GestorAprendices gestorAprendices;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public GestorSesiones(String archivoDatos, String archivoIndice, GestorInstructores gi, GestorAprendices ga) {
        this.archivoDatos = archivoDatos;
        this.archivoIndice = archivoIndice;
        this.gestorInstructores = gi;
        this.gestorAprendices = ga;
        cargarIndice();
    }

    private void cargarIndice() {
        File f = new File(archivoIndice);
        if (!f.exists()) return;
        try (DataInputStream in = new DataInputStream(new FileInputStream(f))) {
            while (in.available() > 0) {
                int codigo = in.readInt();
                long pos = in.readLong();
                indice.put(codigo, pos);
            }
        } catch (IOException e) {
            System.err.println("Error al cargar índice de sesiones: " + e.getMessage());
        }
    }

    private void guardarIndice() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(archivoIndice))) {
            for (Map.Entry<Integer, Long> e : indice.entrySet()) {
                out.writeInt(e.getKey());
                out.writeLong(e.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar índice de sesiones: " + e.getMessage());
        }
    }

    private long escribirAlFinal(Sesion s) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(archivoDatos, "rw")) {
            long pos = raf.length();
            raf.seek(pos);
            raf.writeInt(s.codigo);
            raf.writeUTF(s.cedulaAprendiz);
            raf.writeUTF(s.cedulaInstructor);
            raf.writeUTF(s.especialidad);
            raf.writeUTF(s.fecha);
            return pos;
        }
    }

    private Sesion leerEnPosicion(long pos) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(archivoDatos, "r")) {
            raf.seek(pos);
            int codigo = raf.readInt();
            String cedulaAprendiz = raf.readUTF();
            String cedulaInstructor = raf.readUTF();
            String especialidad = raf.readUTF();
            String fecha = raf.readUTF();
            return new Sesion(codigo, cedulaAprendiz, cedulaInstructor, especialidad, fecha);
        }
    }

    private int siguienteCodigo() {
        if (indice.isEmpty()) return 1;
        return Collections.max(indice.keySet()) + 1;
    }

    public Sesion asignarSesion(String cedulaAprendiz, String cedulaInstructor, String especialidad, String fecha) throws IOException {
        Instructor instructor = gestorInstructores.buscarPorCedula(cedulaInstructor);
        if (instructor == null) {
            System.out.println(">> No existe ese instructor.");
            return null;
        }
        Aprendiz aprendiz = gestorAprendices.buscarPorCedula(cedulaAprendiz);
        if (aprendiz == null) {
            System.out.println(">> No existe ese aprendiz.");
            return null;
        }
        if (instructor.sesionesMes >= Instructor.MAX_SESIONES_MES) {
            System.out.println(">> El instructor ya alcanzó el máximo de sesiones del mes (15).");
            return null;
        }
        if (aprendiz.sesionesMes >= Aprendiz.MAX_SESIONES_MES) {
            System.out.println(">> El aprendiz ya alcanzó el máximo de sesiones del mes (4).");
            return null;
        }

        Sesion nueva = new Sesion(siguienteCodigo(), cedulaAprendiz, cedulaInstructor, especialidad, fecha);
        long pos = escribirAlFinal(nueva);
        indice.put(nueva.codigo, pos);
        guardarIndice();

        gestorInstructores.ajustarSesiones(cedulaInstructor, +1);
        gestorAprendices.ajustarSesiones(cedulaAprendiz, +1);

        return nueva;
    }

    /** Cancela (=elimina del índice) una sesión, solo si su fecha todavía no ha pasado. */
    public boolean cancelarSesion(int codigo) throws IOException {
        Long pos = indice.get(codigo);
        if (pos == null) {
            System.out.println(">> No existe una sesión con ese código.");
            return false;
        }
        Sesion s = leerEnPosicion(pos);

        LocalDate fechaSesion = LocalDate.parse(s.fecha, FORMATO_FECHA);
        if (!fechaSesion.isAfter(LocalDate.now())) {
            System.out.println(">> No se puede cancelar: la fecha de la sesión ya pasó.");
            return false;
        }

        indice.remove(codigo);
        guardarIndice();

        gestorInstructores.ajustarSesiones(s.cedulaInstructor, -1);
        gestorAprendices.ajustarSesiones(s.cedulaAprendiz, -1);

        return true;
    }

    public Sesion buscarPorCodigo(int codigo) throws IOException {
        Long pos = indice.get(codigo);
        if (pos == null) return null;
        return leerEnPosicion(pos);
    }

    public List<Sesion> listar() throws IOException {
        List<Sesion> lista = new ArrayList<>();
        for (long pos : indice.values()) {
            lista.add(leerEnPosicion(pos));
        }
        return lista;
    }
}
