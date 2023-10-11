package modele;

import java.util.Observable;
import java.util.Random;
import java.util.HashMap;


//Pour la sauvegarde
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Jeu extends Observable {
    private Case[][] tabCases;     
    private static Random rnd = new Random();
    private HashMap<Case, Coord> hashm = new HashMap<Case, Coord>();
    private int size;
    private int score;
    private int bestScore;
    private int coup;
    private boolean retourPossible;
    private boolean verrouiller;

    private Case[][] tabCasesRetour; 
    private HashMap<Case, Coord> hashmRetour = new HashMap<Case, Coord>();
    private int scoreRetour;

    private boolean fin;
    private IA2048 ia;


    public Jeu(int _size, boolean avoirIA) {
        tabCases = new Case[_size][_size];        
        size = _size;
        init();
        bestScore = 0;

        tabCasesRetour = new Case[_size][_size];
        

        if(avoirIA){
            ia = new IA2048(this);
        }
        else{
            ia = null;
        }
        
    }

    public int getSize() {
        return tabCases.length;
    }

    public Case getCase(int i, int j) {
        return tabCases[i][j];
    }

    public int getScore(){
        return score;
    }

    public int getBestScore(){
        return bestScore;
    }

    public int getCoup(){
        return coup;
    }

    public boolean getVerrou(){
        return verrouiller;
    }

    public void setVerrou(boolean ver){
        verrouiller = ver;
    }

    public boolean getFin(){
        return fin;
    }


    public void init() {
        if(!verrouiller){
            Thread t = new Thread() {
                public void run() {
                    for (int i = 0; i < tabCases.length; i++) {
                        for (int j = 0; j < tabCases.length; j++) {
                                tabCases[i][j] = null;
                        }
                    }     
    
                    score = 0;
                    coup = 0;
                    retourPossible = false;
                    verrouiller = false;
                    fin = false;
                    scoreRetour = 0;
                    
                    nouvelleCase();
                    nouvelleCase();
    
                    setChanged();
                    notifyObservers();
                }
            };
            t.start();

        }    
        else{
            System.out.println("Action utilisateur bloquée...");
        }    
        
    }

    public void afficheTab(){
        for (int i = 0; i<size; i++) {
            for (int j = 0; j<size; j++) {
                if(tabCases[i][j] != null){
                    System.out.print(tabCases[i][j].getValeur() + " ");
                }
                else{
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }

        System.out.println("score = " + score);
    }

    // ******************************************************
    // ------------------------------------------------------
    // ---------------------NOYAU DU JEU---------------------
    // ------------------------------------------------------
    // ******************************************************

    public void jouerSiPossible(Direction d){
        if(!verrouiller){
            action(d);
        }
        else{
            System.out.println("Action utilisateur bloquée...");
        }
    }

    public void action(Direction d){
        Thread t = new Thread() {
            public void run() {
                boolean mouvement = false;

                //On fait une sauvegarde que si le déplacement est possible           
                if (testMouvement(d))
                {
                    copyTab1toTab2(tabCases, tabCasesRetour, hashmRetour);
                    scoreRetour = score;
                }

                mouvement = deplacement(d);

                if(mouvement){
                    retourPossible = true;
                    coup++;
                    nouvelleCase();                    
                }
                if(testFinJeu()){
                    fin = true;
                }              
                
                resetFusion();                
                updateScreen();

            }
        };
        t.start();          
    }

    public boolean deplacement(Direction d){
        int i,j;
        boolean mouvement = false;

        //System.out.println("Dep");

        switch(d){
            case haut:
                for(j = 0; j<size; j++){
                    for(i = 1; i<size; i++){
                        if(tabCases[i][j] != null){
                            if (tabCases[i][j].deplacer(d)) { mouvement = true; };
                        }
                    }
                }                
                break;
            case bas:
                for(j = 0; j<size; j++){
                    for(i = size - 2; i >= 0; i--){
                        if(tabCases[i][j] != null){
                            if (tabCases[i][j].deplacer(d)) { mouvement = true; };
                        }
                    }
                }
                break;
            case gauche:
                for(i = 0; i<size; i++){
                    for(j = 1; j<size; j++){
                        if(tabCases[i][j] != null){
                            if (tabCases[i][j].deplacer(d)) { mouvement = true; };
                        }
                    }
                }
                break;
            case droite:
                for(i = 0; i<size; i++){
                    for(j = size - 2; j >= 0; j--){
                        if(tabCases[i][j] != null){
                            if (tabCases[i][j].deplacer(d)) { mouvement = true; };
                        }
                    }
                }
                break;
        }

        return mouvement;

    }

    public void nouvelleCase(){
        int i; 
        int j; 
        int valeur;

        //On selectionne une case vide
        do{
            i = rnd.nextInt(size);
            j = rnd.nextInt(size);
        }while(tabCases[i][j] != null);      

        //On attribue une valeur [0:9]
        valeur = rnd.nextInt(10);

        //La valeur 4 apparait une fois sur 10.
        if(valeur == 9){
            valeur = 4;
        }
        else{
            valeur = 2;
        }

        //On stocke la case
        tabCases[i][j] = new Case(valeur, this);
        hashm.put(tabCases[i][j], new Coord(i, j));
    }

    public void updateScreen(){
        setChanged();
        notifyObservers();
    }

    public void retourSiPossible(){
        if(!verrouiller && retourPossible){
            retour();
        }
        else{
            System.out.println("Action utilisateur bloquée...");
        }        
    }

    public void retour(){
        copyTab1toTab2(tabCasesRetour, tabCases, hashm);

        retourPossible = false;
        coup--;
        fin = false;
        score = scoreRetour;
        updateScreen();
    }
    
    // ******************************************************
    // ------------------------------------------------------
    // --------------FONCTION GERANT LES CASES---------------
    // ------------------------------------------------------
    // ******************************************************

    public void removeCase(Case c){
        Coord coord = this.hashm.get(c);

        tabCases[coord.x][coord.y] = null;
        this.hashm.remove(c);
    }

    public void deplacementUnitaireCase(Case c, Direction d){
        Coord coord;
        switch(d){
            case haut:
                coord = this.hashm.get(c);
                removeCase(c);

                tabCases[coord.x-1][coord.y] = c;
                hashm.put(c,new Coord(coord.x-1, coord.y));
                break;
            case bas:
                coord = this.hashm.get(c);
                removeCase(c);

                tabCases[coord.x+1][coord.y] = c;
                hashm.put(c,new Coord(coord.x+1, coord.y));
                break;
            case gauche:
                coord = this.hashm.get(c);
                removeCase(c);

                tabCases[coord.x][coord.y-1] = c;
                hashm.put(c,new Coord(coord.x, coord.y - 1));
                break;
            case droite:
                coord = this.hashm.get(c);
                removeCase(c);

                tabCases[coord.x][coord.y + 1] = c;
                hashm.put(c,new Coord(coord.x, coord.y + 1));
                break;
        }
    }

    public Coord getCoordCase(Case c){
        return this.hashm.get(c);
    }

    public Case getVoisin(Case c, Direction d){
        Coord coord;
        int i;
        int j;
        boolean erreur;
        
        erreur = false;
        coord = hashm.get(c);

        i = coord.x;
        j = coord.y;

        switch(d){
            case haut:
                i--;
                if(i<0){
                    erreur = true;
                }                
                break;

            case bas:
                i++;
                if(i>=size){
                    erreur = true;
                }
                break;

            case gauche:
                j--;
                if(j<0){
                    erreur = true;
                }                
                break;

            case droite:
                j++;
                if(j>=size){
                    erreur = true;
                }
                break;
        }

        if(erreur){
            return new Case(0, this);
        }
        else{
            return tabCases[i][j];
        }
        
    }

    public void resetFusion(){
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                if(tabCases[i][j] != null){
                    tabCases[i][j].setFusion(false);
                }
            }
        }
    }

    private boolean testMouvement(Direction dir)
    {
        Case c;
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                if(tabCases[i][j] != null){
                    c = getVoisin(tabCases[i][j], dir);
                    if(c == null){
                        return true;
                    }
                    else{
                        if(c.getValeur() == tabCases[i][j].getValeur()){
                            return true;

                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean testFinJeu(){
        for(Direction d : Direction.values()) {
            if(testMouvement(d)){
                return false;
            }
        }
        return true;
    }

    public void incrementeScore(int n){
        score += n;
        if (score >= bestScore) { 
            bestScore = score; 
        }
    }

    public void reinitBestScore(){
        bestScore = 0;
        updateScreen();
    }

    private void copyTab1toTab2(Case [][] tab1, Case [][] tab2, HashMap<Case, Coord> hash2){
        Case c;

        hash2.clear();
        for (int i = 0; i<size; i++) {
            for (int j = 0; j<size; j++) {
                if(tab1[i][j] != null){
                    c = new Case(tab1[i][j].getValeur(), this);
                    tab2[i][j] = c;
                    hash2.put(c, new Coord(i, j));
                }
                else{
                    tab2[i][j] = null;
                }
            }
        }
    }


    // ******************************************************
    // ------------------------------------------------------
    // -------------------FONCTION POUR IA-------------------
    // ------------------------------------------------------
    // ******************************************************

    public void copyProfondeFrom(Jeu jeu){

        /* Copie des valeurs */
        size = jeu.size;
        score = jeu.score;
        rnd = jeu.rnd;

        hashm.clear();        
        copyTab1toTab2(jeu.tabCases, tabCases, hashm);

        /* Tab et Hasmap Retour */
        hashmRetour.clear();
        scoreRetour = jeu.scoreRetour;
        copyTab1toTab2(jeu.tabCasesRetour, tabCasesRetour, hashmRetour);
    }

    public void IAplay(){  

        //On vérifie que le jeu contient une IA (normalement toujours vrai)
        if(ia != null){

            //Si l'IA n'est pas lancée, on la démarre dans un nouveau thread
            if(!ia.getRun()){
                Jeu jeuDsThread = this; 
                Thread t = new Thread() {
                    
                    public void run() {  
                        ia.IAstart(jeuDsThread,400); 
                    }
                };
                t.start();
            }
            else{
                ia.IAstop(this);
            }
        }
    }

    public void IACoupSimple(){
        if(ia != null && !verrouiller){
            Jeu jeuDsThread = this; 
            Thread t = new Thread() {
                    
                public void run() {  
                    ia.IADeplacement(jeuDsThread,400);
                }
            };
            t.start();

        }else{
            System.out.println("Action utilisateur bloquée...");
        }
        
    }

    // ******************************************************
    // ------------------------------------------------------
    // -------------FONCTION SAUVEGARDE FICHIER--------------
    // ------------------------------------------------------
    // ******************************************************

    public void LireDepuisFichier() throws NumberFormatException, IOException{
        /**
         * ---- Format du fichier ----
         * Longueur Largeur
         * Score
         * NbrCoup
         * ....
         * ....
         * ....
         * ....
         */
        if(!verrouiller){
            System.out.println("Lecture du fichier de sauvegarde...");
            Case[][] tab; 
            BufferedReader reader = new BufferedReader(new FileReader("sauvegarde.txt"));
            String[] cols;
            int x,y;
            String line = "";
            int lig = 0;
            int col = 0;

            //Lecture des dimensions de la grille
            line = reader.readLine();
            cols = line.split(" ");
            x = Integer.parseInt(cols[0]);
            y = Integer.parseInt(cols[1]);
            tab = new Case[x][y];

            //Lecture du score + nbrCoup
            score = Integer.parseInt(reader.readLine());
            if(score > bestScore){
                bestScore = score;
            }
            coup = Integer.parseInt(reader.readLine());

            //Lecture de la grille
            while((line = reader.readLine()) != null){
                cols = line.split(" ");
                col = 0;
                for(String  c : cols)
                {
                    if(!c.equals("0")){
                        tab[lig][col] = new Case(Integer.parseInt(c),this);
                    }
                    else{
                        tab[lig][col] = null;
                    }
                    
                    col++;
                }
                lig++;
            }
            reader.close();

            copyTab1toTab2(tab, tabCases, hashm);
            fin = false;
            updateScreen();


        }else{
            System.out.println("Action utilisateur bloquée...");
        }
        

    }

    public void EcrireDansFichier() throws IOException{
        /**
         * ---- Format du fichier ----
         * Longueur Largeur
         * Score
         * NbrCoup
         * ....
         * ....
         * ....
         * ....
         */
        System.out.println("Ecriture du fichier de sauvegarde...");

        StringBuilder builder = new StringBuilder();

        builder.append(size+" "+size+"\n");
        builder.append(score+"\n");
        builder.append(coup+"\n");

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(tabCases[i][j] != null){
                    //On écrit la valeur dans la chaine
                    builder.append(tabCases[i][j].getValeur());
                }
                else{
                    builder.append("0");
                }
                
                if(j < size - 1)//if this is not the last row element
                    builder.append(" ");//then add comma (if you don't like commas you can use spaces)
            }
            builder.append("\n");//append new line at the end of the row
        }

        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter("sauvegarde.txt"));
        writer.write(builder.toString());
        writer.close();

        System.out.println("Fichier sauvegardé...");
    }
}
