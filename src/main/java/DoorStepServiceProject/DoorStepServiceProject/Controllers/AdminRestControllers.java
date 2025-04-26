package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import DoorStepServiceProject.DoorStepServiceProject.vmm.DBLoader;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AdminRestControllers {


    @PostMapping("/Adminlogin")
    public String login(@RequestParam String email, @RequestParam String password) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from admin where Email_id='" + email + "' and Password='" + password + "'");
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

    @PostMapping("/AdminManageCities")
    public String go4(@RequestParam String cityname, @RequestParam String citydescription, @RequestParam MultipartFile cityphoto) {
        try {

            ResultSet rs = DBLoader.executeQuery("select * from cities where city_name= '" + cityname + "'");
            if (rs.next()) {
                return "exists";
            } else {

                String projectPath = System.getProperty("user.dir");
                String internal_path = "/src/main/resources/static";
                String folderName = "/myUploads";
                String orgName = "/" + cityphoto.getOriginalFilename();
                rs.moveToInsertRow();
                rs.updateString("city_name", cityname);
                rs.updateString("city_desc", citydescription);
                rs.updateString("city_photo", orgName);
                rs.insertRow();
                byte b1[];

                FileOutputStream fos = new FileOutputStream(projectPath + internal_path + folderName + orgName);
                b1 = cityphoto.getBytes();
                fos.write(b1);
                return "City Added successfully";

            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    @GetMapping("/AdminGetAllCities")
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
                ans += "<button onclick='deleteCity(" + city_id + ")' style='background:red; color:white; border:none; padding:10px 20px; margin-top:10px; border-radius:5px;'>Delete</button>";
                ans += "</div>";
            }
            ans += "</div>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

    @GetMapping("/AdminDeleteCity")
    public String deleteCity(@RequestParam int city_id) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from cities where city_id=" + city_id);
            if (rs.next()) {
                rs.deleteRow();
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    @PostMapping("/ManageServices")
    public String go7(@RequestParam String servicename, @RequestParam String servicedescription, @RequestParam MultipartFile servicephoto) {
        try {

            ResultSet rs = DBLoader.executeQuery("select * from services where service_name= '" + servicename + "'");
            if (rs.next()) {
                return "exists";
            } else {

                String projectPath = System.getProperty("user.dir");
                String internal_path = "/src/main/resources/static";
                String folderName = "/myUploads";
                String orgName = "/" + servicephoto.getOriginalFilename();
                rs.moveToInsertRow();
                rs.updateString("service_name", servicename);
                rs.updateString("service_desc", servicedescription);
                rs.updateString("service_photo", orgName);
                rs.insertRow();
                byte b1[];

                FileOutputStream fos = new FileOutputStream(projectPath + internal_path + folderName + orgName);
                b1 = servicephoto.getBytes();
                fos.write(b1);
                return "Service Added successfully";

            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    @GetMapping("/AdminGetAllServices")
    public String getAllservices() {
        String ans = "";
        try {
            ResultSet rs = DBLoader.executeQuery("select * from services");
            ans += "<div style='display:flex; flex-wrap:wrap; gap:20px;'>";
            while (rs.next()) {
                String service_id = rs.getString("service_id");
                String service_name = rs.getString("service_name");
                String service_photo = rs.getString("service_photo");

                ans += "<div style='flex: 0 0 calc(33.33% - 20px); border:1px solid #ccc; border-radius:10px; padding:10px; box-shadow:2px 2px 10px rgba(0,0,0,0.2); text-align:center;'>";
                ans += "<img src='myUploads" + service_photo + "' style='width:100%; height:200px; object-fit:cover; border-radius:10px;'><br>";
                ans += "<h3 style='margin:10px 0;'>" + service_name + "</h3>";
                ans += "<button onclick='deleteservice(" + service_id + ")' style='background:red; color:white; border:none; padding:10px 20px; margin-top:10px; border-radius:5px;'>Delete</button>";
                ans += "</div>";
            }
            ans += "</div>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

    @GetMapping("/deleteservice")
    public String deleteservice(@RequestParam int service_id) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from services where service_id=" + service_id);
            if (rs.next()) {
                rs.deleteRow();
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }
}
