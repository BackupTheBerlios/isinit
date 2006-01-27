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
* Cet outil permet de cr�er un �l�ment quelconque sur un diagramme.
*/
public class OCreerElement extends Outil {

    static final int MOVE_STATE = 1;

       
    public OCreerElement(VueDPGraphe vue)
	{
        super(vue);
    }

    /**
    * Cr�e une �dition "AjouterElement".
    */
    public void creerAjouterElement( MouseEvent event ) {
        // On d�termine l'endroit o� placer le nouvel �l�ment, qui peut �tre soit
        // directement sur le diagramme, soit par-dessus un �l�ment, soit par-dessus un lien.
        Vecteur translation = getCurrent();

        Map NoteAttribute = GraphConstants.createMap();
		TextCell note = new TextCell(event.getX(),event.getY());
		NoteAttribute.put(note, note.getAttributs());

        // On emp�che l'utilisateur de cr�er un �l�ment qui sort
        // des limites du diagramme.
        if (translation.y + note.getHauteur() >= diagramme.getHeight())
            translation.y = diagramme.getHeight() - note.getHauteur();
        if (translation.x + note.getLargeur() >= diagramme.getWidth())
            translation.x = diagramme.getWidth() - note.getLargeur();

		diagramme.getModel().insert(new Object[] { note }, NoteAttribute, null, null, null);

        diagramme.clearSelection();
        this.diagramme.ajouterCell(note);
		
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
