package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    
    @GetMapping("/ShowServicesToUsers")
    public String showServicesPage() {
        return "ShowServicesToUsers"; // Thymeleaf will look in templates/ShowServicesToUsers.html
    }
    
     @GetMapping("/ShowServiceProvidersToUsers")
    public String showServiceProviders() {
        return "ShowServicesDetails"; // Thymeleaf will look in templates/ShowServicesToUsers.html
    }
    
    @GetMapping("/SingleVendorDetails")
    public String showVendorDetailsPage() {
        return "SingleVendorDetails"; // Thymeleaf will look in templates/ShowServicesToUsers.html
    }
    
    @GetMapping("/slots")
    public String slots()
    {
        return"UserViewSlot";
    }
    
    @GetMapping("/payment")
    public String payment()
    {
        return "payment";
    }
    
    @GetMapping("/rating")
    public String rating()
    {
        return "rating";
    }
    
    @GetMapping("/Forget")
    public String forget()
    {
        return "Forget";
    }
    
}
