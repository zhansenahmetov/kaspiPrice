/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kaspiprice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.util.ArrayList;
import kaspiprice.product.Product;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.NetworkInterface;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import kaspiprice.product.Product;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;



/**
 *
 * @author zhans
 */
public class KaspiPrice extends Application {
    FXMLLoader loader = new FXMLLoader(
      getClass().getResource(
        "main.fxml"
      )
    );
    Controller controller;


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {


    stage.setTitle("Kaspi Price");
    stage.setScene(
      new Scene(loader.load())
    );
    controller = loader.getController();
    controller.setMainApp(this);
    stage.show();
    if(checkLicense()){
        System.out.print("good");
    }else{
        System.out.print("BAD");
        stage.close();
    }
    //ReadXML2();




    }
    public ArrayList<Product> ReadXML2(String FileName){
        ArrayList<Product> products = new ArrayList<>();
        try{
            File file1 = new File(FileName);
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf1 = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db1 = dbf1.newDocumentBuilder();
            Document doc1 = db1.parse(file1);
            doc1.getDocumentElement().normalize();
            //System.out.println("Root element: " + doc1.getDocumentElement().getNodeName());
            NodeList nodeList1 = doc1.getElementsByTagName("offer");
            //System.out.print("NodeList length "+nodeList1.getLength());


            for (int itr = 0; itr < nodeList1.getLength(); itr++){
                Node node = nodeList1.item(itr);
                //System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) node;
                    products.add(new Product(eElement.getElementsByTagName("model").item(0).getTextContent(),Integer.parseInt(eElement.getAttribute("sku")),  Integer.parseInt(eElement.getElementsByTagName("price").item(0).getTextContent().trim()),itr,getHostServices()));
                    //System.out.println(Integer.parseInt(eElement.getAttribute("sku")));
                }
                //System.out.println(itr);

            }



        }catch(Exception e){

        }
        return products;
    }


    public  void WriteFile(String FileName){
            ArrayList<Product> products = new ArrayList<>();
            products=ReadXML2(FileName);

            ObservableList<Product> masterData2 = FXCollections.observableArrayList(products);
            controller.initialize(masterData2);
    }

    public boolean checkLicense() throws Exception{
        Gson gson = new Gson();
        boolean checkMac=false;
        boolean checkInternet=false;

        //get computer mac adresses
        String str = controller.getPhysicalAddress();
        //System.out.print(str);
        Map<String, String> elements = new HashMap<String, String>();
        elements.put("mac", str);
        elements.put("license", "");

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
            //System.out.print("not lol "+responseCode+" not lol");
            if(responseCode==201){
                checkMac=true;
            }

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


        }catch(Exception e){
            Notifications.create().title("Внимание!").text("НЕТУ ИНТЕРНЕТ СОЕДИНЕНИЯ").position(Pos.CENTER).showInformation();
            checkInternet=true;
        }

        if(checkMac){}
        else{
            if(!checkInternet){
            Notifications.create().title("Внимание!").text("У вас нету прав на использование").position(Pos.CENTER).showInformation();

             try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("NewWindow.fxml"));
                /*
                 * if "fx:controller" is not set in fxml
                 * fxmlLoader.setController(NewWindowController);
                 */
                Scene scene = new Scene(fxmlLoader.load(), 381, 171);
                Stage stage = new Stage();
                stage.setTitle("Введите лицензионный ключ");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.out.print("Window is not opened");
            }
            }
        }
        return checkMac;
    }


}
