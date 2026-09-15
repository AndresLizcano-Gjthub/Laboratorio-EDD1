public class Instructor {
    static final int MAX_SESIONES_MES = 15;

    String nombre;
    String cedula;
    String especialidad;
    String telefono;
    int sesionesMes;

    public Instructor(String nombre, String cedula, String especialidad, String telefono, int sesionesMes) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.sesionesMes = sesionesMes;
    }

    @Override
    public String toString() {
        return String.format("Instructor[%s | %s | %s | tel:%s | sesiones:%d/%d]",
                nombre, cedula, especialidad, telefono, sesionesMes, MAX_SESIONES_MES);
    }
}
