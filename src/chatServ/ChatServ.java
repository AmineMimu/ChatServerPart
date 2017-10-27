/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatServ;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <ul>la classe main de l'application côté serveur, reçoit une séquence de l'utilisateur<br>
 * Se déroule comme ceci :
 * <li>séquence = inscription --> execution des instructions d'inscription (sur BDD aussi) <strong> Vérifiée dans la BDD</strong></li>
 * <li>séquence = visite --> execution des instructions de visiteur</li>
 * <li>séquence = connexion --> execution des instructions de vérification d'authentification, <strong> Vérifiée dans la BDD</strong></li>
 * </ul>
 * @author anamwa
 */
public class ChatServ {
    
    public static ArrayList<Socket> listeSocket = new ArrayList();
    public static ArrayList<String> listePseudo = new ArrayList();
    public static ArrayList<Socket> listeSocketV = new ArrayList();
    public static ArrayList<String> listePseudoV = new ArrayList();
    
    /**
     * Méthode "main" du Serveur, où vérifier l'inscription, et la connexion dans la BDD<br>Et les visiteurs dans la collection de données
     * @param args the command line arguments
     */    
    public static void main(String[] args) {
        // TODO code application logic here
        
        int portEcoute = 4444;
        Socket socket;
        ServerSocket serveurSocket;
        InputStreamReader streamMessage;
        BufferedReader donneeIn;
        PrintWriter donneeOut;
        String connecter = null, indice = null;
        
        
        try{
            serveurSocket = new ServerSocket(portEcoute);            
            
            while(true){
                            //Attente de client
                System.out.println("Initiation de la connexion\nAttente d'un client");
                socket = serveurSocket.accept();
                System.out.println("Connexion acceptée");
                
                streamMessage = new InputStreamReader(socket.getInputStream());			
                donneeIn = new BufferedReader(streamMessage);
                connecter = donneeIn.readLine();
                
                String tab[] = connecter.split("#");
                indice = tab[0];
                
               
                //Vérification si l'utilisateur veut s'inscrire
                //puis si oui éxecuter son souhait d'inscription.
                if(indice.compareTo("inscription")==0){
                    
                    String table[] = connecter.split(",");
                    String exist = table[5];
                    String email[] = exist.split("'");
                    
                    // Vérifier si l'e-mail utilisé n'a pas déjà été stocké dans ma BDD.                     
                    try{ 
                        Connection connex = connexionBDD();
                        PreparedStatement ps2 = connex.prepareStatement("SELECT * from utilisateur where emailUtilisateur ='"+email[1]+"';");
                        ResultSet result = ps2.executeQuery();
                        int trouve=0;
                        while(result.next()){
                             if(email[1].equals(result.getString(6))){
                                trouve=1; 
                             }
                        }
                        
                        //Si l'e-mail exite dans ma BDD je renvois au client une erreur.               
                        if (trouve==1){
                            donneeOut = new PrintWriter(socket.getOutputStream());
                            connecter = "nok";
                            donneeOut.println(connecter);
                            donneeOut.flush();
                       
                            socket.close();
                            connex.close();
                            
                        //Sinon j'envoie OK à l'application coté client.
                        }else{
                            PreparedStatement ps = connex.prepareStatement(tab[1]);
                            ps.execute();
                            donneeOut = new PrintWriter(socket.getOutputStream());
                            donneeOut.println("ok");
                            donneeOut.flush();                        
                        
                            socket.close();
                            connex.close();
                        }
                        
                    }catch(SQLException e){
                        System.out.println("Erreur SQL :" +e.getMessage());
                        socket.close();
                    }
                }
                
                
                //Vérification si l'utilisateur veut se connecter comme invité
                //Puis éxecuter son souhait d'une connexion visiteur.
                else if (indice.compareTo("visite")==0){    //Vérifier l'existence du Pseudo dans la liste
                    int exist = 1;
                    for (int i=0; i < listePseudoV.size(); i++){
                        if (tab[1].compareTo(listePseudoV.get(i))==0)
                            exist = 0;          
                    }
                    if(exist==0){       //Pseudo existe et non accepté
                        donneeOut = new PrintWriter(socket.getOutputStream());
                        donneeOut.println("pseudoExist");
                        donneeOut.flush();
                    
                        socket.close(); 
                    }else{      //Pseudo accepté
                        donneeOut = new PrintWriter(socket.getOutputStream());
                        donneeOut.println("pseudoCorrect");
                        donneeOut.flush();
                        
                        listeSocketV.add(socket);
                        listePseudoV.add(tab[1]);
                        
                        ServerThreadVisitor objt = new ServerThreadVisitor(socket);
                        objt.start();
                    }
                                       
                }
                  
                //Vérifier si l'utilisateur a les bons coordoonées pour une connexion
                //puis executer la connexion.
                else if (indice.compareTo("connexion")==0){
                 
                    try{    //Comparaison entre le mot de passe introduit et celui qui figure sur la BDD
                        Connection connex2 = connexionBDD();
                        PreparedStatement ps = connex2.prepareStatement("SELECT * from utilisateur where emailUtilisateur ='"+tab[1]+"';");
                        ResultSet result = ps.executeQuery();
                        int trouve=1;                        
                        while(result.next()){
                            if(tab[2].equals(result.getString(4)) && tab[1].equals(result.getString(6))){ //MDP incorrect
                                trouve = 0;
                            }
                            else{
                                trouve=1;
                            }
                        }
                        
                        if(trouve == 0){    //Le bon mot de passe
                            donneeOut = new PrintWriter(socket.getOutputStream());
                            donneeOut.println("okMDP");
                            donneeOut.flush();
                            
                            listeSocket.add(socket);
                            listePseudo.add(tab[1]);
                            
                            ServerThreadClient objetComm = new ServerThreadClient(socket);
                            objetComm.start();
                            
                        }else{//login ou mot de passe faux
                            donneeOut = new PrintWriter(socket.getOutputStream());
                            donneeOut.println("nop");
                            donneeOut.flush();
                        }
                    }catch(SQLException e){
                        System.out.println("Erreur connexion "+ e.getMessage());
                    }
                }else{
                    System.out.println("Erreur, valeur reçue ne correspond pas aux propositions");
                }
            }
        }catch(Exception e){
            
        }
    }
    
    /**
     * Méthode de connexion à la base de données
     * @return 
     */
    public static Connection connexionBDD(){
        Connection cunt = null;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println("Erreur driver :" + e.getMessage());
        }
        try{    //Mot de passe de conexion : "motdepasse", Utilisateur : "root"
            cunt = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatApp?zeroDateTimeBehavior=convertToNull", "root", "motdepasse");
        }catch(SQLException e){
            System.out.println("Erreur SQL :" + e.getMessage());
        }
        
        return cunt;
    }
}