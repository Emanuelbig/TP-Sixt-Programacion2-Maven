package ar.edu.utn.inspt.sixt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {
        if ("admin".equals(username) && "1234".equals(password)) {
            model.addAttribute("username", username);
            return "home";
        }

        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }
}
