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
 * <ul>La classe qui génère le Thread qui s'occupe de transmettre les messages<br>Des visiteurs à tout utilisateur non authentifié
 * <li>instancie les Thread qui gèrent le flux des visiteurs</li>
 * <li>vérifie si le visiteur a fait une demande de déconnexion</li>
 * </ul>
 * @author anamwa
 */
public class ServerThreadVisitor extends Thread {
    
    public Socket socketVisitor = null;
    InputStreamReader streamMessage = null;
    BufferedReader donneeIn = null;
    String connecter = null;
    PrintWriter donneeOut = null;
    
    /**
     * Le constructeur du thread
     * @param y 
     */
    public ServerThreadVisitor(Socket y){
        this.socketVisitor = y;
    }
    
    /**
     * Méthode "run" qui éxecute le code d'acheminement pour chaque Thread de visiteur à tous les autres visiteurs.
     */
    @Override
    public void run(){
        while(true){
            try{
                streamMessage = new InputStreamReader(socketVisitor.getInputStream());			
                donneeIn = new BufferedReader(streamMessage);
                connecter = donneeIn.readLine();
                
                //lorsque on reçoit la séquence entrante :
                if (connecter.compareTo("###deconnexion###")==0){
                    int dec = ChatServ.listeSocketV.indexOf(socketVisitor);
                    ChatServ.listeSocketV.remove(dec);
                    ChatServ.listePseudoV.remove(dec);
                    
                    donneeOut = new PrintWriter(socketVisitor.getOutputStream()); //Envoyer l'ordre d'arrêter le Threadreception au niveau de l'application utilisateur
                    donneeOut.println("##deconnexion##");
                    donneeOut.flush();
                    
                    ServerThreadVisitor.currentThread().stop(); //Arrêter le Thread en cours.
                }else{
                    int indice = ChatServ.listeSocketV.indexOf(socketVisitor);
                    String pseudoEmetteur = ChatServ.listePseudoV.get(indice);
                    connecter = pseudoEmetteur + " : " + connecter;
                    
                    for(int i = 0; i< ChatServ.listeSocketV.size(); i++){   //Envoyer le flux à tous les visiteurs.
                        donneeOut = new PrintWriter(ChatServ.listeSocketV.get(i).getOutputStream());
                        donneeOut.println(connecter);
                        donneeOut.flush();
                    }
                }
            }catch(Exception e){
                System.out.println("Erreur" + e.getMessage());
            }
        }
    }
}
