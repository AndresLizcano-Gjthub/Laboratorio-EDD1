import java.io.*;
import java.util.*;

public class GestorAprendices {
    private final String archivoDatos;
    private final String archivoIndice;
    private final Map<String, Long> indice = new HashMap<>();

    public GestorAprendices(String archivoDatos, String archivoIndice) {
        this.archivoDatos = archivoDatos;
        this.archivoIndice = archivoIndice;
        cargarIndice();
    }

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
            System.err.println("Error al cargar índice de aprendices: " + e.getMessage());
        }
    }

    private void guardarIndice() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(archivoIndice))) {
            for (Map.Entry<String, Long> e : indice.entrySet()) {
                out.writeUTF(e.getKey());
                out.writeLong(e.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar índice de aprendices: " + e.getMessage());
        }
    }

    private long escribirAlFinal(Aprendiz a) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(archivoDatos, "rw")) {
            long pos = raf.length();
            raf.seek(pos);
            raf.writeUTF(a.nombre);
            raf.writeUTF(a.cedula);
            raf.writeUTF(a.especialidad);
            raf.writeInt(a.sesionesMes);
            return pos;
        }
    }

    private Aprendiz leerEnPosicion(long pos) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(archivoDatos, "r")) {
            raf.seek(pos);
            String nombre = raf.readUTF();
            String cedula = raf.readUTF();
            String especialidad = raf.readUTF();
            int sesiones = raf.readInt();
            return new Aprendiz(nombre, cedula, especialidad, sesiones);
        }
    }

    public boolean crear(Aprendiz nuevo) throws IOException {
        if (indice.containsKey(nuevo.cedula)) {
            System.out.println(">> Ya existe un aprendiz con esa cédula.");
            return false;
        }
        long pos = escribirAlFinal(nuevo);
        indice.put(nuevo.cedula, pos);
        guardarIndice();
        return true;
    }

    public Aprendiz buscarPorCedula(String cedula) throws IOException {
        Long pos = indice.get(cedula);
        if (pos == null) return null;
        return leerEnPosicion(pos);
    }

    public boolean actualizar(String cedula, Aprendiz datosNuevos) throws IOException {
        if (!indice.containsKey(cedula)) {
            System.out.println(">> No existe aprendiz con esa cédula.");
            return false;
        }
        datosNuevos.cedula = cedula;
        long pos = escribirAlFinal(datosNuevos);
        indice.put(cedula, pos);
        guardarIndice();
        return true;
    }

    public boolean eliminar(String cedula) throws IOException {
        if (!indice.containsKey(cedula)) {
            System.out.println(">> No existe aprendiz con esa cédula.");
            return false;
        }
        indice.remove(cedula);
        guardarIndice();
        return true;
    }

    public List<Aprendiz> listar() throws IOException {
        List<Aprendiz> lista = new ArrayList<>();
        for (long pos : indice.values()) {
            lista.add(leerEnPosicion(pos));
        }
        return lista;
    }

    public void ajustarSesiones(String cedula, int delta) throws IOException {
        Aprendiz a = buscarPorCedula(cedula);
        if (a == null) return;
        a.sesionesMes = Math.max(0, a.sesionesMes + delta);
        actualizar(cedula, a);
    }

    public void reiniciarContadoresMensuales() throws IOException {
        for (String cedula : new ArrayList<>(indice.keySet())) {
            Aprendiz a = buscarPorCedula(cedula);
            a.sesionesMes = 0;
            actualizar(cedula, a);
        }
    }
}
