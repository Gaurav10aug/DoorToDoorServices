package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Gaurav
 */
@Controller
public class AdminControllers {
    @GetMapping("/AdminLogin")
    public String go(){
        return "AdminLogin";
    }
    
    @GetMapping("/AdminHome")
    public String go2(){
        return "AdminHome";
    }
    
    @GetMapping("/AdminManageCities")
    public String go3(){
        return "AdminManageCities";
    }
    
    @GetMapping("/ManageServices")
    public String go4(){
        return "ManageServices";
    }
}
