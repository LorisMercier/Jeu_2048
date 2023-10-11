package modele;

public class Case {
    private int valeur;
    private Jeu jeu;
    private boolean fusion;


    public Case(int _valeur, Jeu _jeu) {
        valeur = _valeur;
        this.jeu = _jeu;
        fusion = false;
    }

    public int getValeur() {
        return valeur;
    }

    public void setFusion(boolean _fusion){
        fusion = _fusion;
    }

    public boolean deplacer(Direction d){
        Case c;
        boolean deplacement = false;
        c = jeu.getVoisin(this,d);
        while(c == null){
            jeu.deplacementUnitaireCase(this, d);
            c = jeu.getVoisin(this,d);
            deplacement = true;
        }
        if(c.valeur == this.valeur && !c.fusion){
            c.valeur *= 2;
            c.fusion = true;
            jeu.removeCase(this);

            deplacement = true;
            jeu.incrementeScore(c.valeur);
        }
        return deplacement;
    }

}
