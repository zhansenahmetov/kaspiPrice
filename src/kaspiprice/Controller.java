package kaspiprice;

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


public class Controller {
    public KaspiPrice mainApp;
    
    private String FileSave;
    
    private ObservableList<Product> productsData = FXCollections.observableArrayList();
    private FilteredList<Product> flProduct; 
    private SortedList<Product> slProduct;
    @FXML
    private Button saveButton;
    @FXML
    private TableView<Product> tableProducts;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Integer> costColumn;
    @FXML
    private TableColumn<Product, String> actionColumn;
    @FXML
    private TableColumn<Product, Hyperlink> kaspiLink;
    @FXML
    private TextField costs;
    @FXML
    private CheckBox setAllcheck;
    @FXML
    private TextField pathField;
    @FXML
    private AnchorPane anchorid;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private TextField searchField;

    
    
    String format = "%02X"; // To get 2 char output.
    public String getPhysicalAddress() throws Exception{
        try {
            // DHCP Enabled - InterfaceMetric
            String macs="";

            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            boolean macFound=false;
            
            while( nis.hasMoreElements()&&(!macFound) ) {
                NetworkInterface ni = nis.nextElement();
                byte mac [] = ni.getHardwareAddress(); // Physical Address (MAC - Medium Access Control)
                if( mac != null ) {
                    final StringBuilder macAddress = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        macAddress.append(String.format("%s"+format, (i == 0) ? "" : "", mac[i]) );
                        //macAddress.append(String.format(format+"%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                    }
                    //System.out.print(ni.getName()+" ");
                    //System.out.println(macAddress.toString());
                    if(ni.getName().equals("eth0")||ni.getName().equals("wlan0")||ni.getName().equals("wlan1")||ni.getName().equals("eth1")||ni.getName().equals("wlan2")||ni.getName().equals("eth2")){
                    macs= macAddress.toString();
                    macFound=true;
                    }
                    System.out.println(macs);
                }
            }
            //System.out.print(macs);
            return macs;
        } catch( Exception ex ) {
            System.err.println( "Exception:: " + ex.getMessage() );
            ex.printStackTrace();
        }
        return "";
    }
    
    


    // инициализируем форму данными
    @FXML
    public void initialize(ObservableList<Product> input) {
        flProduct= new FilteredList(input, p -> true);
        
        // устанавливаем тип и значение которое должно хранится в 
        idColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        costColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("cost"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<Product,String>("checkbox")); 
        kaspiLink.setCellValueFactory(new PropertyValueFactory<Product,Hyperlink>("link"));
        choiceBox.getItems().addAll("sku", "Наименование товара", "Цена товара");
        choiceBox.setValue("Наименование товара");
        
        
        searchField.setOnKeyReleased(keyEvent ->
        {
            switch (choiceBox.getValue())//Switch on choiceBox value
            {
                case "sku":
                    flProduct.setPredicate(p -> Integer.toString(p.getId()).toLowerCase().contains(searchField.getText().toLowerCase().trim()));//filter table by first name
                    break;
                case "Наименование товара":
                    flProduct.setPredicate(p -> p.getName().toLowerCase().contains(searchField.getText().toLowerCase().trim()));//filter table by first name
                    break;
                case "Цена товара":
                    flProduct.setPredicate(p -> Integer.toString(p.getCost()).toLowerCase().contains(searchField.getText().toLowerCase().trim()));//filter table by first name
                    break;
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {//reset table and textfield when new choice is selected
            if (newVal != null)
            {
                searchField.setText("");
                flProduct.setPredicate(null);//This is same as saying flPerson.setPredicate(p->true);
                setAllcheck.setSelected(false);
            }
        });
        
        slProduct=new SortedList<Product>(flProduct);
        slProduct.comparatorProperty().bind(tableProducts.comparatorProperty());
        // заполняем таблицу данными
        tableProducts.setItems(slProduct);
        tableProducts.refresh();
    }
    
    
    
        @FXML
    private void plusSelectedRows(ActionEvent event) {
        if(costs.getText().matches("[0-9]*")){
            setAllcheck.setSelected(false);
            int value=Integer.parseInt(costs.getText());
            int i=0;
            for(Product bean : flProduct)
            {
               if(bean.getCheckbox().isSelected())
               {   
                   flProduct.get(i).setCost(value);
                   flProduct.get(i).setCheckBox();
               }
               i++;

            }

            slProduct=new SortedList<Product>(flProduct);
            slProduct.comparatorProperty().bind(tableProducts.comparatorProperty());
            // заполняем таблицу данными
            tableProducts.setItems(slProduct);
            tableProducts.refresh();
            Notifications.create().title("Внимание!").text("Цена товара была увеличена на "+value).position(Pos.CENTER).showInformation();
        }else{
            Notifications.create().title("Внимание!").text("Введите цифры").position(Pos.CENTER).showInformation();

        }
    }
        @FXML
    private void minusSelectedRows(ActionEvent event) {
        if(costs.getText().matches("[0-9]*")){
            setAllcheck.setSelected(false);
            int value=Integer.parseInt(costs.getText());
            int i=0;
            for(Product bean : flProduct)
            {
               if(bean.getCheckbox().isSelected())
               {   
                   flProduct.get(i).setCost(value*(-1));
                   flProduct.get(i).setCheckBox();
               }
               i++;

            }

            slProduct=new SortedList<Product>(flProduct);
            slProduct.comparatorProperty().bind(tableProducts.comparatorProperty());
            // заполняем таблицу данными
            tableProducts.setItems(slProduct);
            tableProducts.refresh();
            Notifications.create().title("Внимание!").text("Цена товара была уменьшена на "+value).position(Pos.CENTER).showInformation();
        }else{
            Notifications.create().title("Внимание!").text("Введите цифры").position(Pos.CENTER).showInformation();

        }
    }
    
    
    
    @FXML
    void handleDragOver(DragEvent event) {
        if(event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
            
    }
    @FXML
    void handleDrop(DragEvent event) throws FileNotFoundException{
        List<File> files=event.getDragboard().getFiles();
        //System.out.print("File name is!!!!:"+files.get(0));
        mainApp.WriteFile(files.get(0).getAbsolutePath());
        FileSave=files.get(0).getAbsolutePath();
    }
    
    public void setMainApp(KaspiPrice userStart) {
        this.mainApp = userStart;
    }
    
    
    @FXML
    void saveFile(ActionEvent event) throws Exception {
        
        saveButton.setDisable(true);
        Task<Void> databaseTask = new Task<Void>() {
            @Override
            public Void call() throws Exception{
                saveFileX();
                return null;
            }
        };
        databaseTask.setOnSucceeded(e ->{
            saveButton.setDisable(false);
            String pathSave="";
           if(pathField!=null){
               pathSave=pathField.getText()+"\\";
           }
           if(pathSave.equals("\\")){
               pathSave="";
           }
           Notifications.create().title("Внимание!").text("Ваш файл сохранен "+((pathSave.equals(""))?"в папке app внутри kaspiPrice":("в "+pathSave))).position(Pos.CENTER).showInformation();

        });
        databaseTask.setOnFailed(e->{
            saveButton.setDisable(false);
            Notifications.create().title("Внимание!").text("ФАЙЛ НЕ ВЫБРАН ИЛИ НЕТУ ИНТЕРНЕТА.").position(Pos.CENTER).showInformation();
        }
        );
        new Thread(databaseTask).start();
    }
    
    @FXML
    void selectAll(ActionEvent event) {
        int i=0;
        if(setAllcheck.isSelected()){
            for(Product bean : flProduct)
            {
                flProduct.get(i).setCheckBoxTrue();
                i++;

            }
            tableProducts.refresh();
        }else{
            for(Product bean : flProduct)
            {
                flProduct.get(i).setCheckBoxFalse();
                i++;

            }
            tableProducts.refresh();
        }
    }

    @FXML
    void choosePath(ActionEvent event) {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        Stage stage = (Stage) anchorid.getScene().getWindow();
        File file = dirchooser.showDialog(stage);
        
        if(file!=null){
            //System.out.print("Path: "+file.getAbsolutePath());
            pathField.setText(file.getAbsolutePath());
        }else{
            //System.out.print("choose path");
        }
        
    }
    
    
    public void saveFileX() throws Exception{
            if(FileSave!=null){
                try{
                    File file = new File(FileSave);  
                    DocumentBuilderFactory dbf1 = DocumentBuilderFactory.newInstance();  
                    //an instance of builder to parse the specified xml file  
                    DocumentBuilder db1 = dbf1.newDocumentBuilder();  
                    Document doc1 = db1.parse(file);  
                    doc1.getDocumentElement().normalize();  
                    //System.out.println("Root element: " + doc1.getDocumentElement().getNodeName());  
                    NodeList nodeList = doc1.getElementsByTagName("offer"); 
                    //System.out.print("NodeList length"+nodeList.getLength());  
                    //System.out.print("COST"+productsData.get(0).getCost()+"LOL     ");
                    // nodeList is not iterable, so we are using for loop  
                    flProduct.setPredicate(null);

                    Comparator<Product> comparator = Comparator.comparingInt(Product::getNumber); 
                    //flProduct.sort(comparator);
                    //FXCollections.sort(productsData, comparator);
                    System.out.print(productsData.size());

                    for (int itr = 0; itr < nodeList.getLength(); itr++){  
                        Node node = nodeList.item(itr);  
                        //System.out.println("\nNode Name :" + node.getNodeName());  
                        if (node.getNodeType() == Node.ELEMENT_NODE){  
                            Element eElement = (Element) node;  
                            //System.out.print(eElement.getElementsByTagName("price").item(0).getTextContent());
                            if(!eElement.getElementsByTagName("price").item(0).getTextContent().equals(String.valueOf(flProduct.get(itr).getCost()))){
                                eElement.getElementsByTagName("price").item(0).setTextContent(String.valueOf(flProduct.get(itr).getCost()));

                            }

                        }  
                    }

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc1);
                    //System.out.println("-----------Modified File-----------");


                    doc1.getDocumentElement().normalize();

                    String pathSave="";
                    if(pathField!=null){
                        pathSave=pathField.getText()+"\\";
                    }
                    if(pathSave.equals("\\")){
                        pathSave="";
                    }
                       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy");
                        LocalDateTime now = LocalDateTime.now();
                    StreamResult result = new StreamResult(new File(pathSave+"kaspi_"+dtf.format(now)+".xml"));

                    //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.transform(source, result);
                    Notifications.create().title("Внимание!").text("Ваш файл сохранен "+((pathSave.equals(""))?"в папке app внутри kaspiPrice":("в "+pathSave))).position(Pos.CENTER).showInformation();


                    //System.out.println("XML file updated successfully");
                }catch(Exception e){

     
            }}else{
                throw new RuntimeException("foobar");
            }
    }


}