package es.upm.dit.isst.lab_datos.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String acronimo;

    @OneToMany(mappedBy = "departamento")
    @JsonIgnore
    private List<Asignatura> asignaturas;

    @OneToMany(mappedBy = "departamento")
    @JsonIgnore
    private List<Profesor> profesores;
}