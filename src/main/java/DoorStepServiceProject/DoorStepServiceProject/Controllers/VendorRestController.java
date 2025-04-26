package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import DoorStepServiceProject.DoorStepServiceProject.vmm.DBLoader;
import DoorStepServiceProject.DoorStepServiceProject.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

//    @GetMapping("/AdminGetAllVendors")
//    public String getAllVendors() {
//        StringBuilder ans = new StringBuilder();
//
//        try {
//            ResultSet rs = DBLoader.executeQuery("SELECT * FROM vendor");
//            ans.append("<div style='display:flex; flex-wrap:wrap; gap:20px;'>");
//
//            while (rs.next()) {
//                String v_id = rs.getString("v_id");
//                String v_name = rs.getString("v_name");
//                String v_photo = rs.getString("v_photo");
//                String v_city = rs.getString("v_city");
//                String v_service = rs.getString("v_service");
//                String v_subservice = rs.getString("v_subservice");
//                String v_price = rs.getString("v_price");
//                String v_status = rs.getString("v_status");
//
//                ans.append("<div style='flex: 0 0 calc(33.33% - 20px); border:1px solid #ccc; border-radius:10px; padding:10px; box-shadow:2px 2px 10px rgba(0,0,0,0.2); text-align:center;'>");
//                ans.append("<img src='myUploads/VendormyUploads/").append(v_photo).append("' style='width:100%; height:200px; object-fit:cover; border-radius:10px;'><br>");
//                ans.append("<h3>").append(v_name).append("</h3>");
//                ans.append("<p>City: ").append(v_city).append("</p>");
//                ans.append("<p>Service: ").append(v_service).append("</p>");
//                ans.append("<p>Subservice: ").append(v_subservice).append("</p>");
//                ans.append("<p>Price: ₹").append(v_price).append("</p>");
//
//                if ("Block".equalsIgnoreCase(v_status)) {
//                    ans.append("<button onclick='acceptVendor(").append(v_id).append(")' style='background:green; color:white; border:none; padding:10px 20px; border-radius:5px;'>Accept</button>");
//                } else {
//                    ans.append("<button onclick='deleteVendor(").append(v_id).append(")' style='background:red; color:white; border:none; padding:10px 20px; border-radius:5px;'>Block</button>");
//                }
//
//                ans.append("</div>");
//            }
//
//            ans.append("</div>");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ans.toString();
//    }
    @GetMapping("/AdminGetAllVendors")
    public String getAllVendors() {
        StringBuilder ans = new StringBuilder();

        try {
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM vendor");
            ans.append("<table border='1' style='width:100%; border-collapse:collapse; text-align:center;'>");
            ans.append("<thead style='background:#f2f2f2;'>");
            ans.append("<tr>");
            ans.append("<th>Photo</th>");
            ans.append("<th>Name</th>");
            ans.append("<th>City</th>");
            ans.append("<th>Service</th>");
            ans.append("<th>Subservice</th>");
            ans.append("<th>Price</th>");
            ans.append("<th>Status</th>");
            ans.append("<th>Action</th>");
            ans.append("</tr>");
            ans.append("</thead>");
            ans.append("<tbody>");

            while (rs.next()) {
                String v_id = rs.getString("v_id");
                String v_name = rs.getString("v_name");
                String v_photo = rs.getString("v_photo");
                String v_city = rs.getString("v_city");
                String v_service = rs.getString("v_service");
                String v_subservice = rs.getString("v_subservice");
                String v_price = rs.getString("v_price");
                String v_status = rs.getString("v_status");

                ans.append("<tr>");
                ans.append("<td><img src='myUploads/VendormyUploads/").append(v_photo).append("' style='width:100px; height:100px; object-fit:cover; border-radius:10px;'></td>");
                ans.append("<td>").append(v_name).append("</td>");
                ans.append("<td>").append(v_city).append("</td>");
                ans.append("<td>").append(v_service).append("</td>");
                ans.append("<td>").append(v_subservice).append("</td>");
                ans.append("<td>₹").append(v_price).append("</td>");
                ans.append("<td>").append(v_status).append("</td>");
                ans.append("<td>");
                ans.append("<button onclick=\"toggleVendor('").append(v_id).append("', '").append(v_status).append("')\" style='padding:8px 12px; background:").append(v_status.equalsIgnoreCase("Block") ? "green" : "red").append("; color:white; border:none; border-radius:5px;'>");
                ans.append(v_status.equalsIgnoreCase("Block") ? "Accept" : "Block");
                ans.append("</button>");
                ans.append("</td>");
                ans.append("</tr>");
            }

            ans.append("</tbody>");
            ans.append("</table>");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ans.toString();
    }

    @PostMapping("/ToggleVendorStatus")
    public String toggleVendorStatus(@RequestBody Map<String, String> data) {
        String v_id = data.get("v_id");

        try {
            ResultSet rs = DBLoader.executeQuery("SELECT v_status FROM vendor WHERE v_id = '" + v_id + "'");
            if (rs.next()) {
                String currentStatus = rs.getString("v_status");

                if (currentStatus.equalsIgnoreCase("Block")) {
                    // If currently Blocked → Accept the vendor (update status)
                    DBLoader.executeUpdate("UPDATE vendor SET v_status = 'Accepted' WHERE v_id = '" + v_id + "'");
                    return "Vendor Accepted";
                } else {
                    // If currently Accepted → Delete the vendor
                    DBLoader.executeUpdate("DELETE FROM vendor WHERE v_id = '" + v_id + "'");
                    return "Vendor Deleted";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";
    }

    @GetMapping("/AcceptVendor")
    public String acceptVendor(@RequestParam String v_id) {
        try {
            DBLoader.executeUpdate("UPDATE vendor SET v_status = 'Accepted' WHERE v_id = '" + v_id + "'");
            return "Vendor Accepted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @GetMapping("/DeleteVendor")
    public String deleteVendor(@RequestParam String v_id) {
        try {
            DBLoader.executeUpdate("DELETE FROM vendor WHERE v_id = '" + v_id + "'");
            return "Vendor Deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @PostMapping("/Vendorlogin")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        try {
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM vendor WHERE v_email='" + email + "' AND v_pass='" + password + "'");
            if (rs.next()) {
                int vendorId = rs.getInt("v_id");
                session.setAttribute("vendorId", vendorId);
                return "success";

            } else {
                return "failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/uploadPhoto")
    public String uploadPhoto(@RequestParam("photo") MultipartFile file,
            @RequestParam("description") String desc,
            HttpSession session) {
        try {
            Integer v_id = (Integer) session.getAttribute("vendorId");

            if (v_id == null) {
                return "You must be logged in.";
            }

            // Correct path and create folder if it doesn't exist
            String folderPath = System.getProperty("user.dir") + "/src/main/resources/static/myUploads/";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Unique file name and save location
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(folderPath + fileName);
            file.transferTo(dest); // Save file

            // Save filename and other details to DB
            Connection conn = DBLoader.getConnection(); // You'll need to add this method if missing
            String query = "INSERT INTO managephotos (photo, p_desc, v_id) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, fileName);
            ps.setString(2, desc);
            ps.setInt(3, v_id);

            int result = ps.executeUpdate();
            conn.close();

            return (result == 1) ? "Photo uploaded successfully!" : "Upload failed.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
    }

    @GetMapping("/GetVendorDetails")
    public String getVendorDetails(HttpSession session) {
        try {
            Integer vendorId = (Integer) session.getAttribute("v_id");

            if (vendorId == null) {
                return "Session expired or not logged in";
            }

            String jsonData = new RDBMS_TO_JSON().generateJSON("SELECT * FROM vendor WHERE v_id=" + vendorId);

            return jsonData;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

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
