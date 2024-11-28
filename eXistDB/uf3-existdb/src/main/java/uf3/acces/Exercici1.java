package uf3.acces;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

public class Exercici1 {
    static XPathQueryService service;
    public static void main(String[] args) throws Exception{
        connect();
        // addComanda();
        // deleteComanda();
        // changeComanda1();
        // // changeComanda2();
        // addAtribute();
        // addAtribute();
        calculateCost();
        // list();
        // newDocuemnt();
        // addElements();
        comprovacio();
    }

    // Creem un metode per a connectar la base de dades, amb un throws Exception per a la gestio d'errors.
    public static void connect() throws Exception  {
        //defineix el driver necessàri per connectar-se a la base de dades eXistDB i carrega la classe per tal d'instanciar-lo
        String driver = "org.exist.xmldb.DatabaseImpl";
        Class<?> cl = Class.forName(driver);

        // Creem una instància del driver DatabaseImpl per tal de poder utilitzar la connexió amb eXistDB
        Database database = (Database) cl.getDeclaredConstructor().newInstance();

        // Registre l'objecte Database amb DatabaseManager per tal que altres metodes accedeixin a eXistDB utilitzant la connexió creada
        DatabaseManager.registerDatabase(database);

        // Obtenim la col·lecció creada al nostre eXistDB del nostre servidor local localhost a través del port 8080, retornant un objecte del tipus Collection que representa la col·lecció XML, amb el nom d'usuari i la contrasenya
        Collection col = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db/UF3", "admin", "");

        //Inicialitzem res, un objecte de tipus XMLResource obtenint el recurs basedades.xml des de la col·lecció col. Aquest fitxer conté el contingut XML que posteriorment consultarem
        XMLResource res = null;
        res = (XMLResource) col.getResource("PurchasOrders.xml");

        //if que comprova si basedades.xml es troba a la base de dades de eXistDB o no
        if (res == null) {
            System.out.println("No s'ha conectat amb el ficher PurchasOrder.xml");
        } else {
            System.out.println("S'ha conectat amb el ficher PurchasOrder.xml");
        }
        
        //Creem un servei de consulta en format XPath (XPathQueryService)
        //per poder realitzar consultes a la base de dades, especificant la versió 3.1
        service = (XPathQueryService) col.getService("XPathQueryService", "3.1");

        //La consola retorna el format en pretty, és a dir, llegible pels humans
        service.setProperty("pretty", "true");

        //Defineix la codificació de caràcters com ISO-8859-1 per tal de poder tenir els caràcters que usem ç,ñ ...
        service.setProperty("encoding", "ISO-8859-1");
    }

    // Creem el metode per afegir amb XPath
    public static void addComanda() throws Exception {
        // Afegim la consulta amb Id 4
        String addXPath = "update insert" +
                            "   <PurchaseOrder id=\"4\">\n" +
                                "<OrderDate>2024-01-20</OrderDate>" +
                                "<ShipTo>" +
                                    "<Name>Michael Brown</Name>" +
                                    "<Address>Chicago</Address>" +
                                    "<City>Chicago</City>" +
                                    "<State>IL</State>" +
                                    "<Zip>60601</Zip>" +
                                    "<Country>USA</Country>" +
                                "</ShipTo>" +
                                "<Items>" +
                                    "<Item partNumber=\"001-MON\">" +
                                        "<ProductName>Monitor</ProductName>" +
                                        "<Quantity>1</Quantity>" +
                                        "<USPrice>200.00</USPrice>" +
                                    "</Item>" +
                                "</Items>" +
                            "</PurchaseOrder>" + 
                            "into /PurchaseOrders";
        // Amb el try, fem que si dona algun error, et digui l'error i segueixi corrent el codi en lloc de parar
        try {
            service.query(addXPath);
            System.out.println("S'ha afegit una nova comanda amb id 4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Creem el metode per borrar amb XPath
    public static void deleteComanda() throws Exception{
        String deleteComanda = "update delete /PurchaseOrders/PurchaseOrder[@id=\"2\"]";
        // Amb el try, fem que si dona algun error, et digui l'error i segueixi corrent el codi en lloc de parar
        try {
        service.query(deleteComanda);
        System.out.println("S'ha esborrat la comanda amb id 2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeComanda1() throws Exception{
        String updateStudentNoteQuery = "update replace /PurchaseOrders/PurchaseOrder[@id=\"1\"]" + 
                                        "/Items/Item[@partNumber=\"324-QW\"]/Quantity with <Quantity>5</Quantity>";
        try {
        service.query(updateStudentNoteQuery);
        System.out.println("S'ha modificat la quantitat de mouse a 5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeComanda2() throws Exception{
        String updateCity = "update replace /PurchaseOrders/PurchaseOrder[@id=\"3\"]" + 
                            "/ShipTo/City with <City>San Francisco</City>";
        String updareState = "update replace /PurchaseOrders/PurchaseOrder[@id=\"3\"]" + 
                            "/ShipTo/State with <State>CA</State>";
        String updateZip = "update replace /PurchaseOrders/PurchaseOrder[@id=\"3\"]" + 
                            "/ShipTo/Zip with <Zip>94103</Zip>";
        try {
            service.query(updateCity);
            service.query(updareState);
            service.query(updateZip);
            System.out.println("S'ha modificat la localitzacio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addAtribute() throws Exception{
        String addPriority = "update insert attribute Priority {\"High\"} into /PurchaseOrders/PurchaseOrder[@id=\"3\"]";
        try {
        service.query(addPriority);
        System.out.println("S'ha afegit l'atribut Priority");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void calculateCost() throws Exception{
        String addPriority =  "for $order in /PurchaseOrders/PurchaseOrder" + " let $total := sum(for $item in $order/Items/Item"
        + " return number($item/USPrice) * number($item/Quantity))" + " return" + " <Order id=\"{data($order/@id)}\">" 
        + " <TotalCost>{format-number($total, '0.00')}</TotalCost>" + " </Order>";
        try {
        service.query(addPriority);
        System.out.println("S'ha creat la consulta per a calcular el cost total");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void list() throws Exception{

    }

    public static void newDocuemnt() throws Exception{

    }

    public static void addElements() throws Exception{
        
    }

    public static void comprovacio() throws Exception{
        // Executem una consulta per verificar la inserció
        ResourceSet result = service.query("for $b in /PurchaseOrders return $b");
        ResourceIterator i = result.getIterator();
                
        if(!i.hasMoreResources()) {
            System.out.println("La consulta no retorna res");
        }
        //Recorrem els resultats i els mostrem per consola
        while(i.hasMoreResources()) {
            Resource r = i.nextResource();
            System.out.println((String) r.getContent());
        }
    }
}