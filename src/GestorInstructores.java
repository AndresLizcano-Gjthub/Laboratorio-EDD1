import java.io.*;
import java.util.*;

public class GestorInstructores {
    private final String archivoDatos;
    private final String archivoIndice;
    // Índice en memoria: cédula -> posición en bytes dentro del archivo de datos
    private final Map<String, Long> indice = new HashMap<>();

    public GestorInstructores(String archivoDatos, String archivoIndice) {
        this.archivoDatos = archivoDatos;
        this.archivoIndice = archivoIndice;
        cargarIndice();
    }

    /** Lee el archivo de índice a memoria al arrancar el programa. */
    private void cargarIndice() {
        File f = new File(archivoIndice);
        if (!f.exists()) return;
        try (DataInputStream in = new DataInputStream(new FileInputStream(f))) {
            while (in.available() > 0) {
                String cedula = in.readUTF();
                long pos = in.readLong();
                indice.put(cedula, pos);
            }
        } catch (IOException e) {
            System.err.println("Error al cargar índice de instructores: " + e.getMessage());
        }
    }

    /** Persiste el índice completo a disco. Se llama después de cada cambio. */
    private void guardarIndice() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(archivoIndice))) {
            for (Map.Entry<String, Long> e : indice.entrySet()) {
                out.writeUTF(e.getKey());
                out.writeLong(e.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar índice de instructores: " + e.getMessage());
        }
    }

    /** Escribe un instructor SIEMPRE al final del archivo y devuelve la posición donde quedó. */
    private long escribirAlFinal(Instructor i) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(archivoDatos, "rw")) {
            long pos = raf.length();
            raf.seek(pos);
            raf.writeUTF(i.nombre);
            raf.writeUTF(i.cedula);
            raf.writeUTF(i.especialidad);
            raf.writeUTF(i.telefono);
            raf.writeInt(i.sesionesMes);
            return pos;
        }
    }

    private Instructor leerEnPosicion(long pos) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(archivoDatos, "r")) {
            raf.seek(pos);
            String nombre = raf.readUTF();
            String cedula = raf.readUTF();
            String especialidad = raf.readUTF();
            String telefono = raf.readUTF();
            int sesiones = raf.readInt();
            return new Instructor(nombre, cedula, especialidad, telefono, sesiones);
        }
    }

    public boolean crear(Instructor nuevo) throws IOException {
        if (indice.containsKey(nuevo.cedula)) {
            System.out.println(">> Ya existe un instructor con esa cédula.");
            return false;
        }
        long pos = escribirAlFinal(nuevo);
        indice.put(nuevo.cedula, pos);
        guardarIndice();
        return true;
    }

    public Instructor buscarPorCedula(String cedula) throws IOException {
        Long pos = indice.get(cedula);
        if (pos == null) return null;
        return leerEnPosicion(pos);
    }

    /** "Actualizar" escribe un registro nuevo al final y mueve el puntero del índice hacia él. */
    public boolean actualizar(String cedula, Instructor datosNuevos) throws IOException {
        if (!indice.containsKey(cedula)) {
            System.out.println(">> No existe instructor con esa cédula.");
            return false;
        }
        datosNuevos.cedula = cedula;
        long pos = escribirAlFinal(datosNuevos);
        indice.put(cedula, pos);
        guardarIndice();
        return true;
    }

    /** "Eliminar" simplemente saca la cédula del índice: ya no es alcanzable, aunque sus bytes sigan en el archivo. */
    public boolean eliminar(String cedula) throws IOException {
        if (!indice.containsKey(cedula)) {
            System.out.println(">> No existe instructor con esa cédula.");
            return false;
        }
        indice.remove(cedula);
        guardarIndice();
        return true;
    }

    public List<Instructor> listar() throws IOException {
        List<Instructor> lista = new ArrayList<>();
        for (long pos : indice.values()) {
            lista.add(leerEnPosicion(pos));
        }
        return lista;
    }

    public void ajustarSesiones(String cedula, int delta) throws IOException {
        Instructor i = buscarPorCedula(cedula);
        if (i == null) return;
        i.sesionesMes = Math.max(0, i.sesionesMes + delta);
        actualizar(cedula, i);
    }

    public void reiniciarContadoresMensuales() throws IOException {
        for (String cedula : new ArrayList<>(indice.keySet())) {
            Instructor i = buscarPorCedula(cedula);
            i.sesionesMes = 0;
            actualizar(cedula, i);
        }
    }
}
