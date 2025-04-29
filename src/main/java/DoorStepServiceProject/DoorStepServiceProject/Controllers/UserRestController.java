package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import DoorStepServiceProject.DoorStepServiceProject.vmm.DBLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    @GetMapping("/UserGetAllCities")
    public String getAllCities() {
        String ans = "";
        try {
            ResultSet rs = DBLoader.executeQuery("select * from cities");
            ans += "<div style='display:flex; flex-wrap:wrap; gap:20px;'>";
            while (rs.next()) {
                String city_id = rs.getString("city_id");
                String city_name = rs.getString("city_name");
                String city_photo = rs.getString("city_photo");

                ans += "<div style='flex: 0 0 calc(33.33% - 20px); border:1px solid #ccc; border-radius:10px; padding:10px; box-shadow:2px 2px 10px rgba(0,0,0,0.2); text-align:center;'>";
                ans += "<img src='myUploads" + city_photo + "' style='width:100%; height:200px; object-fit:cover; border-radius:10px;'><br>";
                ans += "<h3 style='margin:10px 0;'>" + city_name + "</h3>";
//                ans += "<button onclick='deleteCity(" + city_id + ")' style='background:red; color:white; border:none; padding:10px 20px; margin-top:10px; border-radius:5px;'>Delete</button>";
                ans += "</div>";
            }
            ans += "</div>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

}
