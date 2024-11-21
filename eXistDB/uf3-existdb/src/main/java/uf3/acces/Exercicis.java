package uf3.acces;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.XMLResource;

public class Exercicis {
    public static void main(String[] args) throws Exception{    
        connect();
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
            System.out.println("El fitxer NO el troba");
        } else {
            System.out.println("El fitxer el troba");
        }   
    }
}