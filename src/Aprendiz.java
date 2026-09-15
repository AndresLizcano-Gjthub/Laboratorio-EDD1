public class Aprendiz {
    static final int MAX_SESIONES_MES = 4;

    String nombre;
    String cedula;
    String especialidad;
    int sesionesMes;

    public Aprendiz(String nombre, String cedula, String especialidad, int sesionesMes) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.especialidad = especialidad;
        this.sesionesMes = sesionesMes;
    }

    @Override
    public String toString() {
        return String.format("Aprendiz[%s | %s | %s | sesiones:%d/%d]",
                nombre, cedula, especialidad, sesionesMes, MAX_SESIONES_MES);
    }
}
