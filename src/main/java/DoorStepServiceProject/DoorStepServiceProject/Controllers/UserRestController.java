package DoorStepServiceProject.DoorStepServiceProject.Controllers;

import DoorStepServiceProject.DoorStepServiceProject.vmm.DBLoader;
import java.io.File;
import java.io.FileOutputStream;
import DoorStepServiceProject.DoorStepServiceProject.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from user where u_email='" + email + "' and u_pass='" + password + "'");
            if (rs.next()) {
                session.setAttribute("uemail", email);
                int id = rs.getInt("u_id");
                session.setAttribute("uid", id);
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
    public String showServicesInUserHome(@RequestParam("city_id") String city_id) {
        int cityid = Integer.parseInt(city_id); // Use the correct variable name
        String ans = new RDBMS_TO_JSON().generateJSON(
                "SELECT DISTINCT s.service_id, s.service_name, s.service_desc, s.service_photo, "
                + "MIN(v.v_price) AS min_price " + "FROM services s JOIN vendor v ON v.v_service = s.service_id "
                + "WHERE v.v_city = " + cityid + " AND v.v_status = 'Approved' "
                + "GROUP BY s.service_id, s.service_name, s.service_desc, s.service_photo"
        );
        return ans;
    }

    @GetMapping("/getVendorsForServiceInCity")
    public String getVendorsForServiceInCity(@RequestParam String service_id, @RequestParam String city_id) {
        int sid = Integer.parseInt(service_id);
        int cid = Integer.parseInt(city_id);

        String ans = new RDBMS_TO_JSON().generateJSON(
                "SELECT *"
                + "FROM vendor v "
                + "WHERE v.v_service = " + sid + " AND v.v_city = " + cid + " AND v.v_status = 'Approved'"
        );
        return ans;
    }

    @GetMapping("/GetSingleVendorDetails")
    public String getVendorDetails(@RequestParam int v_id) {
        try {
            String ans = new RDBMS_TO_JSON().generateJSON("select * from vendor where v_id =" + v_id + " ");

            return ans;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @GetMapping("/view_slots")
    String view_slots(@RequestParam String email, @RequestParam String date) {

        System.out.println(date);
        System.out.println(email);
        try {
            ResultSet rs = DBLoader.executeQuery("select * from vendor where v_email='" + email + "'");

            String start;
            String end;
            String slot;
            if (rs.next()) {
                start = rs.getString("v_start_time");
                end = rs.getString("v_end_time");
                slot = rs.getString("v_price");

            } else {
                String err = "failed";
                return err;
            }
            int Start = Integer.parseInt(start);
            int End = Integer.parseInt(end);
            int Slot = Integer.parseInt(slot);
            JSONObject ans = new JSONObject();

            //Define JSONArray
            JSONArray arr = new JSONArray();
            for (int i = Start; i <= End; i++) {
                JSONObject row = new JSONObject();
                row.put("start_slot", Start);
                row.put("end_slot", ++Start);
                row.put("slot_amount", slot);

                ResultSet rs2 = DBLoader.executeQuery("select * from booking_detail where start_slot ='" + i + "' and bid in (select booking_id from booking where date=\'" + date + "\' and vendor_email =\'" + email + "\' ) ");
                if (rs2.next()) {
                    row.put("status", "Booked");
                } else {
                    row.put("status", "Available");
                }

                arr.add(row);
            }
            ans.put("ans", arr);
            System.out.println(ans.toString());
            return (ans.toJSONString());

        } catch (Exception e) {
            return e.toString();
        }

    }

    @GetMapping("/pay")
    public String payment(@RequestParam String date,
            @RequestParam String vendor_email,
            @RequestParam String amount,
            @RequestParam String slots,
            HttpSession session,
            @RequestParam String type,
            @RequestParam String status) {
        String ans = "";
        String user_email = (String) session.getAttribute("uemail");

        try {
            // 1. Insert into booking table
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM booking");
            rs.moveToInsertRow();
            rs.updateString("date", date);
            rs.updateString("vendor_email", vendor_email);
            rs.updateString("user_email", user_email);
            rs.updateString("total_price", amount);
            rs.updateString("payment_type", type);
            rs.updateString("status", status);
            rs.insertRow();

            // 2. Get inserted booking_id
            int booking_id = 0;
            ResultSet rs2 = DBLoader.executeQuery("SELECT MAX(booking_id) AS maxid FROM booking");
            if (rs2.next()) {
                booking_id = rs2.getInt("maxid");
            }

            // 3. Insert slots into booking_detail table
            StringTokenizer st = new StringTokenizer(slots, ",");
            while (st.hasMoreTokens()) {
                int start_slot = Integer.parseInt(st.nextToken());
                int end_slot = start_slot + 1;

                ResultSet rs3 = DBLoader.executeQuery("SELECT * FROM booking_detail");
                rs3.moveToInsertRow();
                rs3.updateInt("bid", booking_id); // fk to booking_id
                rs3.updateString("start_slot", String.valueOf(start_slot));
                rs3.updateString("end_slot", String.valueOf(end_slot));
                rs3.insertRow();
            }

            ans = "success";
            return ans;
        } catch (Exception ex) {
            return ex.toString();
        }
    }

}
