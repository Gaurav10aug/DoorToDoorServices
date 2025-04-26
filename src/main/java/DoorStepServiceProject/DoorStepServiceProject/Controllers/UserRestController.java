package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import DoorStepServiceProject.DoorStepServiceProject.vmm.DBLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserRestController {
    
    @PostMapping("/UserSignUp")
    public String registerUser(
            @RequestParam String usname,
            @RequestParam String usemail,
            @RequestParam String uspass,
            @RequestParam MultipartFile usphoto,
            @RequestParam String usaddress,
            @RequestParam String uscontact
    ) {
        try {
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM user WHERE u_email='" + usemail + "'");

            if (rs.next()) {
                return "Email already registered.";
            }

            String projectPath = System.getProperty("user.dir");
            String internalPath = "/src/main/resources/static/myUploads";
            String folderName = "/UsermyUploads";
            String orgName = "/" + usphoto.getOriginalFilename();

            String folderPath = projectPath + internalPath + folderName;

            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fullPath = folderPath + orgName;

            rs.moveToInsertRow();
            rs.updateString("u_name", usname);
            rs.updateString("u_email", usemail);
            rs.updateString("u_pass", uspass);
            rs.updateString("u_photo", orgName);
            rs.updateString("u_address", usaddress);
            rs.updateString("u_contact", uscontact);
            
            rs.insertRow();

            // Save photo to server
            byte[] photoBytes = usphoto.getBytes();
            try (FileOutputStream fos = new FileOutputStream(fullPath)) {
                fos.write(photoBytes);
            }

            return "User Registered Successfully!";

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: " + ex.getMessage();
        }
    }

    @PostMapping("/Userlogin")
    public String login(@RequestParam String email, @RequestParam String password) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from user where u_email='" + email + "' and u_pass='" + password + "'");
            if (rs.next()) {
                return "success";
            } else {
                return "failed";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "exception";
        }
    }
}
