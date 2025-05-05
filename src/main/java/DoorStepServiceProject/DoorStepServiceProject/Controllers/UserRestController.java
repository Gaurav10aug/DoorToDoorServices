package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import DoorStepServiceProject.DoorStepServiceProject.vmm.DBLoader;
import java.io.File;
import java.io.FileOutputStream;
import DoorStepServiceProject.DoorStepServiceProject.vmm.RDBMS_TO_JSON;
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
        while (rs.next()) {
            String city_id = rs.getString("city_id");
            String city_name = rs.getString("city_name");
            String city_photo = rs.getString("city_photo");

            ans += "<a href='ShowServicesToUsers?id=" + city_id + "' class='city-card'>";
            ans += "<img src='myUploads" + city_photo + "' alt='" + city_name + "' />";
            ans += "<h3>" + city_name + "</h3>";
            ans += "</a>";
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return ans;
}


    
    @PostMapping("/showServicesToUsers")
    public String showServicesInUserHome(@RequestParam("city_id") String city_id)
    {
      int cityid = Integer.parseInt(city_id); // Use the correct variable name
      String ans = new RDBMS_TO_JSON().generateJSON(
      //  "SELECT DISTINCT services.* FROM services JOIN vendors ON v_service = services.service_id WHERE v_city = " + cityid + " AND v_status = 'Blocked'"
       "SELECT DISTINCT s.service_id, s.service_name, s.service_desc, s.service_photo, " +
        "MIN(v.v_price) AS min_price " + "FROM services s JOIN vendor v ON v.v_service = s.service_id " +
        "WHERE v.v_city = " + cityid + " AND v.v_status = 'Approved' " +
        "GROUP BY s.service_id, s.service_name, s.service_desc, s.service_photo"
     );
    return ans;
    }  


    @GetMapping("/getVendorsForServiceInCity")
    public String getVendorsForServiceInCity(@RequestParam String service_id, @RequestParam String city_id) {
    int sid = Integer.parseInt(service_id);
    int cid = Integer.parseInt(city_id);

    String ans = new RDBMS_TO_JSON().generateJSON(
        "SELECT *" +
        "FROM vendor v " +
        "WHERE v.v_service = " + sid + " AND v.v_city = " + cid + " AND v.v_status = 'Approved'"
    );
    return ans;
    }

 //    @GetMapping("/getVendorDetails")
 //   public ResponseEntity<Map<String, Object>> getVendorDetails(@RequestParam("v_id") int v_id) {
   //     Map<String, Object> response = new HashMap<>();
//
  //      try {
    //        String query = "SELECT * FROM vendors WHERE v_id = '" + v_id + "'";
      //      ResultSet rs = DBLoader.executeQuery(query);
//
  //          if (rs != null && rs.next()) {
    //            response.put("status", "success");
      //          response.put("v_id", rs.getInt("v_id"));
        //        response.put("vendor_name", rs.getString("vendor_name"));
          //      response.put("email", rs.getString("email"));
            //    response.put("phone", rs.getString("phone"));
              //  response.put("address", rs.getString("address"));
                //response.put("photo", rs.getString("photo"));
   //             response.put("category", rs.getString("category")); // only if this column exists
     //       } else {
       //         response.put("status", "error");
         //       response.put("message", "Vendor not found");
           // }
    //    } catch (Exception e) {
   //         e.printStackTrace();
     //       response.put("status", "error");
       //     response.put("message", "Exception occurred: " + e.getMessage());
        //}

        //return ResponseEntity.ok(response);
   // }
    
    @GetMapping("/GetSingleVendorDetails")
    public String getVendorDetails(@RequestParam int v_id) {
        try {
            String ans = new RDBMS_TO_JSON().generateJSON("select * from vendor where v_id ="+v_id+" ");
            
            return ans;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
