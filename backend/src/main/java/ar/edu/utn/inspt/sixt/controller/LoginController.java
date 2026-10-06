package ar.edu.utn.inspt.sixt.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    public record LoginRequest(String username, String password) {}

    // TODO: validar contra la tabla person cuando tenga password
    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        if ("admin".equals(request.username()) && "1234".equals(request.password())) {
            return ResponseEntity.ok(Map.of("username", request.username()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Usuario o contraseña incorrectos"));
    }
}
