package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
    
    @GetMapping("/UserSignup")
    public String go9(){
        return "UserSignUp";
    }
    
     @GetMapping("/UserLogin")
    public String go(){
        return "UserLogin";
    }
    
    @GetMapping("/UserHome")
    public String go2(){
        return "UserHome";
    }
}
