/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatServ;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * <ul>La classe d'acheminement de flux où :
 * <li>instancie les Thread qui gèrent le flux de chaque utilisateur authentifié</li>
 * <li>vérifie si l'utilisateur authentifié a fait une demande de rafraichissement</li>
 * <li>vérifie si l'utilisateur authentifié a fait une demande de déconnexion</li>
 * </ul>
 * @author anamwa
 */
public class ServerThreadClient extends Thread {
    public Socket socketThreadClient = null;
    public String pseudoDst = null;
    public BufferedReader donneeIn = null ;
    public PrintWriter donneeOut;
    public InputStreamReader streamMessage = null;
    public String connecter = null ;
    
    /**
     * Le constructeur du thread
     * @param y 
     */
    public ServerThreadClient(Socket y){
        this.socketThreadClient = y;
    }
    
    /**
     * Méthode "run" qui éxecute le code d'acheminement pour chaque Thread d'utilisateur authentifié
     */
    @Override
    public void run(){
        
        while(true){
            
            try{
                streamMessage = new InputStreamReader(socketThreadClient.getInputStream());			
                donneeIn = new BufferedReader(streamMessage);
                connecter = donneeIn.readLine();			
                               
                //Si on reçoit la séquence entrante
                if (connecter.compareTo("##actualiser##")==0){ //Actualiser
                  
                    String listeConnectes = "";
                    for (int i=0; i < ChatServ.listePseudo.size(); i++){                        
                        listeConnectes = listeConnectes+ChatServ.listePseudo.get(i)+",";                        
                    }                    
                    listeConnectes = "##actualiser##" + listeConnectes;
                    donneeOut = new PrintWriter(socketThreadClient.getOutputStream());
                    donneeOut.println(listeConnectes);
                    donneeOut.flush();
                //Si séquence reçue pour déconnexion
                }else if(connecter.compareTo("##deconnexion##")==0){
                    int dec = ChatServ.listeSocket.indexOf(socketThreadClient);
                    ChatServ.listeSocket.remove(dec);
                    ChatServ.listePseudo.remove(dec);
                    donneeOut = new PrintWriter(socketThreadClient.getOutputStream());
                    donneeOut.println("##deconnexion##");
                    donneeOut.flush();
                    ServerThreadClient.currentThread().stop();
                    
                }else{      //Sinon acheminer le flux avec les 2 utilisateurs
                    String tab[] = connecter.split(" : ");
                    pseudoDst = tab[0];                
                    int indice = ChatServ.listePseudo.indexOf(pseudoDst);
                    Socket sock = ChatServ.listeSocket.get(indice);
                
                    int indiceEmetteur = ChatServ.listeSocket.indexOf(socketThreadClient);
                    String emetteur = ChatServ.listePseudo.get(indiceEmetteur);
                
                    donneeOut = new PrintWriter(sock.getOutputStream());
                    donneeOut.println(emetteur + " : " +tab[1]);
                    donneeOut.flush();
                }
            }catch(Exception e){
                
            }
        }        
    }    
}