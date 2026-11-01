package com.sixt.excepcion;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Unico lugar donde las excepciones se convierten en respuestas HTTP.
// Asi los controladores y servicios no tienen que saber de codigos de estado.
@RestControllerAdvice
public class ManejadorDeErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorDeErrores.class);

    @ExceptionHandler(AccesoDenegadoExcepcion.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RespuestaError accesoDenegado(AccesoDenegadoExcepcion e) {
        return new RespuestaError(e.getMessage());
    }

    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RespuestaError noEncontrado(RecursoNoEncontradoExcepcion e) {
        return new RespuestaError(e.getMessage());
    }

    @ExceptionHandler(DatosInvalidosExcepcion.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaError datosInvalidos(DatosInvalidosExcepcion e) {
        return new RespuestaError(e.getMessage());
    }

    @ExceptionHandler(ConflictoExcepcion.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public RespuestaError conflicto(ConflictoExcepcion e) {
        return new RespuestaError(e.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasExcepcion.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RespuestaError credencialesInvalidas(CredencialesInvalidasExcepcion e) {
        return new RespuestaError(e.getMessage());
    }

    // Fallo alguna validacion de un DTO (@NotBlank, @Email, ...): se juntan todos los mensajes
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaError validacionFallida(MethodArgumentNotValidException e) {
        String mensajes = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .sorted()
                .collect(Collectors.joining(". "));
        return new RespuestaError(mensajes);
    }

    // Segunda barrera: la base rechazo un dato repetido (ej. dos pedidos simultaneos con el mismo DNI)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public RespuestaError datoRepetido(DataIntegrityViolationException e) {
        return new RespuestaError("Ya existe un registro con esos datos");
    }

    // JSON mal escrito o con un tipo equivocado (ej. texto donde va un numero)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaError jsonInvalido(HttpMessageNotReadableException e) {
        return new RespuestaError("El cuerpo del pedido no es un JSON valido o tiene datos con formato incorrecto");
    }

    // Ej. /api/reservas/abc/devolver
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaError parametroInvalido(MethodArgumentTypeMismatchException e) {
        return new RespuestaError("El valor '" + e.getValue() + "' no es valido para " + e.getName());
    }

    // Cualquier otra cosa es un error nuestro: se loguea completo y al cliente
    // se le da un mensaje generico (sin detalles internos).
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RespuestaError errorInesperado(Exception e) {
        log.error("Error inesperado", e);
        return new RespuestaError("Ocurrio un error inesperado en el servidor");
    }
}
