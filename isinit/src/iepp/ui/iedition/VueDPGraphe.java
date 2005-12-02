package iepp.ui.iedition;

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


import iepp.Application;
import iepp.application.CAjouterComposantDP;
import iepp.application.aedition.CAjouterComposantGraphe;
import iepp.application.aedition.aoutil.OCreerElement;
import iepp.application.aedition.aoutil.OLier2Elements;
import iepp.application.aedition.aoutil.OSelection;
import iepp.application.aedition.aoutil.Outil;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.FElement;
import iepp.ui.iedition.dessin.rendu.Figure;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.handle.Handle;
import iepp.ui.iedition.dessin.rendu.liens.FLien;
import iepp.ui.iedition.dessin.rendu.liens.FLienClassic;
import iepp.ui.iedition.dessin.vues.ComposantView;
import iepp.ui.iedition.dessin.vues.MDDiagramme;
import iepp.ui.iedition.dessin.vues.MDElement;
import iepp.ui.iedition.dessin.vues.MDLienClassic;
import iepp.ui.iedition.dessin.vues.ProduitView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

import util.Vecteur;

/**
 * Classe permettant d'afficher un diagramme d'assemblage de composant
 */
public class VueDPGraphe extends JGraph implements Observer, MouseListener, MouseMotionListener, Serializable, DropTargetListener
{

	/**
	* Outil courant.
	*/
	private Outil diagramTool;
	 
	/**
	* Mod�le du diagramme.
	*/
	private MDDiagramme modele;

	
	/**
	* Mod�le du diagramme JGraph.
	*/
	private GraphModel Gmodele = new DefaultGraphModel();

	
	/**
	* El�ments pr�sents sur le diagramme.
	*/
	private Vector elements;

	/**
	* Liens pr�sents sur le diagramme.
	*/
	private Vector liens;

	/**
	* Figures s�lectionn�s.
	*/
	private Vector selection;
	
	
	/**
	 * Dimension de la zone � afficher
	 * sert � indiquer la taille setPreferedSize() du diagramme
	 */
	private Dimension zone_affichage ; 
	
	/**
	 * Memorise la premiere cellule cliquee
	 */
	private MouseEvent firstMouseEvent;
	
	/**
	 * 
	 */
	private boolean boutonLierActif;
	
	/**
	 * Construire le diagramme � partir de la d�finition de processus et 
	 * d'un controleur
	 * @param defProc, donn�es � observer
	 */
	public VueDPGraphe (DefinitionProcessus defProc )
	{
		// la vue observe le mod�le
		defProc.addObserver (this) ;
		// le diagramme au d�part est vide
		this.setModele(new MDDiagramme());
		
		
		this.setModel(Gmodele);

		this.setOpaque(true);
		this.setLayout(null);
	
		// initialiser les listes d'�l�ments
		this.elements = new Vector();
		this.liens = new Vector();
		this.selection = new Vector();
		
		// par d�fault, on utilise l'outil de s�lection
		this.diagramTool = new OSelection(this);		
		
		// ajouter les controleurs
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.zone_affichage = this.getSize();
		this.setPreferredSize(this.zone_affichage);
		this.setAutoscrolls(true);
		
		// on met la couleur par d�faut au diagramme
		modele.setFillColor(new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_fond_diagrmme"))));
		
		//on peut aussi d�poser des objets dans l'arbre drop target
		 new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true);
		 
		 this.setFocusable(true);
	}

	/**
	 * M�thode appel�e quand l'objet du domaine observ� est modifi�
	 * et qu'il appelle la m�thode notifyObservers()
	 */
	public void update(Observable o, Object arg)
	{
		this.repaint();
	}

	//-------------------------------------------------------------------------
	//  Affichage
	//-------------------------------------------------------------------------
	/**
	* Repeind le diagramme.
	*/
	public void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		this.calculerDimension();

		//A regarder
		//this.updateAutoSize()
		
		/*
		// Couleur de remplissage
		g.setColor(getModele().getFillColor());
		g.fillRect(0,0,getWidth(),getHeight());

		// Dessine les liens
		for (int i = 0; i < this.liens.size(); i++)
		{
			((FLien) (this.liens.elementAt(i))).paintComponent(g);
		} 
		
		// Dessine les �l�ments
		for (int i = 0; i < this.elements.size(); i++)
		{
		  ((FElement) (this.elements.elementAt(i))).paintComponent(g);
		}
		

		// Dessine les poign�es (handles)
		for (int i = 0; i < this.selection.size(); i++)
		{
			((Figure) (this.selection.elementAt(i))).displayHandles(g);
		} 
		  
		 // Permet de dessiner la repr�sentation de l'outil (ex : cadre de s�lection)
		 if (this.diagramTool != null)
		 {
			 //this.diagramTool.draw(g);
		 } 
		*/
	}

	

	//-------------------------------------------------------------------------
	//                         Relations avec le mod�le
	//-------------------------------------------------------------------------
	
	/**
	* Retourne le mod�le repr�sent� par le diagramme.
	* @return le mod�le de dessin associ� au diagramme
	*/
	public MDDiagramme getModele()
	{
		return this.modele;
	}

	/**
	* Fixe le mod�le de dessin repr�sent� par le diagramme.
	* @param m, mod�le de dessin � afficher
	*/
	public void setModele(MDDiagramme m)
	{
		this.modele = m;
	}

	//---------------------------------------------------------------------
    //                       Gestion des figures
    //---------------------------------------------------------------------

   /**
	* Recherche parmi les figures s�lectionn�es, si on a click� sur un handle.
	* @param x, abscisse du click
	* @param y, ordonn�e du click
	* @return le handle sur lequel on a cliqu�, null su aucun handle n'est s�lectionn�
	*/
   public Handle chercherHandleFigure(int x, int y)
   {
		 int n = this.selection.size();
		 Vecteur v = new Vecteur(x,y);
		 Figure f;
		 Handle h;
		
		 for(int i = 0 ; i < n ; i++)
		 {
			 f = (Figure) this.selection.elementAt(i);
			 h = f.getHandle(v.x, v.y);
			 if(h != null)
			 {
				 if (h.getVisible() == true)
				 {
				 	return h; 
				 } 
			 }
		 }
	 	return null;
   }

   /**
 	* Recherche la figure sur laquelle on a click�
 	* @param x, abscisse du click
	* @param y, ordonn�e du click
	* @return la figure sur laquelle on a cliqu�, null sinon
	*/
   public Figure chercherFigure(int x, int y)
   {
	 	Vecteur v = new Vecteur(x,y);
		Figure f;
	 	int n;

		 // Recherche parmi les �l�ments
		 n = this.elements.size();
		 for(int i = n - 1; i >= 0; i--)
		 {
			f = (Figure) this.elements.elementAt(i);
			if(f.contient(v))
			{
				return f;
			} 
		 }
	
		 // Recherche parmi les liens
		 n = this.liens.size();
		 for(int i = n - 1; i >= 0; i--) 
		 {
			f = (Figure) this.liens.elementAt(i);
			if(f.contient(v))
			{
				return f;
			} 
		 }
		 return null;
   }

   /**
   * Retourne tous les �l�ments du diagramme.
   */
   public Enumeration elements()
   {
	   return elements.elements();
   }

   /**
   * Retourne tous les liens du diagramme.
   */
   public Enumeration liens()
   {
	   return liens.elements();
   }
   
   public Vector getElements()
   {
   		return this.elements;
   }
   
   public Vector getLiens()
   {
   		return this.liens;
   }
   
   public void  setLiens(Vector l)
   {
   		this.liens = l;
   }
   
   public void  setElements(Vector l)
   {
   		this.elements = l;
   }
   
   
   
   /**
   * Ajoute une figure au diagramme (�l�ment ou lien).
   * @param f, figure � ajouter au diagramme
   */
   public void ajouterFigure(Figure f)
   {
   		 // selon le type de la figure
		 if (f instanceof FElement)
		 {
		  	this.elements.addElement(f);
		 } 
		 // lien
		 else if (f instanceof FLien)
		 {
		   	this.liens.addElement(f);
		   	((FLien) f).getSource().ajouterLien((FLien) f);
		   	((FLien) f).getDestination().ajouterLien((FLien) f);
		 }
		
		 // ajouter la figure au mod�le de dessin		
		 this.getModele().ajouterModeleFigure(f.getModele());
		 // mettre � jour l'affichage
		 this.repaint();
   }

   /**
   * Supprime un �l�ment du diagramme.
   * @param f, l'�l�ment � supprimer du diagramme
   */
   public void supprimerFigure(Figure f)
   {
   		// enlever l'�l�ment de toutes les listes disponibles
	   this.selection.removeElement(f);
	   this.elements.removeElement(f);
	   this.liens.removeElement(f);

	   // enlever la figure du mod�le de dessin
	   this.getModele().supprimerModeleFigure(f.getModele());
   }

	  
	  //---------------------------------------------------------------------
	  //                       Gestion de la s�lection
	  //---------------------------------------------------------------------
	
	  /**
	  * Retourne tous les �l�ments s�lectionn�s.
	  */
	  public Enumeration selectedElements()
	  {
		  return this.selection.elements();
	  }
	
	  /**
	  * Retourne le nombre d'�l�ments s�lectionn�s.
	  */
	  public int nbSelectedElements()
	  {
		  return this.selection.size();
	  }
	
	  /**
	  * S�lectionne une figure.
	  */
	  public void selectionneFigure(Figure figure)
	  {
		 if (!this.selection.contains(figure))
		 {
		   	this.selection.addElement(figure);
		   	figure.setSelectionne(true);
		 }
	  }
	
	  /**
	  * D�-s�lectionne une figure.
	  */
	  public void deSelectionneFigure(Figure figure)
	  {
		 this.selection.removeElement(figure);
		 figure.setSelectionne(false);
	  }
	
	  /**
	  * Inverse la s�lection d'une figure.
	  */
	  public void changeSelection(Figure figure)
	  {
		  if(this.selection.contains(figure))
		  {
			  this.deSelectionneFigure(figure);
		  }
		  else
		  {
			  this.selectionneFigure(figure);
		  }
	  }
	
	  /**
	  * Inverse la s�lection de toutes les figures comprises entre les deux points A (en haut � gauche)
	  * et B (en bas � droite).
	  */
	  public void changeSelectionFigures(Vecteur A, Vecteur B)
	  {
		  Enumeration e = this.elements.elements();
		  while(e.hasMoreElements())
		  {
			  Figure  figure = (Figure) e.nextElement();
			  if (figure.appartient(A, B))
			  {
			  		this.changeSelection(figure);
			  } 
		  }
	
		  e = this.liens.elements();
		  while(e.hasMoreElements())
		  {
			  Figure  figure = (Figure) e.nextElement();
			  if (figure.appartient(A, B))
			  {
			  		this.changeSelection(figure);
			  } 
		  }
	  }
	
	  /**
	  * D�s�lectionne toutes les figures de la s�lection.
	  */
	  public void clearSelection()
	  {
			Figure figure;
			while(selection.size() > 0)
			{
		  		figure = (Figure) this.selection.elementAt(0);
		  		this.deSelectionneFigure(figure);
			}
	  }
	  
	  /**
	   * Efface compl�tement le diagramme
	   */
	  public void effacerDiagramme()
	  {
	  	this.selection.removeAllElements();
	  	this.liens.removeAllElements();
	  	this.elements.removeAllElements();
	  	this.repaint();
   		
	  }
	
	  /**
	  * S�lectionne tous les �l�ments du diagramme.
	  */
	  public void selectionnerTout()
	  {
		  Enumeration e = this.elements.elements();
		  while(e.hasMoreElements())
		  {
			  Figure  figure = (Figure) e.nextElement();
			  this.selectionneFigure(figure);
		  }
	
		  e = this.liens.elements();
		  while(e.hasMoreElements())
		  {
			  Figure  figure = (Figure) e.nextElement();
			  this.selectionneFigure(figure);
		  }
		// mettre � jour l'affichage
		this.repaint();
	  }

		/**
		* Calculer la zone � afficher pour permettre le scrolling et l'encodage des images
		* rectangle de la zone � afficher
		*/
		public void calculerDimension()
		{
			// r�cup�rer les coordonn�es de la figure la plus en bas � droit possible
			int x = 0;
			int y = 0;
			
			// r�cup�rer les coordonn�es de la figure la plus en haut � gauche
			int x2 = this.getWidth();
			int y2 = this.getHeight();
			
			Enumeration e = this.elements.elements();
			while(e.hasMoreElements())
			{
				FElement  figure = (FElement) e.nextElement();
				MDElement vue = (MDElement)figure.getModele();
				if (vue.getX() + vue.getLargeur() > x )
				{
					x = vue.getX() + vue.getLargeur() ;
					if ( figure.getFinChaine() > x )
					{
						x = figure.getFinChaine();
					}
				}
				if (vue.getY() + vue.getHauteur() > y )
				{
					y = vue.getY() + vue.getHauteur() ;
				}
				if (vue.getX() < x2 )
				{
					x2 = vue.getX() ;
					if ( figure.getDebutChaine() > 0 && figure.getDebutChaine() < x2 )
					{
						x2 = figure.getDebutChaine();
					}
				}
				if (vue.getY() < y2 )
				{
					y2 = vue.getY();
				}
			}
			
			this.zone_affichage.height = y + 45 ;
			this.zone_affichage.width = x + 50 ;
			this.setPreferredSize(this.zone_affichage);
			this.revalidate();
		}

		public Dimension getZoneAffichage()
		{
			return this.zone_affichage ;
		}

	  //-------------------------------------------------------------------------
	  //                           Gestion des outils
	  //-------------------------------------------------------------------------

	  /**
	  * Renvoie l'outil courant.
	  */
	  public Outil getOutil()
	  {
		  return this.diagramTool;
	  }

	  /**
	  * Fixe l'outil courant.
	  */
	  public void setOutil(Outil o)
	  {
		  this.diagramTool = o;
	  }
	  
	  /**
	  * Fixe l'outil courant en tant que OSelection.
	  */
	  public void setOutilSelection()
	  {
		  boutonLierActif = false;
		  
		  this.setOutil(new OSelection(this));

          this.setMoveable(true);

          /*			
		  this.setSelectionCells(new Object[]{});

		  for(int i=0;i<this.getModel().getRootCount();i++){
				if(this.getModel().getRootAt(i) instanceof IeppCell){
					IeppCell cell = (IeppCell)this.getModel().getRootAt(i);
					System.out.println((IeppCell)this.getModel().getRootAt(i));
					
					GraphConstants.setMoveable(cell.getAttributes(),true);
				}
			}
				
			// pour la prise en compte dans le graph
			 * 
			 */
		   this.update(this.getGraphics());
		  }

	  /**
	   * Fixe l'outil courant en tant que OLier2Element.
	   */
	   public void setOutilLier()
	   {
		   boutonLierActif = true;
		   
		   this.setOutil(new OLier2Elements(this, Color.BLACK, new FLienClassic(new MDLienClassic())));

		   this.setMoveable(false);
/*

		   System.out.println(this.getModel().getRootCount());
		   
		   
		   this.setSelectionCells(new Object[]{});
				   
		   for(int i=0;i<this.getModel().getRootCount();i++){
				if(this.getModel().getRootAt(i) instanceof IeppCell){
					IeppCell cell = (IeppCell)this.getModel().getRootAt(i);
					  System.out.println("Lier:"+(IeppCell)this.getModel().getRootAt(i));
					 
					GraphConstants.setMoveable(cell.getAttributes(),false);
				}
			}
			
			// pour la prise en compte dans le graph
			 */
		   this.update(this.getGraphics());
		}
	   
	   /**
	    * Fixe l'outil courant en tant que OCreerElement
	    */
	   public void setOutilCreerElement(FElement e)
	   {
	        this.setOutil(new OCreerElement(this, new Color(153, 0, 51), e));
	   }
	  
	  
	//---------------------------------------------------------------------
	//    Gestion des actions sur le diagramme
	//---------------------------------------------------------------------

	public void mouseClicked(MouseEvent e)
	{
		//this.diagramTool.mouseClicked(e);
	}

	public void mousePressed( MouseEvent e )
	{
		System.out.println("bool:"+boutonLierActif);
		IeppCell ic = (IeppCell)this.getFirstCellForLocation(e.getX(),e.getY());
		if(boutonLierActif == true){
			GraphConstants.setMoveable(ic.getAttributes(), false);
			this.repaint();
		
			if ( this.getFirstCellForLocation(e.getX(),e.getY()) != null) {
	        	firstMouseEvent = e;
				
			}else{
				
				firstMouseEvent = null;
			}
	 	}
		else
		{
			GraphConstants.setMoveable(ic.getAttributes(), true);
			this.repaint();
			firstMouseEvent = null;
		}
	}

	public void mouseReleased( MouseEvent e ) 
	{
		//this.diagramTool.mouseReleased(e);
        System.out.println("mouseReleased");
        
        if(boutonLierActif == true){
        	
        	// verifier ke la ou l'on a relacher la souris , il y a un produit
        	
        	if (firstMouseEvent != null){
        		
        		Object cellSrc = this.getFirstCellForLocation(firstMouseEvent.getX(),firstMouseEvent.getY());
    			Object cellDes = this.getFirstCellForLocation(e.getX(),e.getY());
		
    			if (((cellSrc instanceof ProduitCellEntree) && (cellDes instanceof ProduitCellSortie)) || (cellSrc instanceof ProduitCellSortie) && (cellDes instanceof ProduitCellEntree) ) {
    				// verif ke les 2 soit un produit de type differents
	    			
    				
    				System.out.println("SOURCE :"+cellSrc);
    				System.out.println("DESTINATION :"+cellDes);
	    				
    				DefaultEdge edge = new DefaultEdge();
    			    
   			        DefaultPort portS = ((IeppCell)cellSrc).getPortComp();
   			        DefaultPort portD = ((IeppCell)cellDes).getPortComp();

    		        ConnectionSet cs = new ConnectionSet(edge, portS, portD);
    		     
   			        Vector vecObj = new Vector();
   			        vecObj.add(edge);
   			        
   			        this.getModel().insert(vecObj.toArray(), null, cs, null, null);
   			        
           	}
    			else {
	    				System.out.println("SOURCE & DESTINATION identiques");
    			}

        	}
        }
        firstMouseEvent = null;
	}

	public void mouseEntered( MouseEvent e ) 
	{
		this.repaint();
		//this.diagramTool.mouseEntered(e);
	}

	public void mouseExited( MouseEvent e ) 
	{
		//this.diagramTool.mouseExited(e);
	}

	public void mouseDragged( MouseEvent e ) 
	{
		//this.diagramTool.mouseDragged(e);
	}

	public void mouseMoved( MouseEvent e ) 
	{
		//this.diagramTool.mouseMoved(e);
	}


	//---------------------------------------------------------------------
	//    gestion du drop sur le diagramme
	//---------------------------------------------------------------------
	

	public void dragEnter(DropTargetDragEvent arg0){}
	public void dragOver(DropTargetDragEvent arg0) {}
	public void dropActionChanged(DropTargetDragEvent arg0) {}
	public void dragExit(DropTargetEvent arg0) {}

	/**
	 * R�cup�re l'�l�ment qui a �t� gliss� d�pos� sur le diagramme
	 * cet �l�ment est obligatoirement un idcomposant
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent dtde)
	{
		
		// r�cup�rer l'objet d�plac�
		Transferable transferable = dtde.getTransferable();
		DataFlavor[] flavors = transferable.getTransferDataFlavors();
		
		String nomComp = new String(); 
		
		// r�cup�rer l'endroit o� l'utilisateur � d�placer l'objet
		Point p = dtde.getLocation();
		
		try
		{
			// R�cup�rer l'objet transferr� par glissement
			Object obj = transferable.getTransferData(flavors[0]) ; 
			IdObjetModele id = null;	// composant � afficher
			// Si c'est un Long, il s'agit en fait de l'id du composant dans le r�f�rentiel
			// => Charger le composant
			if (obj instanceof Long)
			{
				// R�cup�rer l'id
				long idComp = ((Long)obj).longValue() ;
				// Ajouter le composant � la DP
				CAjouterComposantDP commande = new CAjouterComposantDP (idComp) ;
				if (commande.executer())
				{
					Application.getApplication().getProjet().setModified(true);
				}
				// R�cup�rer sa r�f�rence et remplir l'Id
				Referentiel ref = Application.getApplication().getReferentiel() ;
				ComposantProcessus comp = (ComposantProcessus) ref.chercherReference (idComp) ;
				if ( comp != null )
				{
					id = comp.getIdComposant() ;
				}
				else
				{
					dtde.dropComplete(false);
					return;
				}
			}
			// Sinon c'est un vrai id, � r�cup�rer
			else
			{
				id = (IdObjetModele) obj ;
			}
			dtde.dropComplete(true);
			
			CAjouterComposantGraphe c = new CAjouterComposantGraphe(id, p);
			if (c.executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}
			
			
			nomComp = ((ComposantProcessus)id.getRef()).getNomComposant();
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		dtde.dropComplete(false);	
	}

	/**
	 * @param IdObjetModele de l'objet
	 * @return FElement si l'objet est present dans le graphe, null sinon
	 */
	public FElement contient(IdObjetModele id)
	{
		FElement courant;
		
		for (int i = 0; i<this.elements.size(); i++)
		{
			courant = (FElement)this.elements.elementAt(i);
			if (courant.getModele().getId() != null)
			{
				if (courant.getModele().getId().equals(id))
				{
					return courant;
				}
			}
		}
		return null;
	}

	/** 
	 * @see JGraph#createVertexView(java.lang.Object, org.jgraph.graph.CellMapper)
	 */
	protected VertexView createVertexView(Object v, CellMapper cm) {
		
		// Return the appropriate view
		if(v instanceof ComposantCell)
		{
			return new ComposantView(v, this, cm);
		}
		else if(v instanceof ProduitCell)
		{
		    return new ProduitView(v, this, cm);
		}
		else
		{
		    return new VertexView(v, this, cm);
		}
		
	}
}
