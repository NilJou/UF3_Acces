package existdb;


//llibreries necessàries de les dependències del pom.xml
import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;


//definim la classe existdb amb el mètode principal mail, enllaçant Exception per simplificar la gestió d'errors
public class existdb {
    public static void main(String[] args) throws Exception {
    	
    	//defineix el driver necessàri per connectar-se a la base de dades eXistDB i carrega la classe per tal d'instanciar-lo
        String driver = "org.exist.xmldb.DatabaseImpl";
        Class<?> cl = Class.forName(driver);

        // Creem una instància del driver DatabaseImpl per tal de poder utilitzar la connexió amb eXistDB
        Database database = (Database) cl.getDeclaredConstructor().newInstance();

        // Registre l'objecte Database amb DatabaseManager per tal que altres metodes accedeixin a eXistDB utilitzant la connexió creada
        DatabaseManager.registerDatabase(database);

        // Obtenim la col·lecció creada al nostre eXistDB del nostre servidor local localhost a través del port 8080, retornant un objecte del tipus Collection que representa la col·lecció XML, amb el nom d'usuari i la contrasenya
        Collection col = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db/basedades1", "admin", "qwertyuiop");

        //Inicialitzem res, un objecte de tipus XMLResource obtenint el recurs basedades.xml des de la col·lecció col. Aquest fitxer conté el contingut XML que posteriorment consultarem
        XMLResource res = null;
        res = (XMLResource) col.getResource("basedades.xml");
        //if que comprova si basedades.xml es troba a la base de dades de eXistDB o no
        if (res == null) {
            System.out.println("El fitxer NO el troba");
        } else {
            System.out.println("El fitxer el troba");
        }
        
        //Creem un servei de consulta en format XPath (XPathQueryService) per poder realitzar consultes a la base de dades, especificant la versió 3.1
        XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "3.1");
        //La consola retorna el format en pretty, és a dir, llegible pels humans
        service.setProperty("pretty", "true");
        //Defineix la codificació de caràcters com ISO-8859-1 per tal de poder tenir els caràcters que usem ç,ñ ...
        service.setProperty("encoding", "ISO-8859-1");
        
       
        //Executa la consulta XPath sobre el rec   

        // Afegim una nova entrada d'alumne amb el nom David i nota 10
        String addStudentQuery = "update insert <alumne id='3'><nom>David</nom><nota>10</nota></alumne> into /classe";
        service.query(addStudentQuery);
        System.out.println("S'ha afegit un nou alumne amb el nom David i nota 10.");
	        
     	// Esborrar l'alumne amb nom David i nota 10
        String deleteStudentQuery = "update delete /classe/alumne[nom='David' and nota='10']";
        service.query(deleteStudentQuery);
        System.out.println("S'ha esborrat l'alumne David amb nota 10.");

        // Modificar la nota de l'alumne Pere a 7
        String updateStudentNoteQuery = "update replace /classe/alumne[nom='Pere']/nota with <nota>7</nota>";
        service.query(updateStudentNoteQuery);
        System.out.println("S'ha modificat la nota de Pere a 7.");
        
        
        // Executem una consulta per verificar la inserció
        ResourceSet result = service.query("for $b in /classe/alumne return $b");
        ResourceIterator i = result.getIterator();
        
        if(!i.hasMoreResources()) {
        	System.out.println("La consulta no retorna res");
        }
        //Recorrem els resultats i els mostrem per consola
        while(i.hasMoreResources()) {
        	Resource r = i.nextResource();
        	System.out.println((String) r.getContent());
        }
        //Tanquem la connexió per alliberar els recursos.
        col.close();
    }
}