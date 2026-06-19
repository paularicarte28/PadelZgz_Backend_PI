package com.padelzgz.api.controller;

import com.padelzgz.api.dto.ReservaInDTO;
import com.padelzgz.api.dto.ReservaOutDTO;
import com.padelzgz.api.exception.*;
import com.padelzgz.api.model.Reserva;
import com.padelzgz.api.service.ReservaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    @Autowired private ReservaService reservaService;

    private ReservaOutDTO toDTO(Reserva r) {
        ReservaOutDTO dto = new ReservaOutDTO();
        dto.setId(r.getId());
        dto.setFecha(r.getFecha());
        dto.setHoraInicio(r.getHoraInicio());
        dto.setHoraFin(r.getHoraFin());
        dto.setPrecio(r.getPrecio());
        dto.setPagado(r.isPagado());
        dto.setComentario(r.getComentario());
        if (r.getPista() != null) {
            dto.setPistaId(r.getPista().getId());
            dto.setPistaNumero(r.getPista().getNumero());
            dto.setPistaTipo(r.getPista().getTipo());
            dto.setPistaSuperficie(r.getPista().getSuperficie());
        }
        if (r.getUsuario() != null) {
            dto.setUsuarioId(r.getUsuario().getId());
            dto.setUsuarioNombre(r.getUsuario().getNombre());
            dto.setUsuarioApellidos(r.getUsuario().getApellidos());
            dto.setUsuarioEmail(r.getUsuario().getEmail());
        }
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<ReservaOutDTO>> getReservas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) String pagado) {
        logger.info("GET /reservas fecha={} pagado={}", fecha, pagado);
        Set<Reserva> reservas;
        if (fecha != null && pagado != null) {
            reservas = reservaService.findByFechaAndPagado(fecha, Boolean.parseBoolean(pagado));
        } else if (fecha != null) {
            reservas = reservaService.findByFecha(fecha);
        } else if (pagado != null) {
            reservas = reservaService.findByPagado(Boolean.parseBoolean(pagado));
        } else {
            reservas = reservaService.findAll();
        }
        return ResponseEntity.ok(reservas.stream().map(this::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReserva(@PathVariable long id) {
        logger.info("GET /reservas/{}", id);
        return ResponseEntity.ok(reservaService.findById(id).orElseThrow(() -> new ReservaNotFoundException(id)));
    }

    @GetMapping("/pista/{pistaId}")
    public ResponseEntity<List<ReservaOutDTO>> getReservasByPista(@PathVariable long pistaId) {
        logger.info("GET /reservas/pista/{}", pistaId);
        return ResponseEntity.ok(reservaService.findByPistaId(pistaId).stream().map(this::toDTO).toList());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaOutDTO>> getReservasByUsuario(@PathVariable long usuarioId) {
        logger.info("GET /reservas/usuario/{}", usuarioId);
        return ResponseEntity.ok(reservaService.findByUsuarioId(usuarioId).stream().map(this::toDTO).toList());
    }

    @PostMapping
    public ResponseEntity<Reserva> addReserva(@Valid @RequestBody ReservaInDTO dto) {
        logger.info("POST /reservas pistaId={} usuarioId={}", dto.getPistaId(), dto.getUsuarioId());
        Reserva created = reservaService.addReserva(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> modifyReserva(@PathVariable long id, @Valid @RequestBody ReservaInDTO dto) {
        logger.info("PUT /reservas/{}", id);
        return ResponseEntity.ok(reservaService.modifyReserva(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Reserva> patchReserva(@PathVariable long id, @RequestBody Reserva partial) {
        logger.info("PATCH /reservas/{}", id);
        return ResponseEntity.ok(reservaService.patchReserva(id, partial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable long id) {
        logger.info("DELETE /reservas/{}", id);
        reservaService.deleteReserva(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ReservaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ReservaNotFoundException e) {
        logger.error("Reserva no encontrada: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.notFound(e.getMessage()));
    }

    @ExceptionHandler({PistaNotFoundException.class, UsuarioNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleRelatedNotFound(RuntimeException e) {
        logger.error("Entidad relacionada no encontrada: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.notFound(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err -> {
            errors.put(((FieldError) err).getField(), err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(ErrorResponse.validationError(errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        logger.error("Error en ReservaController: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(ErrorResponse.internalError());
    }
}