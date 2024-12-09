package uf3.acces;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

public class Exercici3 {
    static Collection col;
    static XPathQueryService queryService;
    static XPathQueryService service;
    public static void main(String[] args) throws Exception {
        connect();
        // addRecepta();
        changeDif();
        // addIngridient();
        comprovacio();
    }

     // Creem un metode per a connectar la base de dades, amb un throws Exception per a la gestio d'errors.
     public static void connect() throws Exception  {
        //Defineix el driver necessàri per connectar-se a la base de dades eXistDB i carrega la classe per tal d'instanciar-lo
        String driver = "org.exist.xmldb.DatabaseImpl";
        Class<?> cl = Class.forName(driver);

        // Creem una instància del driver DatabaseImpl per tal de poder utilitzar la connexió amb eXistDB
        Database database = (Database) cl.getDeclaredConstructor().newInstance();

        // Registre l'objecte Database amb DatabaseManager per tal que altres metodes accedeixin a eXistDB utilitzant la connexió creada
        DatabaseManager.registerDatabase(database);

        // Obtenim la col·lecció creada al nostre eXistDB del nostre servidor local localhost a través del port 8080, retornant un objecte del tipus Collection que representa la col·lecció XML, amb el nom d'usuari i la contrasenya
        // El nostre cas es admin i sense contrasenya
        Collection col = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db/UF3", "admin", "");

        //Inicialitzem res, un objecte de tipus XMLResource obtenint el recurs PurchasOrder.xml des de la col·lecció col. Aquest fitxer conté el contingut XML que posteriorment consultarem
        XMLResource res = null;
        res = (XMLResource) col.getResource("recetas.xml");

        //if que comprova si PurchaseOrders.xml.xml es troba a la base de dades de eXistDB o no
        if (res == null) {
            System.out.println("No s'ha conectat amb el ficher recetas.xml");
        } else {
            System.out.println("S'ha conectat amb el ficher recetas.xml");
        }
        
        //Creem un servei de consulta en format XPath (XPathQueryService)
        //per poder realitzar consultes a la base de dades, especificant la versió 3.1
        service = (XPathQueryService) col.getService("XPathQueryService", "3.1");

        //La consola retorna el format en pretty, és a dir, llegible pels humans
        service.setProperty("pretty", "true");

        //Defineix la codificació de caràcters com ISO-8859-1 per tal de poder tenir els caràcters que usem ç,ñ ...
        service.setProperty("encoding", "ISO-8859-1");
    }
    
    public static void addRecepta() throws Exception{
        // Afegeix una nova recepta
        String addRecepta = "update insert" +
        "<receta>" +
            "<tipo definicion=\"bebida\"/>" +
            "<dificultad>Fácil</dificultad>" +
            "<nombre>Batido de Fresas</nombre>" +
            "<ingredientes>" +
                "<ingrediente nombre=\"fresas\" cantidad=\"300 grams\"/>" +
                "<ingrediente nombre=\"leche\" cantidad=\"200 ml\"/>" +
                "<ingrediente nombre=\"azúcar\" cantidad=\"1 culleradeta\"/>" +
            "</ingredientes>" +
            "<calorias>200</calorias>" +
            "<pasos>" +
                "<paso orden=\"1\">Rentar les freses.</paso>" +
                "<paso orden=\"2\">Afegir tots els ingredients a la licuadora.</paso>" +
                "<paso orden=\"3\">Licuar fins que estigui suau.</paso>" +
            "</pasos>" +
            "<tiempo>10 minutos</tiempo>" +
            "<elaboracion>Licuadora</elaboracion>" +
        "</receta>" + 
        "into /recetas";

        try {
            service.query(addRecepta);
            System.out.println("S'ha afegit una nova recepta: Batut de Maduixa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // No va de moment
    public static void changeDif() {
        String updateStudentNoteQuery = "update replace /recetas/receta[1]/dificultad" + 
        "with <dificultad>Media</dificultad>";
        
        // Amb el try, fem que si dona algun error, et digui l'error i segueixi corrent el codi en lloc de parar
        // Executem l'ordre anterior a l'arrel /PurchaseOrders.
        try {
            service.query(updateStudentNoteQuery);
            System.out.println("S'ha modificat la dificultat del pastis de chocolata");
        } catch (Exception e) {
            e.printStackTrace();   
        }
    }

    // No va de moment
    public static void addIngridient() {
        String addIngredient = "update insert" +
        "<ingrediente nombre=\"chocolate rallado\" cantidad=\"100 gramos\"/>";
        // Amb el try, fem que si dona algun error, et digui l'error i segueixi corrent el codi en lloc de parar
        // Executem l'ordre anterior a l'arrel /PurchaseOrders.
        try {
        service.query(addIngredient);
        System.out.println("S'ha afegit l'ingredient chocolate rallado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void comprovacio() throws Exception{
    // Executem una consulta per verificar la inserció
    ResourceSet result = service.query("for $b in /recetas return $b");
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