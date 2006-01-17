package iepp.application.aedition.aoutil;



import iepp.Application;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDNote;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;

import org.jgraph.graph.GraphConstants;

import com.sun.rsasign.d;

import util.Vecteur;

/**
* Cet outil permet de cr�er un �l�ment quelconque sur un diagramme.
*/
public class OCreerElement extends Outil {

    static final int MOVE_STATE = 1;

    /**
    * L'�l�ment qui va �tre ajout�.
    */
    private FElement element;

    /**
    * Mod�le de l'�l�ment qui va �tre ajout�.
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
    * Cr�e une �dition "AjouterElement".
    */
    public void creerAjouterElement( MouseEvent event ) {
        // On d�termine l'endroit o� placer le nouvel �l�ment, qui peut �tre soit
        // directement sur le diagramme, soit par-dessus un �l�ment, soit par-dessus un lien.
        Vecteur translation = getCurrent();

        // On emp�che l'utilisateur de cr�er un �l�ment qui sort
        // des limites du diagramme.
        if (translation.y + modele.getHauteur() >= diagramme.getHeight())
            translation.y = diagramme.getHeight() - modele.getHauteur();
        if (translation.x + modele.getLargeur() >= diagramme.getWidth())
            translation.x = diagramme.getWidth() - modele.getLargeur();

		Map NoteAttribute = GraphConstants.createMap();
		TextCell note = diagramme.getNote();
		note.setAbscisse(event.getX());
		note.setOrdonnee(event.getY());
		NoteAttribute.put(note, note.getAttributs());

		diagramme.getModel().insert(new Object[] { note }, NoteAttribute, null, null, null);

        // On dispose l'�l�ment � l'endroit du click
        element.translate(translation);
        diagramme.clearSelection();
        //ajouterEditionDiagramme(new AjouterElement(diagramme, element));
        this.diagramme.ajouterFigure(element);
		// reprendre l'outil de s�l�ction
        Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
		// on avertit qu'il y a eu modification
        Application.getApplication().getProjet().setModified(true);
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
    Un click ajoute l'�l�ment � l'endroit du click.
    */
    public void mouseReleased( MouseEvent event ) {
        super.mouseReleased(event);
    	creerAjouterElement( event );
        terminer();
    }
}
