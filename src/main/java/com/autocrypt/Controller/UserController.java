package com.autocrypt.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/auth/user/save")
    public String userSave() {
        return "layout/user/user-save";
    }

    @GetMapping("/auth/user/login")
    public String userLogin() {
        return "layout/user/user-login";
    }

    @GetMapping("/user/update")
    public String userUpdate() {
        return "layout/user/user-update";
    }
}
