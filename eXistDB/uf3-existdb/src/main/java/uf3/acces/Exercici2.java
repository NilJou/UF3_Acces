package uf3.acces;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;

public class Exercici2 {
    static Collection col;
    static XPathQueryService queryService;
    public static void main(String[] args) throws Exception{
        connect();
        col.close();
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
        col = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db/UF3", "admin", "");

        //Inicialitzem res, un objecte de tipus XMLResource obtenint el recurs basedades.xml des de la col·lecció col. Aquest fitxer conté el contingut XML que posteriorment consultarem
        XMLResource res = null;
        res = (XMLResource) col.getResource("PurchasOrders.xml");

        //if que comprova si basedades.xml es troba a la base de dades de eXistDB o no
        if (res == null) {
            System.out.println("No s'ha conectat amb el ficher PurchasOrder.xml");
        } else {
            System.out.println("S'ha conectat amb el ficher PurchasOrder.xml");
        }
        
        // Ara podem realitzar consultes sobre el document XML
        queryService = (XPathQueryService) col.getService("XPathQueryService", "1.0");
    }

    public static void showVenta() throws Exception{

        // a. Mostrar tots els purchaseOrder sota l’etiqueta venta
        String queryA = "//venta/purchaseOrder";
        ResourceSet resultA = queryService.query(queryA);
        ResourceIterator iteratorA = resultA.getIterator();
        System.out.println("PurchaseOrders sota l'etiqueta venta:");
        while (iteratorA.hasMoreResources()) {
            Resource r = iteratorA.nextResource();
            System.out.println(r.getContent());
        }
    }


    public static void showSeattle() throws Exception{
        // b. Mostrar tots els purchaseOrder de Seattle
        String queryB = "//purchaseOrder[shipTo/city='Seattle']";
        ResourceSet resultB = queryService.query(queryB);
        ResourceIterator iteratorB = resultB.getIterator();
        System.out.println("\nPurchaseOrders de Seattle:");
        while (iteratorB.hasMoreResources()) {
            Resource r = iteratorB.nextResource();
            System.out.println(r.getContent());
        }
    }

    public static void showUSP50() throws Exception{
        // c. Mostrar tots els purchaseOrder amb USPrice superior a 50
        String queryC = "//purchaseOrder[USPrice > 50]";
        ResourceSet resultC = queryService.query(queryC);
        ResourceIterator iteratorC = resultC.getIterator();
        System.out.println("\nPurchaseOrders amb USPrice superior a 50:");
        while (iteratorC.hasMoreResources()) {
            Resource r = iteratorC.nextResource();
            System.out.println(r.getContent());
        }
    }

    public static void show2Articles() throws Exception{
        // d. Mostrar tots els purchaseOrder amb més de dos articles
        String queryD = "//purchaseOrder[count(item) > 2]";
        ResourceSet resultD = queryService.query(queryD);
        ResourceIterator iteratorD = resultD.getIterator();
        System.out.println("\nPurchaseOrders amb més de dos articles:");
        while (iteratorD.hasMoreResources()) {
            Resource r = iteratorD.nextResource();
            System.out.println(r.getContent());
        }
    }

    public static void renameNode() throws Exception{
        // e. Renombrar el node PurchaseOrder a Ventas
        String queryE = "for $po in //purchaseOrder return rename node $po as venta";
        queryService.query(queryE); // No necessitem imprimir res
    }

    public static void modifyItem33() throws Exception{
        // f. Modificar la quantity del primer Venta, el primer ítem a 33
        String queryF = "let $firstVenta := //venta[1]/item[1] return replace value of node $firstVenta/quantity with 33";
        queryService.query(queryF); // No necessitem imprimir res
    }

    public static void deleteThirdPO() throws Exception{
        // g. Eliminar el tercer PurchaseOrder
        String queryG = "let $thirdPO := //purchaseOrder[3] return delete $thirdPO";
        queryService.query(queryG); // No necessitem imprimir res
    }

    public static void countPO() throws Exception{
         // h. Comptar tots els PurchaseOrder
        String queryH = "count(//purchaseOrder)";
        ResourceSet resultH = queryService.query(queryH);
        int countH = Integer.parseInt(resultH.getIterator().nextResource().getContent().toString());
        System.out.println("\nTotal PurchaseOrders: " + countH);
    }    
}