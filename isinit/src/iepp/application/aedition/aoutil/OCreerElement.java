package iepp.application.aedition.aoutil;



import iepp.Application;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.TextCell;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Map;

import org.jgraph.graph.GraphConstants;

import util.Vecteur;

/**
* Cet outil permet de créer un élément quelconque sur un diagramme.
*/
public class OCreerElement extends Outil {

    static final int MOVE_STATE = 1;

       
    public OCreerElement(VueDPGraphe vue)
	{
        super(vue);
    }

    /**
    * Crée une édition "AjouterElement".
    */
    public void creerAjouterElement( MouseEvent event ) {
        // On détermine l'endroit où placer le nouvel élément, qui peut être soit
        // directement sur le diagramme, soit par-dessus un élément, soit par-dessus un lien.
        Vecteur translation = getCurrent();

        Map NoteAttribute = GraphConstants.createMap();
		TextCell note = new TextCell(event.getX(),event.getY());
		NoteAttribute.put(note, note.getAttributs());

        // On empêche l'utilisateur de créer un élément qui sort
        // des limites du diagramme.
        if (translation.y + note.getHauteur() >= diagramme.getHeight())
            translation.y = diagramme.getHeight() - note.getHauteur();
        if (translation.x + note.getLargeur() >= diagramme.getWidth())
            translation.x = diagramme.getWidth() - note.getLargeur();

		diagramme.getModel().insert(new Object[] { note }, NoteAttribute, null, null, null);

        diagramme.clearSelection();
        this.diagramme.ajouterCell(note);
		
        // reprendre l'outil de séléction
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
    Un click ajoute l'élément à l'endroit du click.
    */
    public void mouseReleased( MouseEvent event ) {
        super.mouseReleased(event);
    	creerAjouterElement( event );
        terminer();
    }
}
