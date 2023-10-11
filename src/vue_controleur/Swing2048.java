package vue_controleur;

import modele.Case;
import modele.Direction;
import modele.Jeu;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Swing2048 extends JFrame implements Observer {
    private static final int PIXEL_PER_SQUARE = 120;
    // tableau de cases : i, j -> case graphique
    private JLabel[][] tabC;
    private JLabel instruction;
    private JLabel nbDeplacements;
    private JLabel points;
    private JButton nouveau;
    private JButton undo;
    private JLabel meileurScore;
    private JButton reinitBestScore;
    private JButton AIFullGame;
    private JButton AIMove;
    private Jeu jeu;
    private HashMap<Integer, String> hashCouleur = new HashMap<Integer, String>();

    private JButton EcrireSauv;
    private JButton LireSauv;

    public Swing2048(Jeu _jeu) {
        jeu = _jeu;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setSize(jeu.getSize() * PIXEL_PER_SQUARE, jeu.getSize() * PIXEL_PER_SQUARE);
        tabC = new JLabel[jeu.getSize()][jeu.getSize()];
        this.setTitle("2048");

        //Mise en forme de la hashMap
        int [] valeur = {0,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144,524288,1048576,2097152,4194304,8388608,16777216,33554432,67108864,134217728,268435456,536870912,1073741824};
        String [] couleur= {"#cdc1b4","#eee4da","#ede0c8","#f2b179","#f59563","#f67c5f","#edcf72","#edcc61","#edc850","#edc53f","#edc22e","#FFBF00","#BE81F7","#9F81F7","#8181F7","#5858FA","#2E2EFE","#0000FF"};
                
        for(int i = 0; i < 18; i++){
            hashCouleur.put(valeur[i], couleur[i]);
        }
        for(int i = 18; i < valeur.length; i++){
            hashCouleur.put(valeur[i], "#212121");
        }

        //Mise en forme de la fenêtre

        JPanel contentPane = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));

        for (int i = 0; i < jeu.getSize(); i++) {
            for (int j = 0; j < jeu.getSize(); j++) {
                Border border = BorderFactory.createLineBorder(Color.darkGray, 5);
                tabC[i][j] = new JLabel();
                tabC[i][j].setFont(new Font("SansSerif", Font.BOLD, PIXEL_PER_SQUARE/4));
                tabC[i][j].setBorder(border);
                tabC[i][j].setOpaque(true);
                tabC[i][j].setHorizontalAlignment(SwingConstants.CENTER);


                contentPane.add(tabC[i][j]);

            }
        }
        getContentPane().add(contentPane, BorderLayout.CENTER);

        //JPanel contentPaneUp = new JPanel(new FlowLayout());
        JPanel contentPaneUp = new JPanel(new GridLayout(2, 1));
        contentPaneUp.setPreferredSize(new Dimension(500, 120));
        JPanel contentPaneDown = new JPanel(new GridLayout(3, 1));

        instruction = new JLabel();
        //instruction.setText("Join the numbers and get to the 2048 tile!");
        instruction.setText("Bienvenue au jeu 2048 !");
        instruction.setFont(new Font("SansSerif", Font.BOLD, PIXEL_PER_SQUARE/6));
        instruction.setHorizontalAlignment(SwingConstants.CENTER);
        contentPaneUp.add(instruction);

        JPanel contentPaneUpBottom = new JPanel(new FlowLayout());
        FlowLayout layoutUpBottom = (FlowLayout)contentPaneUpBottom.getLayout();
        layoutUpBottom.setVgap(0);

        nouveau = new JButton("Nouveau");
        nouveau.setBackground(Color.decode("#F75E3E"));
        nouveau.setForeground(Color.white);
        nouveau.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 jeu.init();
            }
        }); 
        nouveau.setFocusable(false);
        contentPaneUpBottom.add(nouveau);

        undo = new JButton("Retour");
        undo.setBackground(Color.decode("#F75E3E"));
        undo.setForeground(Color.white);
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 jeu.retourSiPossible();
            }
        }); 
        undo.setFocusable(false);
        contentPaneUpBottom.add(undo);

        points = new JLabel();
        points.setText("<html>SCORE<br/>0</html>");
        contentPaneUpBottom.add(points);

        meileurScore = new JLabel();
        meileurScore.setText("<html>BEST<br/>0</html>");
        contentPaneUpBottom.add(meileurScore);

        reinitBestScore = new JButton("Réinitialiser le meilleur score");
        reinitBestScore.setBackground(Color.white);
        reinitBestScore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 jeu.reinitBestScore();
            }
        });
        reinitBestScore.setFocusable(false);
        contentPaneUpBottom.add(reinitBestScore);

        contentPaneUp.add(contentPaneUpBottom);

        getContentPane().add(contentPaneUp, BorderLayout.NORTH);




        nbDeplacements = new JLabel();
        nbDeplacements.setText("Déplacements effectués : 0.");
        nbDeplacements.setHorizontalAlignment(SwingConstants.CENTER);
        contentPaneDown.add(nbDeplacements);

        JPanel contentPaneBottomDown = new JPanel(new FlowLayout());
        FlowLayout layoutBottomUp = (FlowLayout)contentPaneBottomDown.getLayout();
        layoutBottomUp.setVgap(0);

        AIFullGame = new JButton("Lancer/Arrêter l’I.A.");
        AIFullGame.setBackground(Color.decode("#FFE5AE"));
        //AIFullGame.setBackground(Color.lightGray);
        AIFullGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 jeu.IAplay();
            }
        }); 
        AIFullGame.setFocusable(false);
        contentPaneBottomDown.add(AIFullGame);

        AIMove = new JButton("Laisser l'IA faire un coup");
        AIMove.setBackground(Color.decode("#FFE5AE"));
        //AIMove.setBackground(Color.lightGray);
        AIMove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 jeu.IACoupSimple();
            }
        }); 
        AIMove.setFocusable(false);
        contentPaneBottomDown.add(AIMove);

        contentPaneDown.add(contentPaneBottomDown);


        JPanel contentPaneBottom2Down = new JPanel(new FlowLayout());
        EcrireSauv = new JButton("Sauvegarder");
        //EcrireSauv.setBackground(Color.decode("#B4FFBA"));
        //EcrireSauv.setBackground(Color.lightGray);
        EcrireSauv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 try {
                    jeu.EcrireDansFichier();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }); 
        EcrireSauv.setFocusable(false);
        contentPaneBottom2Down.add(EcrireSauv);

        LireSauv = new JButton("Charger");
        //LireSauv.setBackground(Color.decode("#B4FFBA"));
        //LireSauv.setBackground(Color.green);
        LireSauv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 try {
                    jeu.LireDepuisFichier();
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }); 
        LireSauv.setFocusable(false);
        contentPaneBottom2Down.add(LireSauv);



        contentPaneDown.add(contentPaneBottom2Down);


        getContentPane().add(contentPaneDown, BorderLayout.SOUTH);
        contentPaneDown.setPreferredSize(new Dimension(500, 110));

        //System.out.println(contentPaneDown.getPreferredSize());
        //System.out.println(contentPaneUp.getPreferredSize());
        //System.out.println(contentPane.getPreferredSize());
        setSize(jeu.getSize() * PIXEL_PER_SQUARE, jeu.getSize() * PIXEL_PER_SQUARE + contentPaneDown.getPreferredSize().height + contentPaneUp.getPreferredSize().height);
        setLocationRelativeTo(null);

        //setContentPane(contentPane);
        ajouterEcouteurClavier();
        rafraichir();

    }


    /**
     * Correspond à la fonctionnalité de Vue : affiche les données du modèle
     */
    private void rafraichir()  {

        SwingUtilities.invokeLater(new Runnable() { // demande au processus graphique de réaliser le traitement
            @Override
            public void run() {
                nbDeplacements.setText("Nombre de déplacements effectués : " + jeu.getCoup() + ".");
                points.setText("<html><br/>SCORE<br/>" + jeu.getScore() + "</html>");
                meileurScore.setText("<html><br/>BEST<br/>" + jeu.getBestScore() + "</html>");

                for (int i = 0; i < jeu.getSize(); i++) {
                    for (int j = 0; j < jeu.getSize(); j++) {
                        Case c = jeu.getCase(i, j);
                        int val;

                        if(jeu.getFin()){
                            tabC[i][j].setForeground(Color.WHITE);
                            tabC[i][j].setBackground(Color.decode("#2b2b2b"));
                            if (c == null) {
                                tabC[i][j].setText(""); 
                            }else{
                                val = c.getValeur();
                                tabC[i][j].setText(val + "");
                            }
                        }
                        else{
                            if (c == null) {
                                tabC[i][j].setText("");                            
                                tabC[i][j].setBackground(Color.decode(hashCouleur.get(0)));
    
                            } else {
                                val = c.getValeur();
    
                                tabC[i][j].setText(val + "");
                                if(val == 2 || val == 4){
                                    tabC[i][j].setForeground(Color.BLACK);
                                }
                                else{
                                    tabC[i][j].setForeground(Color.WHITE);
                                }
                                tabC[i][j].setBackground(Color.decode(hashCouleur.get(val)));
                            }

                        }
                        


                    }
                }
            }
        });


    }

    /**
     * Correspond à la fonctionnalité de Contrôleur : écoute les évènements, et déclenche des traitements sur le modèle
     */
    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    //Touche de jeu
                    case KeyEvent.VK_LEFT : jeu.jouerSiPossible(Direction.gauche); break;
                    case KeyEvent.VK_RIGHT : jeu.jouerSiPossible(Direction.droite); break;
                    case KeyEvent.VK_DOWN : jeu.jouerSiPossible(Direction.bas); break;
                    case KeyEvent.VK_UP : jeu.jouerSiPossible(Direction.haut); break;
                    case KeyEvent.VK_R : jeu.retourSiPossible(); break;

                    //Touche d'IA
                    case KeyEvent.VK_I : jeu.IAplay(); break;
                    case KeyEvent.VK_H : jeu.IACoupSimple(); break;

                    //Touche de sauvegarde
                    case KeyEvent.VK_E : try {
                            jeu.EcrireDansFichier();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        } break; 
                    case KeyEvent.VK_L : try {
                            jeu.LireDepuisFichier();
                        } catch (NumberFormatException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            //e1.printStackTrace();
                            System.out.println("Erreur dans l'ouverture fichier...");
                        } break; 
                }
            }
        });
    }


    @Override
    public void update(Observable o, Object arg) {
        rafraichir();
    }
}