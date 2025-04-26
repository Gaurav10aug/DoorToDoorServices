package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendorController {
    
    @GetMapping("/VendorSignupp")
    public String go5(){
        return "VendorSignup";
    }
    
    @GetMapping("/AdminManageVendors")
    public String go6(){
        return "AdminManageServiceProviders";
    }
    
    @GetMapping("/VendorLogin")
    public String go(){
        return "VendorLogin";
    }
    
    @GetMapping("/VendorHome")
    public String go7(){
        return "VendorHome";
    }
    
    @GetMapping("/VendorManagePhotos")
    public String go8(){
        return "VendorManagePhotos";
    }
    
    @GetMapping("/EditVendorDetails")
    public String go9(){
        return "EditDetails";
    }
    
}
