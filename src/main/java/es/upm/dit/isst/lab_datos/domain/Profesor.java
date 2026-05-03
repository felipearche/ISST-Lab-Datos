package es.upm.dit.isst.lab_datos.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nrp;
    private String nombre;
    private String despacho;
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_departamento")
    private Departamento departamento;

    @ManyToMany
    @JoinTable(
        name = "profesor_imparte_asignatura",
        joinColumns = @JoinColumn(name = "id_profesor"),
        inverseJoinColumns = @JoinColumn(name = "id_asignatura")
    )
    private List<Asignatura> asignaturas;
}