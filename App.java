


//ATTENTION, vous devez faire entrer le pathName pour que le programme se compile!!!!!.
// normalement ça marche parfait!!!!.

package algoAvancée;

import algoAvancée.WeightedGraph.Edge;
import algoAvancée.WeightedGraph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashSet;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;


//Classe pour g�rer l'affichage
class Board extends JComponent 
{
	private static final long serialVersionUID = 1L;
	Graph graph;
	int pixelSize;
	int ncols;
	int nlines;
	HashMap<Integer, String> colors;
	int start;
	int end;
	double max_distance;
	int current;
	LinkedList<Integer> path;
	
    public Board(Graph graph, int pixelSize, int ncols, int nlines, HashMap<Integer, String> colors, int start, int end)
    {
        super();
        this.graph = graph;
        this.pixelSize = pixelSize;
        this.ncols = ncols;
        this.nlines = nlines;
        this.colors = colors;
        this.start = start;
        this.end = end;
        this.max_distance = ncols * nlines;
        this.current = -1;
        this.path = null;
    }
    
    //Mise � jour de l'affichage
	public void paint(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				        	RenderingHints.VALUE_ANTIALIAS_ON);
		//Ugly clear of the frame
		g2.setColor(Color.cyan);
		g2.fill(new Rectangle2D.Double(0,0,this.ncols*this.pixelSize, this.nlines*this.pixelSize));
		
		
		int num_case = 0;
		for (WeightedGraph.Vertex v : this.graph.vertexlist)
		{
			double type = v.indivTime;
			int i = num_case / this.ncols;
			int j = num_case % this.ncols;

			if (colors.get((int)type).equals("green"))
				g2.setPaint(Color.green);
			if (colors.get((int)type).equals("gray"))
				g2.setPaint(Color.gray);
			if (colors.get((int)type).equals("blue"))
				g2.setPaint(Color.blue);
			if (colors.get((int)type).equals("yellow"))
				g2.setPaint(Color.yellow);
			g2.fill(new Rectangle2D.Double(j*this.pixelSize, i*this.pixelSize, this.pixelSize, this.pixelSize));
			
			if (num_case == this.current)
			{
				g2.setPaint(Color.red);
				g2.draw(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 6, 6));
			}
			if (num_case == this.start)
			{
				g2.setPaint(Color.white);
				g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 4, 4));
				
			}
			if (num_case == this.end)
			{
				g2.setPaint(Color.black);
				g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 4, 4));
			}
			
			num_case += 1;
		}
		
		num_case = 0;
		for (WeightedGraph.Vertex v : this.graph.vertexlist)
		{
			int i = num_case / this.ncols;
			int j = num_case % this.ncols;
			if (v.timeFromSource < Double.POSITIVE_INFINITY)
			{
				float g_value = (float) (1 - v.timeFromSource / this.max_distance);
				if (g_value < 0)
					g_value = 0;
				g2.setPaint(new Color(g_value, g_value, g_value));
				g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 4, 4));
				WeightedGraph.Vertex previous = v.prev;
				if (previous != null)
				{
					int i2 = previous.num / this.ncols;
					int j2 = previous.num % this.ncols;
					g2.setPaint(Color.black);
					g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize/2, i * this.pixelSize + this.pixelSize/2, j2 * this.pixelSize + this.pixelSize/2, i2 * this.pixelSize + this.pixelSize/2));
				}
			}
				
			num_case += 1;
		}
		
		int prev = -1;
		if (this.path != null)
		{
			g2.setStroke(new BasicStroke(3.0f));
			for (int cur : this.path)
			{
				if (prev != -1)
				{
					g2.setPaint(Color.red);
					int i = prev / this.ncols;
					int j = prev % this.ncols;
					int i2 = cur / this.ncols;
					int j2 = cur % this.ncols;
					g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize/2, i * this.pixelSize + this.pixelSize/2, j2 * this.pixelSize + this.pixelSize/2, i2 * this.pixelSize + this.pixelSize/2));
				}
				prev = cur;
			}
		}
	}
	
	//Mise � jour du graphe (� appeler avant de mettre � jour l'affichage)
	public void update(Graph graph, int current)
	{
		this.graph = graph;
		this.current = current;
		repaint();
	}
	
	//Indiquer le chemin (pour affichage)
	public void addPath(Graph graph, LinkedList<Integer> path)
	{
		this.graph = graph;
		this.path = path;
		this.current = -1;
		repaint();
	}
}

//Classe principale. C'est ici que vous devez faire les modifications
public class App {
	
	//Initialise l'affichage
	private static void drawBoard(Board board, int nlines, int ncols, int pixelSize)
	{
	    JFrame window = new JFrame("Plus court chemin");
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    window.setBounds(0, 0, ncols*pixelSize+20, nlines*pixelSize+40);
	    window.getContentPane().add(board);
	    window.setVisible(true);
	}
	
	//M�thode A*
	//graph: le graphe repr�sentant la carte
	//start: un entier repr�sentant la case de d�part
	//       (entier unique correspondant � la case obtenue dans le sens de la lecture)
	//end: un entier repr�sentant la case d'arriv�e
	//       (entier unique correspondant � la case obtenue dans le sens de la lecture)
	//ncols: le nombre de colonnes dans la carte
	//numberV: le nombre de cases dans la carte
	//board: l'affichage
	//retourne une liste d'entiers correspondant au chemin.
	private static LinkedList<Integer> AStar(Graph graph, int start, int end, int ncols, int numberV, Board board)
	{
		graph.vertexlist.get(start).timeFromSource=0;
		int number_tries = 0;
		
		//TODO: mettre tous les noeuds du graphe dans la liste des noeuds � visiter:
		HashSet<Integer> to_visit = new HashSet<Integer>();
		
		//TODO: Remplir l'attribut graph.vertexlist.get(v).heuristic pour tous les noeuds v du graphe:
		 for (int i = 0; i < numberV; i++) {
		        to_visit.add(i);
		        graph.vertexlist.get(i).heuristic = calculateHeuristic(i, end, ncols); // À compléter
		    }
		
		while (to_visit.contains(end))
		{
			int min_v=findMinVertex(to_visit,graph);
			to_visit.remove(min_v);
			number_tries += 1;
			
			//TODO: pour tous ses voisins, on v�rifie si on est plus rapide en passant par ce noeud.
			for (int i = 0; i < graph.vertexlist.get(min_v).adjacencylist.size(); i++)
			{
				int to_try = graph.vertexlist.get(min_v).adjacencylist.get(i).destination;
				double newTime = graph.vertexlist.get(min_v).timeFromSource + graph.vertexlist.get(min_v).adjacencylist.get(i).weight;
			
			 if (newTime < graph.vertexlist.get(to_try).timeFromSource) {
	                graph.vertexlist.get(to_try).timeFromSource = newTime;
	                graph.vertexlist.get(to_try).prev = graph.vertexlist.get(min_v);
	            }
	        }

			try {
	    	    board.update(graph, min_v);
	    	    Thread.sleep(10);
	    	} catch(InterruptedException e) {
	    	    System.out.println("stop");
	    	}
	            
		}
		
		System.out.println("Done! Using A*:");
		System.out.println("	Number of nodes explored: " + number_tries);
		System.out.println("	Total time of the path: " + graph.vertexlist.get(end).timeFromSource);
		LinkedList<Integer> path=new LinkedList<Integer>();
		path.addFirst(end);
		//TODO: remplir la liste path avec le chemin
	    buildPath(graph, start, end, path); 
		board.addPath(graph, path);
		return path;
	}
    private static double calculateHeuristic(int current, int end, int ncols) {
      int currentRow = current / ncols;
      int currentCol = current % ncols;
      int endRow = end / ncols;
      int endCol = end % ncols;
    return Math.sqrt(Math.pow(endRow - currentRow, 2) + Math.pow(endCol - currentCol, 2));
}
    private static void buildPath(Graph graph, int start, int end, LinkedList<Integer> path) {
        int current = end;

        while (current != start) {
            if (graph.vertexlist.get(current).prev == null) {
                // Si le chemin n'est pas atteignable depuis le départ, sortir de la boucle
                System.out.println("Path not reachable.");
                break;
            }

            path.addFirst(current);
            current = graph.vertexlist.get(current).prev.num;
        }

        // Ajouter le sommet de départ à la fin du chemin
        path.addFirst(start);
    }

    private static int findMinVertex(HashSet<Integer> toVisit, Graph graph) {
        int minVertex = -1;
        double minDistance = Double.POSITIVE_INFINITY;

        for (int vertex : toVisit) {
            double distance = graph.vertexlist.get(vertex).timeFromSource;
            if (distance < minDistance) {
                minDistance = distance;
                minVertex = vertex;
            }
        }

        return minVertex;
    }
	private static LinkedList<Integer> Dijkstra(Graph graph, int start, int end, int numberV, Board board)
	{
		graph.vertexlist.get(start).timeFromSource=0;
		int number_tries = 0;
		
		//TODO: mettre tous les noeuds du graphe dans la liste des noeuds � visiter:
		HashSet<Integer> to_visit = new HashSet<Integer>();
		 for (int i = 0; i < numberV; i++) {
		        to_visit.add(i);
		    }
		 while (to_visit.contains(end))
		 {
			 int min_v = findMinVertex(to_visit, graph);
			to_visit.remove(min_v);
			number_tries += 1;
			
			//TODO: pour tous ses voisins, on v�rifie si on est plus rapide en passant par ce noeud.
			for (int i = 0; i < graph.vertexlist.get(min_v).adjacencylist.size(); i++)
			{
				int to_try = graph.vertexlist.get(min_v).adjacencylist.get(i).destination;
				double newTime = graph.vertexlist.get(min_v).timeFromSource + graph.vertexlist.get(min_v).adjacencylist.get(i).weight;
			
			if (newTime < graph.vertexlist.get(to_try).timeFromSource) {
                graph.vertexlist.get(to_try).timeFromSource = newTime;
                graph.vertexlist.get(to_try).prev = graph.vertexlist.get(min_v);
            }
        }
			
			
			try {
	    	    board.update(graph, min_v);
	    	    Thread.sleep(10);
	    	} catch(InterruptedException e) {
	    	    System.out.println("stop");
	    	}
	            
		}
		
		System.out.println("Done! Using Dijkstra:");
		System.out.println("	Number of nodes explored: " + number_tries);
		System.out.println("	Total time of the path: " + graph.vertexlist.get(end).timeFromSource);
		LinkedList<Integer> path=new LinkedList<Integer>();
		path.addFirst(end);
		buildPath(graph, start, end, path); // À compléter
		board.addPath(graph, path);
		return path;
	}
	
	//M�thode principale
	public static void main(String[] args) {
	    //Lecture de la carte et création du graphe
	    try {
	        // TODO: obtenir le fichier qui décrit la carte

		// j'ai enlevé mon pathName, pour compiler le programme faudra entrer le pathName du fichier Graph.txt.	
	        File myObj = new File("");
			
	        Scanner myReader = new Scanner(myObj);
	        String data = "";
	        // On ignore les deux premières lignes
	        for (int i = 0; i < 3; i++)
	            data = myReader.nextLine();

	        // Lecture du nombre de lignes
	        int nlines = Integer.parseInt(data.split("=")[1]);
	        // Et du nombre de colonnes
	        data = myReader.nextLine();
	        int ncols = Integer.parseInt(data.split("=")[1]);

	        // Initialisation du graphe
	        Graph graph = new Graph();

	        HashMap<String, Integer> groundTypes = new HashMap<String, Integer>();
	        HashMap<Integer, String> groundColor = new HashMap<Integer, String>();
	        data = myReader.nextLine();
	        data = myReader.nextLine();
	        // Lire les différents types de cases
	        while (!data.equals("==Graph==")) {
	            String name = data.split("=")[0];
	            int time = Integer.parseInt(data.split("=")[1]);
	            data = myReader.nextLine();
	            String color = data;
	            groundTypes.put(name, time);
	            groundColor.put(time, color);
	            data = myReader.nextLine();
	        }

	        // On ajoute les sommets dans le graphe (avec le bon type)
	        for (int line = 0; line < nlines; line++) {
	            data = myReader.nextLine();
	            for (int col = 0; col < ncols; col++) {
	                graph.addVertex(groundTypes.get(String.valueOf(data.charAt(col))));
	            }
	        }

	        // TODO: ajouter les arêtes
	        for (int line = 0; line < nlines; line++) {
	            for (int col = 0; col < ncols; col++) {
	                int source = line * ncols + col;
	                int dest;
	                double weight;

	                // On donne la première arête
	                    if (line > 0 && col > 0) {
	                        dest = (line - 1) * ncols + col - 1;
	                        weight = Math.abs(graph.vertexlist.get(source).indivTime - graph.vertexlist.get(dest).indivTime);
	                        graph.addEgde(source, dest, weight);
	                    }
	                 // Connecter avec la case en haut
	                    if (line > 0) {
	                        dest = (line - 1) * ncols + col;
	                        weight = Math.abs(graph.vertexlist.get(source).indivTime - graph.vertexlist.get(dest).indivTime);
	                        graph.addEgde(source, dest, weight);
	                    }

	                    // Connecter avec la case en haut à droite
	                    if (line > 0 && col < ncols - 1) {
	                        dest = (line - 1) * ncols + col + 1;
	                        weight = Math.abs(graph.vertexlist.get(source).indivTime - graph.vertexlist.get(dest).indivTime);
	                        graph.addEgde(source, dest, weight);
	                    }

	                    // Connecter avec la case à gauche
	                    if (col > 0) {
	                        dest = line * ncols + col - 1;
	                        weight = Math.abs(graph.vertexlist.get(source).indivTime - graph.vertexlist.get(dest).indivTime);
	                        graph.addEgde(source, dest, weight);
	                    }

	                    // Connecter avec la case à droite
	                    if (col < ncols - 1) {
	                        dest = line * ncols + col + 1;
	                        weight = Math.abs(graph.vertexlist.get(source).indivTime - graph.vertexlist.get(dest).indivTime);
	                        graph.addEgde(source, dest, weight);
	                    }

	                    // Connecter avec la case en bas à gauche
	                    if (line < nlines - 1 && col > 0) {
	                        dest = (line + 1) * ncols + col - 1;
	                        weight = Math.abs(graph.vertexlist.get(source).indivTime - graph.vertexlist.get(dest).indivTime);
	                        graph.addEgde(source, dest, weight);
	                    }

	                    // Connecter avec la case en bas
	                    if (line < nlines - 1) {
	                        dest = (line + 1) * ncols + col;
	                        weight = Math.abs(graph.vertexlist.get(source).indivTime - graph.vertexlist.get(dest).indivTime);
	                        graph.addEgde(source, dest, weight);
	                    }

	                    // Connecter avec la case en bas à droite
	                    if (line < nlines - 1 && col < ncols - 1) {
	                        dest = (line + 1) * ncols + col + 1;
	                        weight = Math.abs(graph.vertexlist.get(source).indivTime - graph.vertexlist.get(dest).indivTime);
	                        graph.addEgde(source, dest, weight);
	                    }
	                }
	            }
	                
	               
	            
	        Scanner input=new Scanner(System.in);

	        // On obtient les noeuds de départ et d'arrivée
	        data = myReader.nextLine();
	        data = myReader.nextLine();
	        int startV = Integer.parseInt(data.split("=")[1].split(",")[0]) * ncols + Integer.parseInt(data.split("=")[1].split(",")[1]);
	        data = myReader.nextLine();
	        int endV = Integer.parseInt(data.split("=")[1].split(",")[0]) * ncols + Integer.parseInt(data.split("=")[1].split(",")[1]);

	        myReader.close();

	        // À changer pour avoir un affichage plus ou moins grand
	        int pixelSize = 10;
	        Board board = new Board(graph, pixelSize, ncols, nlines, groundColor, startV, endV);
	        drawBoard(board, nlines, ncols, pixelSize);
	        board.repaint();

	        try {
	            Thread.sleep(100);
	        } catch (InterruptedException e) {
	            System.out.println("stop");
	        }
           
	        // On appelle Dijkstra
	       // LinkedList<Integer> path = Dijkstra(graph, startV, endV, nlines * ncols, board);
	      *
            
          // on appelle A*  
            	LinkedList<Integer> path=AStar(graph,startV,endV,ncols,nlines*ncols,board);
            
	        // Écriture du chemin dans un fichier de sortie
	        try {
	            File file = new File("out.txt");
	            if (!file.exists()) {
	                file.createNewFile();
	            }
	            FileWriter fw = new FileWriter(file.getAbsoluteFile());
	            BufferedWriter bw = new BufferedWriter(fw);

	            for (int i : path) {
	                bw.write(String.valueOf(i));
	                bw.write('\n');
	            }
	            bw.close();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (FileNotFoundException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	    }
	}


}
