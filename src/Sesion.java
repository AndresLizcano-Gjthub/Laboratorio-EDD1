public class Sesion {
    int codigo;
    String cedulaAprendiz;
    String cedulaInstructor;
    String especialidad;
    String fecha; // yyyy-MM-dd

    public Sesion(int codigo, String cedulaAprendiz, String cedulaInstructor, String especialidad, String fecha) {
        this.codigo = codigo;
        this.cedulaAprendiz = cedulaAprendiz;
        this.cedulaInstructor = cedulaInstructor;
        this.especialidad = especialidad;
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return String.format("Sesion[#%d | aprendiz:%s | instructor:%s | %s | %s]",
                codigo, cedulaAprendiz, cedulaInstructor, especialidad, fecha);
    }
}
