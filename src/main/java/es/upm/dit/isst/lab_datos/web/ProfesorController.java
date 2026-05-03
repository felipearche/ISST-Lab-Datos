package es.upm.dit.isst.lab_datos.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.upm.dit.isst.lab_datos.domain.DespachoCompartidoDTO;
import es.upm.dit.isst.lab_datos.domain.Profesor;
import es.upm.dit.isst.lab_datos.domain.ProfesorCargaDTO;
import es.upm.dit.isst.lab_datos.domain.ProfesorRepository;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {

    @Autowired
    private ProfesorRepository profesorRepository;

    // GET /api/profesores
    @GetMapping
    public List<Profesor> getAll() {
        return profesorRepository.findAll();
    }

    // GET /api/profesores/nrp/123456
    @GetMapping("/nrp/{nrp}")
    public ResponseEntity<Profesor> getByNrp(@PathVariable String nrp) {
        return profesorRepository.findByNrp(nrp)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/profesores/buscar?nombre=Juan
    @GetMapping("/buscar")
    public List<Profesor> searchByNombre(@RequestParam String nombre) {
        return profesorRepository.findByNombreContaining(nombre);
    }

    // TAREA 1: GET /api/profesores/carga-pesada
    @GetMapping("/carga-pesada")
    public ResponseEntity<List<ProfesorCargaDTO>> getProfesoresConMuchasAsignaturas() {
        List<ProfesorCargaDTO> lista = profesorRepository.findProfesoresCargaPesada2();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    // TAREA 2: GET /api/profesores/despachos-compartidos
    @GetMapping("/despachos-compartidos")
    public List<DespachoCompartidoDTO> getDespachosCompartidos() {
        List<Object[]> resultados = profesorRepository.findProfesoresQueCompartenDespacho();
        return resultados.stream()
            .map(fila -> new DespachoCompartidoDTO(
                (String) fila[0],
                (String) fila[1],
                (String) fila[2]
            ))
            .toList();
    }
}