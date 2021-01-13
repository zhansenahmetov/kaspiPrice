/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kaspiprice;

/**
 *
 * @author zhans
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.controlsfx.control.Notifications;
import kaspiprice.Controller;

public class ControllerLicense {
    Controller controller=new Controller();
    @FXML
    private TextField licenseKey;

    @FXML
    void handleClose(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    void handleSubmit(ActionEvent event) throws Exception {
        if(!licenseKey.getText().trim().equals("")){
        //System.out.print(licenseKey.getText().trim());
        
        String str=licenseKey.getText().trim();
        String str2=controller.getPhysicalAddress();
        
        Map<String, String> elements = new HashMap<String, String>();
        elements.put("mac", str2);
        elements.put("license", str);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInputString = objectMapper.writeValueAsString(elements);
        
        try{
            URL url = new URL ("https://m2a.kz/kaspi.php");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);      
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);			
            }
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(
             new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
            in.close();
            //print in String
            //System.out.println(response.toString());
            //System.out.print("not lol "+responseCode+" not lol");
            if(responseCode==201){
                Notifications.create().title("Внимание!").text("Поздравляю, приложение активировано").position(Pos.CENTER).showInformation();
                //((Node)(event.getSource())).getScene().getWindow().hide();
            }else{
            Notifications.create().title("Внимание!").text("Вы ввели неправильный ключ").position(Pos.CENTER).showInformation();
            }
        }catch(Exception e){
            Notifications.create().title("Внимание!").text("НЕТУ ИНТЕРНЕТ СОЕДИНЕНИЯ").position(Pos.CENTER).showInformation();
        }
        }else{
            //System.out.print("enter again");
            Notifications.create().title("Внимание!").text("Вы не заполнили строку").position(Pos.CENTER).showInformation();

        }
    }

}
