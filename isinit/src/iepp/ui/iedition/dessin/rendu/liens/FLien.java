/*
 * IEPP: Isi Engineering Process Publisher
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package iepp.ui.iedition.dessin.rendu.liens;

import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.Figure;
import iepp.ui.iedition.dessin.rendu.handle.liens.HandleLien;
import iepp.ui.iedition.dessin.vues.MDFleche;
import iepp.ui.iedition.dessin.vues.MDLien;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import util.Vecteur;

public abstract class FLien extends Figure {

  public final static int TAILLE_FLECHE = 10;

  final static BasicStroke classic = new BasicStroke(1.0f,BasicStroke.CAP_ROUND , BasicStroke.JOIN_ROUND);
  final static float dot1[] = {3.0f};
  final static BasicStroke dotted = new BasicStroke(1.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4.0f, dot1, 0.0f);
  final static float dash1[] = {10.0f};
  final static BasicStroke dashed = new BasicStroke(1.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4.0f, dash1, 0.0f);

  FElement source, destination;

  public FLien(MDLien m) {
    super(m);
  }

  public void initHandles() {
    MDLien m = (MDLien) getModele();
    Vecteur p;
    Vector pointsAncrage =  m.getPointsAncrage();

    p = (Vecteur) pointsAncrage.firstElement();
    ajouterHandle(new HandleLien(this, true, p));

    int i;
    for (i = 1; i < pointsAncrage.size() - 1; i++) {
      p = (Vecteur) pointsAncrage.elementAt(i);
      ajouterHandle(new HandleLien(this, false, p));
    }

    p = (Vecteur) pointsAncrage.lastElement();
    ajouterHandle(new HandleLien(this, true, p));
  }

  public void doOnRightClick(VueDPGraphe parent, int x, int y)
   {
   	/*
    PopupFLien p = new PopupFLien(this, x, y);
    p.show(parent, x, y);
    */
  }

  /**
  Retourne la fenêtre de propriétés associée au modèle.
 
  public FenetreProprietes getFenetreProprietes() {
    return new FPDLien((MDLien) getModele());
  }
 */
  
  public void setSource(FElement fe) {
    this.source = fe;
  }
  public FElement getSource() {
    return this.source;
  }

  public void setDestination(FElement fe) {
    this.destination = fe;
  }
  public FElement getDestination() {
    return this.destination;
  }

  public boolean contient(Vecteur v) {
    return (segmentFor(v) >= 0);
  }

  public boolean appartient(Vecteur A, Vecteur B) {
    return getPath(0, 0).intersects(A.x, A.y, B.x - A.x, B.y - A.y);
  }

  /**
  * Crée un point d'ancrage sur la ligne
  */
  public void creerPointAncrage(Vecteur p) {
    int segment = segmentFor(p);

    if (segment >= 0)
      creerPointAncrage(p, segment + 1);
  }
  public void creerPointAncrage(Vecteur p, int index) {
    MDLien m = (MDLien) getModele();

    m.ajouterPointAncrage(p, index);
    ajouterHandle(new HandleLien(this, false, p));
  }

  /**
  * Supprime un point d'ancrage de la ligne
  */
  public void detruirePointAncrage(Vecteur p) {
    MDLien m = (MDLien) getModele();

    m.supprimerPointAncrage(p);

    handles.remove(getHandle(p));
  }

  public int getIndexPointAncrage(Vecteur p) {
    MDLien m = (MDLien) getModele();

    return m.getPointsAncrage().indexOf(p);
  }

  public HandleLien getHandle(Vecteur p) {
    for (int i = 0; i < handles.size(); i++) {
      HandleLien h = (HandleLien) handles.elementAt(i);

      if (h.getPointAncrage() == p)
        return h;
    }

    return null;
  }

 /**
  * Renvoie le segment le plus près du point a
  */
  protected int segmentFor(Vecteur a) {
    int tolerance = 3;
    MDLien m = (MDLien) getModele();
    Vector pointsAncrage = m.getPointsAncrage();

    Vecteur p = (Vecteur) pointsAncrage.elementAt(0);
    int x1 = p.x, y1 = p.y;
    int x2, y2;

    int s = -1;
    for(int i = 1; i < pointsAncrage.size(); i++) {
      p = (Vecteur) pointsAncrage.elementAt(i);
      x2 = p.x;
      y2 = p.y;

      // Get the smallest distance to this point.
      int dist;
      if((dist = (int) Line2D.ptSegDist((double) x1,(double) y1,(double) x2,(double) y2,(double) a.x,(double) a.y)) < tolerance) {
        tolerance = dist;
        s = (i-1);
      }
      x1 = x2;
      y1 = y2;
    }

    return s;
  }

  public void paintComponent(Graphics g) {
    MDLien m = (MDLien) getModele();
    GeneralPath path = getPath(0, 0);

    drawLien(g, m.getCouleur(), path);

    drawFleches((Graphics2D)g, m.getCouleur(), path);
  }

  /**
  * Affiche l'ombre de la figure.
  * Appelée uniquement pour les déplacements/resize de figures avec la souris.
  * @param g, contexte graphique 2D
  */
  public void drawLinkShadow(Graphics g, Color shadowLineColor, GeneralPath path) {
    drawLien(g, shadowLineColor, path);
  }

  protected void drawLien(Graphics g, Color lineColor, GeneralPath path) {
    Graphics2D g2d = (Graphics2D) g;
    Stroke stroke = g2d.getStroke();

    g2d.setColor(lineColor);

    if (((MDLien) getModele()).getStyle() == MDLien.STYLE_CLASSIC)
      g2d.setStroke(classic);
    else if (((MDLien) getModele()).getStyle() == MDLien.STYLE_DOTTED)
      g2d.setStroke(dotted);
    else if (((MDLien) getModele()).getStyle() == MDLien.STYLE_DASHED)
      g2d.setStroke(dashed);

    // Dessine le corps du lien (lignes entre les points d'ancrage)
    g2d.draw(path);

    g2d.setStroke(stroke);
  }

  protected void drawFleches(Graphics2D g2d, Color lineColor, GeneralPath path) {
    MDFleche mf = ((MDLien) getModele()).getFlecheDestination();

    if (mf != null) {
    // Dessine la flèche
    PathIterator it = path.getPathIterator(new AffineTransform());
    GeneralPath pathFleche = new GeneralPath();
    double coords[] = new double[6];
    int x1=0, y1=0, x2=0, y2=0;

    // Parcourt le path pour avoir l'avant dernier point du lien dans (x2, y2)
    while (! it.isDone()) {
      x2 = (int) coords[0];
      y2 = (int) coords[1];
      it.currentSegment(coords);
      it.next();
    }

    // Coordonnées de la destination du lien dans (x1, y1)
    x1 = (int) coords[0];
    y1 = (int) coords[1];

    // Calcule l'angle pour la flèche
    AffineTransform at = new AffineTransform();
    at.setToRotation(getAngle(x2, y2, x1, y1) + Math.PI, x1, y1);
      if (mf.getStyle() == MDFleche.STYLE_CLASSIC)
    pathFleche = ArrowHead.createArrowHead(TAILLE_FLECHE, ArrowHead.FLAT, x1, y1, null);
    pathFleche.transform(at);
    // Dessine la flèche
    g2d.fill(pathFleche);
    }
  }

    /**
   * Calcule l'angle entre 2 points.
   * @return double angle in radians
   */
  final private double getAngle(double x1, double y1, double x2, double y2) {
    double angle;

    if((x2 - x1) == 0) {
      angle = Math.PI/2;
      return (y2 < y1) ? (angle + Math.PI) : angle;
    }

    angle = Math.atan((y1-y2)/(x1-x2));
    return (x2 < x1) ? (angle + Math.PI) : angle;
  }

  public GeneralPath getPath(int decalageX, int decalageY) {
    MDLien m = (MDLien) getModele();
    Vector pointsAncrage =  m.getPointsAncrage();
    Vecteur p;

    m.rafraichirExtremites();

    GeneralPath path = new GeneralPath();

    // Source
    p = (Vecteur) pointsAncrage.elementAt(0);
    path.moveTo(p.x + decalageX, p.y + decalageY);

    // Points d'ancrage intermédiaires
    int i;
    for (i = 1; i < pointsAncrage.size(); i++) {
      p = (Vecteur) pointsAncrage.elementAt(i);
      path.lineTo(p.x + decalageX, p.y + decalageY);
    }

    return path;
  }

  public GeneralPath getNewPath(Vecteur ptAncrage,int ptAncrageX, int ptAncrageY) {
    MDLien m = (MDLien) getModele();
    Vector pointsAncrage =  m.getPointsAncrage();
    Vecteur p;

    m.rafraichirExtremites();

    GeneralPath path = new GeneralPath();

    // Source
    path.moveTo(m.getSource().getX() + m.getSource().getLargeur()/2,
                m.getSource().getY() + m.getSource().getHauteur()/2);

    // Points d'ancrage intermédiaires
    int i;
    for (i = 1; i < pointsAncrage.size() - 1; i++) {
      p = (Vecteur) pointsAncrage.elementAt(i);
      if (p == ptAncrage)
        path.lineTo(ptAncrageX, ptAncrageY);
      else
        path.lineTo(p.x, p.y);
    }

    // Destination
    path.lineTo(m.getDestination().getX() + m.getDestination().getLargeur()/2,
                m.getDestination().getY() + m.getDestination().getHauteur()/2);

    return path;
  }

  public void translate(Vecteur v) {
    Vector pointsAncrage = ((MDLien) getModele()).getPointsAncrage();
    Enumeration e = pointsAncrage.elements();

    while (e.hasMoreElements()) {
      Vecteur p = (Vecteur) e.nextElement();
      p.add(v);
    }
  }
}
