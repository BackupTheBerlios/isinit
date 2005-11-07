package iepp.application.aedition.aoutil;



import iepp.Application;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.vues.MDElement;

import java.awt.*;
import java.awt.event.*;

import util.Vecteur;

/**
* Cet outil permet de créer un élément quelconque sur un diagramme.
*/
public class OCreerElement extends Outil {

    static final int MOVE_STATE = 1;

    /**
    * L'élément qui va être ajouté.
    */
    private FElement element;

    /**
    * Modèle de l'élément qui va être ajouté.
    */
    private MDElement modele;
    
    private Color lineColor;

    
    public OCreerElement(VueDPGraphe vue, Color linecolor, FElement e)
	{
        super(vue);
        this.element = e;
        this.lineColor = linecolor;
        this.modele = (MDElement) e.getModele();
    }

    /**
    * Crée une édition "AjouterElement".
    */
    public void creerAjouterElement() {
        // On détermine l'endroit où placer le nouvel élément, qui peut être soit
        // directement sur le diagramme, soit par-dessus un élément, soit par-dessus un lien.
        Vecteur translation = getCurrent();

        // On empêche l'utilisateur de créer un élément qui sort
        // des limites du diagramme.
        if (translation.y + modele.getHauteur() >= diagramme.getHeight())
            translation.y = diagramme.getHeight() - modele.getHauteur();
        if (translation.x + modele.getLargeur() >= diagramme.getWidth())
            translation.x = diagramme.getWidth() - modele.getLargeur();

        // On dispose l'élément à l'endroit du click
        element.translate(translation);
        diagramme.clearSelection();
        //ajouterEditionDiagramme(new AjouterElement(diagramme, element));
        this.diagramme.ajouterFigure(element);
        Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
        Application.getApplication().getProjet().setModified(true);
    }

    /**
    * Dessine l'ombre de l'élément qui va être créé.
    */
    public void draw( Graphics g ) {
      if (state == MOVE_STATE) {
        element.drawElementShadow(g, lineColor, getCurrent().x, getCurrent().y, 0, 0);
      }
    }

    public void mouseEntered( MouseEvent e ) {
        if (e.getSource() == diagramme) {
          state = MOVE_STATE;
          update();
        }
    }

    public void mouseExited( MouseEvent e ) {
        if (e.getSource() == diagramme) {
          state = IDLE_STATE;
          update();
        }
    }

    public void mouseMoved( MouseEvent event ) {
        super.mouseMoved(event);
        if (event.getSource() == diagramme) {
          update();
        }
    }

    public void mouseDragged( MouseEvent event ) {
      super.mouseDragged(event);
      update();
    }

    /**
    Un click ajoute l'élément à l'endroit du click.
    */
    public void mouseReleased( MouseEvent event ) {
        creerAjouterElement();
        terminer();
    }
}
