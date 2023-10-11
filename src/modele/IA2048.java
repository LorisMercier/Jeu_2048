package modele;

import java.util.Random;

public class IA2048 {
    //private Jeu jeu;
    private Jeu jeuIA;
    private Jeu jeuSim;
    private static Random rnd;
    private boolean run;
    public double memScore = 0.0;

    public IA2048(Jeu _jeu){
        //jeu = _jeu;
        jeuIA = new Jeu(_jeu.getSize(),false);
        jeuSim = new Jeu(_jeu.getSize(),false);
        rnd = new Random();
        run = false;
    }


    public boolean getRun(){
        return run;
    }

    public void IAstart(Jeu jeu, int nbrPartie){
        IAstart(jeu, nbrPartie, Integer.MAX_VALUE);
    }

    public void IAstart(Jeu jeu, int nbrPartie, int profondeur){
        int i = 0;
        int coup = 0;
        run = true;
        jeu.setVerrou(true);
        
        //1 chance 100 de ne pas avoir de 4
        while(i<50 && run){
            while(!jeu.testFinJeu() && run){
            
                IADeplacement(jeu, nbrPartie, profondeur);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            } 
            //Si on effectue plus d'un coup, on remet le compteur du nombre
            //de retour à zéro.
            if(jeu.getCoup() - coup > 1){
                i = 0;
            }    
            coup = jeu.getCoup();    
            
            //On effectue un retour sauf à la dernière itération pour finir
            //proprement le jeu
            if(i<49 && run){
                //System.out.println("Retour : " + i);
                jeu.retour();    
            }             
            i++;
        }
        run = false;         
        jeu.setVerrou(false);
    }

    public void IAstop(Jeu jeu){
        run = false;        
    }

    public synchronized void IADeplacement(Jeu jeu, int nbrPartie){
        IADeplacement(jeu, nbrPartie, Integer.MAX_VALUE);
    }

    public synchronized void IADeplacement(Jeu jeu, int nbrPartie, int profondeur){
        Direction dir = IAMeilleurDeplacement(jeu,nbrPartie,profondeur);                    
        //System.out.println("Direction : " + dir );
        jeu.action(dir);
    }    

   
    private Direction IAMeilleurDeplacement(Jeu jeu, int nbrPartie, int profondeur){
        Direction dir;
        double [] tabScore = new double[4];
        double scorecumule = 0.0;
        boolean mouvement = false;
        int nbrCoup = 0;

        for(Direction d : Direction.values()) {
            jeuIA.copyProfondeFrom(jeu);

            mouvement = jeuIA.deplacement(d);
            tabScore[d.ordinal()] = jeu.getScore();
            scorecumule = 0;

            
            if(mouvement){
                jeuIA.nouvelleCase();
                jeuIA.resetFusion();                

                for(int i = 0; i < nbrPartie; i++){
                    jeuSim.copyProfondeFrom(jeuIA);
                    nbrCoup = 0;
        
                    while(!jeuSim.testFinJeu() && nbrCoup < profondeur){              
                        dir = Direction.values()[rnd.nextInt(Direction.values().length)];
                        
                        mouvement = jeuSim.deplacement(dir);
                        if(mouvement){
                            jeuSim.nouvelleCase(); 
                            jeuSim.resetFusion();                           
                        }                        
                        jeuSim.updateScreen();
                        nbrCoup++;
               
                    }                
                    scorecumule += jeuSim.getScore();
        
                }
                
                tabScore[d.ordinal()] = scorecumule / nbrPartie;
            }
            
        
        }

        //On cherche le meilleur coup
        int indMax = maxTab(tabScore);
        memScore = tabScore[indMax];

        return Direction.values()[indMax];
    }

    /**
     * Indice du maximum
     */
    private int maxTab(double [] tab){
        double max = tab[0];
        int indMax = 0;

        for (int i = 0; i< tab.length ; i++) {
            //Si score égal, on change l'indice une fois sur deux
            if (tab[i] > max || (tab[i] == max && rnd.nextInt(2) == 0) ){
                max = tab[i];                
                indMax = i;                    
            }
                
        }

        return indMax;
    }

    
}
