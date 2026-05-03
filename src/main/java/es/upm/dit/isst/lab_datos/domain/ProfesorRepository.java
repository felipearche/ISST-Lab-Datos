package es.upm.dit.isst.lab_datos.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

    Optional<Profesor> findByNrp(String nrp);
    List<Profesor> findByNombre(String nombre);
    List<Profesor> findByDespacho(String despacho);
    List<Profesor> findByNombreContaining(String nombre);
    List<Profesor> findByEmailEndingWith(String dominio);
    List<Profesor> findByNombreAndDespacho(String nombre, String despacho);
    List<Profesor> findByDespachoOrDespacho(String despacho1, String despacho2);
    List<Profesor> findByDepartamentoAcronimo(String acronimo);

    @Query("SELECT p FROM Profesor p JOIN p.departamento d WHERE d.acronimo = :acronimo")
    List<Profesor> findByDepartamentoAcronimo2(String acronimo);

    @Query("SELECT p.departamento.nombre, COUNT(p) FROM Profesor p " +
           "GROUP BY p.departamento.nombre " +
           "HAVING COUNT(p) > :minimo")
    List<Object[]> countProfesoresByDepartamentoHaving(Long minimo);

    @Query("SELECT p FROM Profesor p JOIN p.asignaturas a WHERE a.acronimo = :acronimoAsignatura")
    List<Profesor> findByAsignaturaImpartida(String acronimoAsignatura);

    // TAREA 1 - con SQL nativo
    @Query(value = "SELECT p.nombre, COUNT(pa.asignatura_id) as total " +
               "FROM profesor p " +
               "JOIN profesor_imparte_asignatura pa ON p.id = pa.profesor_id " +
               "GROUP BY p.id, p.nombre " +
               "HAVING COUNT(pa.asignatura_id) > 2", 
       nativeQuery = true)
    List<Object[]> findProfesoresCargaPesada();

    // TAREA 1 - con JPQL + DTO
    @Query("SELECT new es.upm.dit.isst.lab_datos.domain.ProfesorCargaDTO(p.nombre, COUNT(a)) " +
           "FROM Profesor p JOIN p.asignaturas a " +
           "GROUP BY p.id, p.nombre " +
           "HAVING COUNT(a) > 2")
    List<ProfesorCargaDTO> findProfesoresCargaPesada2();

    // TAREA 2 - despachos compartidos
    @Query(value = "SELECT p1.Despacho, p1.Nombre AS Profesor_1, p2.Nombre AS Profesor_2 " +
           "FROM Profesor p1 JOIN Profesor p2 ON p1.Despacho = p2.Despacho " +
           "WHERE p1.Id < p2.Id " +
           "ORDER BY p1.Despacho",
           nativeQuery = true)
    List<Object[]> findProfesoresQueCompartenDespacho();
}