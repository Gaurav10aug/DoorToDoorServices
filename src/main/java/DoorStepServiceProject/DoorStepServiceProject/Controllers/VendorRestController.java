package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import DoorStepServiceProject.DoorStepServiceProject.vmm.DBLoader;
import DoorStepServiceProject.DoorStepServiceProject.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.io.*;
//import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VendorRestController {

    @GetMapping("/getCities")
    public String getCitiess() {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from cities");

        return ans;
    }

    @GetMapping("/getServices")
    public String getServicess() {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from services");

        return ans;
    }

    @PostMapping("/VendorSignUp")
    public String registerVendor(
            @RequestParam String vename,
            @RequestParam String veemail,
            @RequestParam String vecity,
            @RequestParam String vepass,
            @RequestParam String veservices,
            @RequestParam String vesuser,
            @RequestParam String vestart,
            @RequestParam String veend,
            @RequestParam String veprice,
            @RequestParam String vecontact,
            @RequestParam MultipartFile vephoto,
            @RequestParam String vedesc
    ) {
        try {
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM vendor WHERE v_email='" + veemail + "'");

            if (rs.next()) {
                return "Email already registered.";
            }

            String projectPath = System.getProperty("user.dir");
            String internalPath = "/src/main/resources/static/myUploads";
            String folderName = "/VendormyUploads";
            String orgName = "/" + vephoto.getOriginalFilename();

// Construct folder path (without filename)
            String folderPath = projectPath + internalPath + folderName;

// Make sure the folder exists
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

// Now construct full path including filename
            String fullPath = folderPath + orgName;

            rs.moveToInsertRow();
            rs.updateString("v_name", vename);
            rs.updateString("v_email", veemail);
            rs.updateInt("v_city", Integer.parseInt(vecity));
            rs.updateString("v_pass", vepass);
            rs.updateInt("v_service", Integer.parseInt(veservices));
            rs.updateString("v_subservice", vesuser);
            rs.updateString("v_start-time", vestart);
            rs.updateString("v_end-time", veend);
            rs.updateString("v_price", veprice);
            rs.updateString("v_contact", vecontact);
            rs.updateString("v_photo", orgName);
            rs.updateString("v_desc", vedesc);
            rs.updateString("v_status", "Blocked"); // Initial status
            rs.insertRow();

            // Save photo to server
            byte[] photoBytes = vephoto.getBytes();
            try (FileOutputStream fos = new FileOutputStream(fullPath)) {
                fos.write(photoBytes);
            }

            return "Vendor Registered Successfully!";

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: " + ex.getMessage();
        }
    }

    @PostMapping("/AdminShowVendors")
    public String showVendors() {
        try {
            String sql = "SELECT * FROM vendor";
            RDBMS_TO_JSON rdbms_to_json = new RDBMS_TO_JSON();
            String jsonResult = rdbms_to_json.generateJSON(sql);
            return jsonResult;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "exception";
        }
    }

    @PostMapping("/approveVendors")
    public String approveVendors(@RequestParam String vid) {
        try {
            String sql = "select * from vendor where v_id = " + vid + " ";
            ResultSet rs = DBLoader.executeQuery(sql);
            if (rs.next()) {
                rs.updateString("v_status", "Approved");
                rs.updateRow();

                return "success";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return "exception";
        }
        return null;
    }

    @PostMapping("/blockVendors")
    public String blockVendors(@RequestParam String vid) {
        try {
            String sql = "select * from vendor where v_id = " + vid + " ";
            ResultSet rs = DBLoader.executeQuery(sql);
            if (rs.next()) {
                rs.updateString("v_status", "Blocked");
                rs.updateRow();

                return "success";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "exception";
        }
        return null;
    }

    @PostMapping("/Vendorlogin")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        try {
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM vendor WHERE v_email='" + email + "' AND v_pass='" + password + "'");
            if (rs.next()) {
                int vendorId = rs.getInt("v_id");
                session.setAttribute("vendorId", vendorId);
                // FIXED: consistent key
                return "success";
            } else {
                return "failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/VendorManagePhotos")
    public String photoDetails(@RequestParam MultipartFile photo, @RequestParam String pdesc, HttpSession session) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from managephotos");
            String projectPath = System.getProperty("user.dir");
            String internal_path = "/src/main/resources/static/myUploads";
            String folderName = "/VendormyUploads";
            String orgName = photo.getOriginalFilename(); // FIXED: no leading slash
            Integer v_id = (Integer) session.getAttribute("vendorId"); // FIXED: consistent key
            if (v_id == null) {
                return "error: Please login again. Session expired.";
            }

            rs.moveToInsertRow();
            rs.updateString("photo", orgName);
            rs.updateString("p_desc", pdesc);
            rs.updateInt("v_id", v_id);
            rs.insertRow();

            byte b1[] = photo.getBytes();
            FileOutputStream fos = new FileOutputStream(projectPath + internal_path + folderName + "/" + orgName);
            fos.write(b1);
            fos.close();

            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "exception";
        }
    }

    @PostMapping("/VendorShowPhotos")
    public String showPhotos(HttpSession session) {
        try {
            Integer vendorId = (Integer) session.getAttribute("vendorId");
            if (vendorId == null) {
                return "[]"; // Return empty array if session is invalid
            }

            RDBMS_TO_JSON rdbms_to_json = new RDBMS_TO_JSON();
            String json = rdbms_to_json.generateJSON("SELECT * FROM managephotos WHERE v_id=" + vendorId);

            return json; // Return raw JSON array, not wrapped in an object
        } catch (Exception e) {
            e.printStackTrace();
            return "[]"; // Return empty array on error
        }
    }

    @PostMapping("/DeleteVendorPhoto")
    public String deleteVendorPhoto(@RequestParam String photo, HttpSession session) {
        try {
            Integer v_id = (Integer) session.getAttribute("vendorId");
            if (v_id == null) {
                return "error: Please login again.";
            }
            int count = DBLoader.executeUpdate("DELETE FROM managephotos WHERE photo='" + photo + "' AND v_id=" + v_id);
            return (count > 0) ? "success" : "failed";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

//    @GetMapping("/GetVendorDetails")
//    public String getVendorDetails(HttpSession session) {
//        try {
//            Integer vendorId = (Integer) session.getAttribute("v_id");
//
//            if (vendorId == null) {
//                return "Session expired or not logged in";
//            }
//
//            String jsonData = new RDBMS_TO_JSON().generateJSON("SELECT * FROM vendor WHERE v_id=" + vendorId);
//
//            return jsonData;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error: " + e.getMessage();
//        }
//    }
    @PostMapping("/VendorUpdateDetails")
    public String updateVendor(HttpSession session,
            @RequestParam String vname,
            @RequestParam String vstart,
            @RequestParam String vend,
            @RequestParam String vprice,
            @RequestParam String vcontact,
            @RequestParam String vdesc) {
        try {
            // Get vendorId from session safely
            Object obj = session.getAttribute("vendorId");
            if (obj == null) {
                return "Session expired. Please login again.";
            }

            int vendorId = Integer.parseInt(obj.toString());

            // Fetch vendor record
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM vendor WHERE v_id=" + vendorId);
            if (rs.next()) {
                // Update vendor data
                rs.updateString("v_name", vname);
                rs.updateString("v_start-time", vstart);
                rs.updateString("v_end-time", vend);
                rs.updateString("v_price", vprice);
                rs.updateString("v_contact", vcontact);
                rs.updateString("v_desc", vdesc);
                rs.updateRow();

                return "Details updated successfully!";
            } else {
                return "Vendor not found!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: " + ex.getMessage();
        }
    }

}
