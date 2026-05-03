package es.upm.dit.isst.lab_datos.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String acronimo;
    private String curso;
    private String semestre;

    @ManyToOne
    @JoinColumn(name = "id_departamento")
    private Departamento departamento;

    @ManyToMany(mappedBy = "asignaturas")
    @JsonIgnore
    private List<Profesor> profesores;
}