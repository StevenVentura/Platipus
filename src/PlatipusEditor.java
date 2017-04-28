import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;



public class PlatipusEditor extends JApplet implements KeyListener, MouseListener, MouseMotionListener
{
	
	
	public PlatipusMap pm;
	
	final JFileChooser fc = new JFileChooser();
	public ArrayList<EveryLine> everyLines = new ArrayList<EveryLine>();
	
	
	int mode = 0;
	private static final int MODE_DEFAULT = 0;
	private static final int MODE_DRAWING_SHAPE = 1;
	private static final int MODE_PLACING_MOB = 2;
	private static final int MODE_EVERY_LINE = 3;
	
	public static final TreeMap<Integer, BufferedImage> mobImages = new TreeMap<Integer, BufferedImage>();
	public TreeMap<Integer, BufferedImage> resizedMobImagesRight = new TreeMap<Integer, BufferedImage>();
	
	
	public PlatipusEditor()
	{
		pm = new PlatipusMap();
	}
	
	
	long CT, LT;
	
	private BufferedImage bi;
	private Graphics2D g;
	private Graphics2D g2;
	
	public Keyboard k;
	public Mouse m;
	
	private JFrame frame;
	public EditGUI gui;
	
	public void begin()
	{
		gui = new EditGUI(this);
		frame = new JFrame("PlatipusEditor!");
		frame.setSize(800+ 38,600+16);
		frame.setLocation(500,200);
		frame.add(this);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				int message = (JOptionPane.showConfirmDialog(frame, "Save?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE));
			if (message == JOptionPane.YES_OPTION)
			    {
				pm.save();        
				gui.println("the map was saved as " + pm.name + ".map");
				System.exit(0);
				}
				if (message == JOptionPane.NO_OPTION)
					System.exit(0);
				if (message == JOptionPane.CANCEL_OPTION)
					;//do nothing; return to the program.
			}
			});
		

		fwidth = frame.getSize().width; 
		fheight = frame.getSize().height;
		flocx = frame.getLocationOnScreen().x;
		flocy = frame.getLocationOnScreen().y;
		
		
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		bi = new BufferedImage(getSize().width,getSize().height, 5);
		g = bi.createGraphics();
		g2 = (Graphics2D)(this.getGraphics());
		
		k = new Keyboard();
		k.setKeyNames("delete backspace enter a b c d e f g h i j k l m n o p q r s t u v w x y z up down left right".split(" "));//k.setKeyNames("up down left right".split(" "));
		
		m = new Mouse();
		
		
		CT = System.currentTimeMillis(); LT = System.currentTimeMillis();
		long LTwindowCheck = 0;
		final long INTERVAL_WINDOW_CHECK = 500L;
		while(true)
		{
			CT = System.currentTimeMillis();
			
			if (CT-LTwindowCheck > INTERVAL_WINDOW_CHECK)
			{
				LTwindowCheck = CT;
				if (frame.getSize().height != (int)(frame.getSize().width*(double)(normalHeight/normalWidth)))
					fixWindowAspectRatio();
				if (fwidth != frame.getSize().width || fheight != frame.getSize().height)
					onWindowResized();
				if (frame.getLocationOnScreen().x != flocx || frame.getLocationOnScreen().y != flocy)
					onWindowMoved();
			}
			
			if (CT - LT > 42)
			{
				LT = CT;
				this.handleKeyPresses();
				this.handleMousePresses();
				
				
				this.paint();
			}
			
			

			
			
		}
		
		
		
	}
	
	/*public void fillScaledRect(int x, int y, int w, int h)
	{
		g.fillRect((int)((x-hShift)*rw),getSize().height-(int)((y-vShift)*rh),(int)(rw*w),(int)(rh*h));
	}*/
	
	public int hShift=0, vShift=0;
	
	public void drawMob(Mob m)
	{
		
		ImageHelper.draw(resizedMobImagesRight.get(m.getImageIndex()), (int)m.x, (int)m.y, g, rw, rh, getSize().height, hShift, vShift);
	}
	
	public void draw()
	{
		pm.defineDrawableShapes(rw, rh, getSize().height, hShift, vShift);
		/*
		 * draw all shapes
		 */
		for (PolyShape s : pm.shapes)
		{
			
			g.setPaint(s.color);
			
			if (s instanceof Platform)
				g.setPaint(Platform.INDICATION_COLOR);
			if (s instanceof ServiceLedge)
				g.setPaint(Platform.SERVICE_INDICATION_COLOR);
			
			g.fill(s.drawableShape);
		}
		
		for (Integer i : pm.mobs.keySet())
		{
			Mob mob = pm.mobs.get(i);
			drawMob(mob);
		}
		for (EveryLine e : everyLines)
		{
			e.draw(g, rw, rh, getSize().height, hShift, vShift);
		}
		
		if (mode == MODE_EVERY_LINE)
			currentLine.draw(g, rw, rh, getSize().height, hShift, vShift);
		
		/*
		 * drawCurrentMob
		 */
		if (mode == MODE_PLACING_MOB)
		{
		currentMob.definePolyShape(m.snappedX, m.snappedY);
			drawMob(currentMob);
		}
		
		
		/*
		 * drawCurrentShape as lines and transparent fill
		 */
		if (mode == MODE_DRAWING_SHAPE && currentShape.xs != null)
		{
			
		if (currentShape instanceof PointZone)
		{
			
			 // do the same thing minus the last vertex, and then draw an arrow to that vertex from the center of the shape.
			
			if (currentShape.xs.length != 1)
			{
			int[] xs = new int[currentShape.xs.length-1]; int[] ys = new int[currentShape.ys.length-1];
			for (int i = 0; i < xs.length; i++)
			{
				xs[i] = currentShape.xs[i];
				ys[i] = currentShape.ys[i];
			}
			
			PolyShape t = new PolyShape();
			t.xs = xs; t.ys = ys;
			
			g.setPaint(Color.YELLOW);
			for (int i = 0; i < xs.length-1; i++)
			{
				drawThickLine(xs[i],ys[i],xs[i+1],ys[i+1]);
			}
			drawThickLine(xs[xs.length-1],ys[ys.length-1],xs[0],ys[0]);
			
			t.defineDrawableShape(rw, rh, getSize().height, hShift, vShift);
			
			g.setPaint(new Color(0.888f, 0.888f, 0.05f, 0.122f));
			g.fill(t.drawableShape);
			
			
			//  now draw an arrow from the center to the spawn location.
			
			g.setPaint(Color.WHITE);
			drawThickLine(currentShape.xs[currentShape.xs.length-2],currentShape.ys[currentShape.ys.length-2],currentShape.xs[currentShape.xs.length-1],currentShape.ys[currentShape.ys.length-1]);
			}
			else
			{
				g.setPaint(Color.YELLOW);
				drawThickLine(currentShape.xs[0],currentShape.ys[0],currentShape.xs[0],currentShape.ys[0]);
			}
			
			
		}
		else
		{
		g.setPaint(Color.YELLOW);
		int[] xs = currentShape.xs, ys = currentShape.ys;
		for (int i = 0; i < currentShape.xs.length-1; i++)
		{
			drawThickLine(xs[i],ys[i],xs[i+1],ys[i+1]);
		}
		drawThickLine(xs[xs.length-1],ys[ys.length-1],xs[0],ys[0]);
		
		currentShape.defineDrawableShape(rw, rh, getSize().height, hShift, vShift);
		
		g.setPaint(new Color(0.888f, 0.888f, 0.05f, 0.122f));
		g.fill(currentShape.drawableShape);
		}
		
		}
			
	}
	
	int grid = 50;
	
	public void drawGrid()
	{
		g.setPaint(Color.white.darker().darker());
		int width = (int)normalWidth, height = (int)normalHeight;//getSize().width, height = getSize().height;
		
		
		
			
			for (int x = pm.minWidth; x <= pm.maxWidth; x += width/grid)
			{
				drawLine(x, pm.minHeight, x, pm.maxHeight);
			}
		
			
			for (int y = pm.minHeight; y <= pm.maxHeight; y += height/grid)
			{
				drawLine(pm.minWidth, y, pm.maxWidth, y);
			}
		
		
		//draw mouse circle
		g.setPaint(Color.ORANGE);
		
		drawScaledRect(m.snappedX-5, m.snappedY+5,10,10);
		
	}
	public Point getTransformedPoint(double x, double y)//the point to be ?
	{
		return new Point((int)((x+hShift)*rw),(int)(getSize().height-(y-vShift)*rh));
	}
	private void drawLine(double x1, double y1, double x2, double y2)
	{
		g.draw(new Line2D.Double((x1-hShift)*rw,getSize().height-(y1-vShift)*rh,(x2-hShift)*rw,getSize().height-(y2-vShift)*rh));
	}
	private void drawScaledOval(double x, double y, double width, double height)
	{
		g.drawOval((int)((x-hShift)*rw)-(int)(width/2*rw),getSize().height-((int)((y-vShift)*rh)-(int)(height/2*rh)),(int)(rw*width),(int)(rh*height));
	}
	public void drawScaledRect(int x, int y, int w, int h)
	{
		
		g.drawRect((int)((x-hShift)*rw),getSize().height-(int)((y-vShift)*rh),(int)(rw*w),(int)(rh*h));
	}
	public void fillScaledRect(int x, int y, int w, int h)
	{
		
		g.fillRect((int)((x-hShift)*rw),getSize().height-(int)((y-vShift)*rh),(int)(rw*w),(int)(rh*h));
	}

	
	public void drawGUI()
	{
		
		
	}
	
	public void paint()
	{
		g.setColor(new Color(43,15,1));g.fillRect(0,0,getSize().width,getSize().height);//background
		
		this.draw();
		this.drawGrid();
		this.drawGUI();
		
		g2.drawImage(bi, null, 0, 0);
	}
	
	public void handleKeyPresses()
	{
		int a = 8;
		if (k.k("up") || k.k("w"))
		{
			vShift += a;
		}
		if (k.k("down") || k.k("s"))
		{
			vShift -= a;
		}
		if (k.k("left") || k.k("a"))
		{
			hShift -= a;
		}
		if (k.k("right") || k.k("d"))
		{
			hShift += a;
		}
		if (mode == MODE_EVERY_LINE)
		{
			if (k.t("delete") || k.t("backspace"))
			{
				currentLine.removePoint();
			}
			
		}
			
		if (mode == MODE_DRAWING_SHAPE)
		{
			if (k.t("enter"))
			{
				
				mode = MODE_DEFAULT;
				if (!currentShape.shapeEmpty())
				{
				
					if (currentShape instanceof PointZone)
					{
						//use the last vertex as the spawn location
						Point p = new Point(currentShape.xs[currentShape.xs.length-1], currentShape.ys[currentShape.ys.length-1]);
						((PointZone)(currentShape)).respawnX = p.x;
						((PointZone)(currentShape)).respawnY = p.y;
						currentShape.popPoint();						
					}
					
				pm.shapes.add(currentShape);
				
				
				}
				currentShape = null;
				gui.println("added shape");
			}
			if (k.t("delete") || k.t("backspace"))
			{
				currentShape.popPoint();
			}
			
			if (k.t("p"))//turn it into a Platform object
			{
				if (isValidPlatform(currentShape))
				{
					currentShape = new Platform(currentShape.xs,currentShape.ys);
					currentShape.color = PolyShape.DEFAULT_COLOR;
					gui.println("converted to platform");
					{
					mode = MODE_DEFAULT;
					if (!currentShape.shapeEmpty())
					{
					pm.shapes.add(currentShape);
					gui.println("added shape");
					}
					else
						gui.println("deleted shape");
					currentShape = null;
					
					}
				}
				else
				gui.println("invalid platform");
			}
			
			if (k.t("i"))//turn it into a ServiceLedge object
			{
				if (isValidPlatform(currentShape))
				{
					currentShape = new ServiceLedge(currentShape.xs,currentShape.ys);
					currentShape.color = PolyShape.DEFAULT_COLOR;
					gui.println("converted to ServiceLedge");
					{
					mode = MODE_DEFAULT;
					if (!currentShape.shapeEmpty())
					{
					
					pm.shapes.add(currentShape);
					
					gui.println("added shape");
					
					}
					else
						gui.println("deleted shape");
					
					currentShape = null;
					
					}
				}
				else
				gui.println("invalid platform");
			}
			
			
			if (k.t("n"))//convert back to normal wall
			{
				if (!(currentShape.shapeEmpty()))
				{
				mode = MODE_DEFAULT;
				currentShape = new PolyShape(currentShape.xs, currentShape.ys);
				currentShape.color = PolyShape.DEFAULT_COLOR;
				
				pm.shapes.add(currentShape);
				currentShape = null;
				gui.println("Converted to wall!");
				}
			}
			
			
			
			
		}//end MODE_DRAWING_SHAPE
		else
		if (mode == MODE_PLACING_MOB)
		{
			if (k.t("enter"))
			{
				mode = MODE_DEFAULT;
				
				int ID = currentMob.ID;
				if (currentMob instanceof Bloop)
				pm.putMob(currentMob.ID, new Bloop(m.snappedX, m.snappedY));
				else if (currentMob instanceof Bunny)
				pm.putMob(currentMob.ID, new Bunny(m.snappedX, m.snappedY));
				else if (currentMob instanceof Zomboo)
				pm.putMob(currentMob.ID, new Zomboo(m.snappedX, m.snappedY));
				
				pm.mobs.get(ID).ID = ID;
				
				gui.println("mob placed");
				
				
			}
			if (k.t("delete") || k.t("backspace"))
			{
				mode = MODE_DEFAULT;
				pm.mobs.remove(currentMob.ID);
				gui.println("mob deleted");
			}
			
			
		}
		else
		if (mode == MODE_DEFAULT)
		{
			if (k.t("n"))
			{
				mode = MODE_DRAWING_SHAPE;
				currentShape = new PolyShape();
				gui.println("making a new wall");
				printControls(MODE_DRAWING_SHAPE);
			}
		}//end MODE_DEFAULT
		
		k.untype();
		
		
	}
	
	private void printControls(int mode)
	{
		if (mode == MODE_DEFAULT)
		{
			gui.println("--Default Mode Controls--");
			gui.println("N : start new wall");
			gui.println("LCLICK : edit a wall");
		}
		else if (mode == MODE_DRAWING_SHAPE)
		{
			gui.println("--Wall Mode Controls--");
			gui.println("LCLICK : add vertex");
			gui.println("DELETE : remove vertex");
			gui.println("ENTER : finish wall");
			gui.println("i : convert to ServiceLedge");
			gui.println("p : convert to Platform");
			gui.println("n : convert back to wall");
		}
		else if (mode == MODE_PLACING_MOB)
		{
			gui.println("--Mob Place Controls--");
			gui.println("ENTER : place mob");
			gui.println("DELETE : delete mob");
		}
	}
	
	
	private PolyShape currentShape;
	private EveryLine currentLine;
	private Mob currentMob;
	
	public void handleMousePresses()
	{
		
		
		if (m.t(Mouse.LEFT_CLICK))
		{
			int mouseX = m.mouseX, mouseY = m.mouseY;
			gui.println("click: x=" + m.snappedX + ", y=" +m.snappedY);
			switch(mode)
			{
			
			case (MODE_EVERY_LINE):
			{
				currentLine.addPoint(m.snappedX, m.snappedY);
				if (currentLine.defined)
				{
					everyLines.add(currentLine);
					mode = MODE_DEFAULT;
					currentLine = null;
				}
				
				
			}	
			break;
				case (MODE_DEFAULT):
				{
					//see if a mob was selected; they have priority over walls.
					Mob sel = null;
					
					int index = -1;
					int selectedIndex = -1;
					
					for (Integer i : pm.mobs.keySet())
					{
						Mob mob = pm.mobs.get(i);
						index++;
						
						if (ImageHelper.getScaledRect(Mob.getClickRectangle(mob, mob.x, mob.y), rw, rh, getSize().height, hShift, vShift).contains(new Point(mouseX, mouseY)))
						{
							sel = mob;
							selectedIndex = mob.ID;
						}
					}
					
					if (sel != null)
					{
						mode = MODE_PLACING_MOB;
						//pm.mobs.remove(selectedIndex);
						gui.println("moving mob " + selectedIndex);
						currentMob = sel;
						
						break;
					}
					
					//////////////////////////
					//see if a wall was selected, if a mob was not clicked on.
					PolyShape selected = null;
				
				
					index = -1;
					selectedIndex = -1;
					for (PolyShape s : pm.shapes)
						{
							index++;
							if (s.drawableShape.contains(new Point(mouseX, mouseY)))
							{
								selected = s;
								selectedIndex = index;
							}
						}
				
					if (selected != null)
					{
						mode = MODE_DRAWING_SHAPE;
						currentShape = pm.shapes.get(selectedIndex);
						pm.shapes.remove(selectedIndex);
						
						if (currentShape instanceof PointZone)
						{
							currentShape.addPoint(new Point((int)((PointZone)currentShape).respawnX,
															(int)((PointZone)currentShape).respawnY));
						}
						
						break;
					}
					
					/////////////////////////////////////////////////////////////////////
					
				
				
				}
				break;
			
			
			
				case (MODE_DRAWING_SHAPE):
				{
					int x = m.snappedX, y = m.snappedY;
					currentShape.addPoint(new Point(x,y));
				}
				break;
			
			
			}
			
		}
		
		m.untype();
		
		
	}
	
	public void drawThickLine(double x1, double y1, double x2, double y2)//so i can see over the grid
	{
		drawLine(x1-1,y1-1,x2-1,y2-1);
		drawLine(x1,y1,x2,y2);
		drawLine(x1+1,y1+1,x2+1,y2+1);
	}
	
	
	
	
	public void mouseMoved(MouseEvent e)
	{
		m.setLocation(e.getX(),e.getY());
		
		double x = m.getScaledLocation(rw,rh,(int)normalHeight,hShift,vShift).x, y = m.getScaledLocation(rw,rh,(int)normalHeight,hShift,vShift).y;
		Point p = m.snapToGrid(x,y,normalWidth, normalHeight,grid);
		x = p.x; y = p.y;
		m.snappedX = (int)x; m.snappedY = (int)y;
		
		
		
		
	}
	public void mouseDragged(MouseEvent e)
	{
		
		m.setLocation(e.getX(),e.getY());
	}
	
	public void mouseClicked(MouseEvent e)
	{
		
	}
	
	public void mousePressed(MouseEvent e)
	{
		int button = e.getButton();
		m.keyPress(button);
		
		
	}
	
	public void mouseReleased(MouseEvent e)
	{
		m.keyRelease(e.getButton());
		
	}
	
	public void keyPressed(KeyEvent e)
	{
		k.keyPress(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase());
	}
	public void keyReleased(KeyEvent e)
	{
		k.keyRelease(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase());
	}
	
	public void keyTyped(KeyEvent e){}
	
	public void mouseEntered(MouseEvent e)
	{
		
	}
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	double normalWidth = 800, normalHeight = 600;
	double rh, rw;
	int fwidth, fheight, flocx, flocy;

	private void fixWindowAspectRatio()
	{
		double w = frame.getSize().width; double h = frame.getSize().height;
		
		if (h > (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight())//if the user tries to maximize the window or make it too big
		{
			h = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 50;
			frame.setSize((int)(h*(double)normalWidth/normalHeight),(int)h);//fit to the max height allowed
			int screenWidth = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			w = frame.getSize().width; 
			frame.setLocation((int)((screenWidth-w)/2),0);//center the frame
		}
		
		else if (h < (int)(w*(double)(normalHeight/normalWidth)) || w < fwidth)
		{
			frame.setSize((int)w,(int)(w*(double)normalHeight/normalWidth));
		}
		else
		{
			frame.setSize((int)(h*(double)normalWidth/normalHeight),(int)h);	
		}
	}
	private void onWindowResized()
	{
		fwidth = frame.getSize().width; fheight = frame.getSize().height;
		
		
		
		this.setSize(new Dimension(fwidth-16, fheight-38));
		bi = new BufferedImage(getSize().width,getSize().height, 5);
		g = bi.createGraphics();
		g2 = (Graphics2D)(this.getGraphics());
		
		rh = getSize().height/normalHeight;//ratio-height scalar
		rw = getSize().width/normalWidth;//ratio-width scalar
		
		for (Integer i : mobImages.keySet())
			resizedMobImagesRight.put(i, ImageHelper.resize(mobImages.get(i),(int)(mobImages.get(i).getWidth()*rw), (int)(mobImages.get(i).getHeight()*rh)));
		
		pm.defineDrawableShapes(rw,rh,getSize().height,hShift,vShift);
	}
	private void onWindowMoved()
	{
		flocx = frame.getLocationOnScreen().x; flocy = frame.getLocationOnScreen().y;
		gui.f.setLocation(flocx-200,flocy+5);
	}
	
	
	public static void main(String[]args)
	{
		PlatipusClient.checkIfImagesExist();
		PlatipusClient.defineMobImages(mobImages);
		PlatipusEditor pe = new PlatipusEditor();
		pe.begin();
		
		
		
	}
	
	public static int pint(String p)
	{
		return Integer.parseInt(p);
	}
	
	public static boolean isValidPlatform(PolyShape s)
	{
		ArrayList<CollisionLine> cls = CollisionLine.getCollisionLines(s);
		
		int verticalCount = 0;
		int horizontalCount = 0;
		for (CollisionLine c : cls)
		{
			if (c.type == CollisionLine.TYPE_HORIZONTAL)
				horizontalCount++;
			if (c.type == CollisionLine.TYPE_VERTICAL)
				verticalCount++;
		}
		
		return (horizontalCount == 2 && verticalCount == 2);
		
		
		
	}
	public void startEveryLine()
	{
		mode = MODE_EVERY_LINE;
		currentLine = new EveryLine(this);
		gui.println("::Every-Line::");
		gui.println("define point 1");
		
	}
	
	class EditGUI
	{
	private PlatipusEditor bs;
	private JFrame f;
	private JTextArea console;
	private JScrollPane jsp;

	public EditGUI(PlatipusEditor bs)
	{
		this.bs=bs;
		console = new JTextArea();
		final int width = 200, height = 500;
		f = new JFrame("PlatipusEditor Console by Steven");
		f.setResizable(false);
		f.setLayout(new FlowLayout());

		f.setSize(width,height);
		f.setLocation(300,200+5);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				int message = (JOptionPane.showConfirmDialog(frame, "Save?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE));
			if (message == JOptionPane.YES_OPTION)
			    {
				pm.save();        
				gui.println("the map was saved as " + pm.name + ".map");
				System.exit(0);
				}
				if (message == JOptionPane.NO_OPTION)
					System.exit(0);
				if (message == JOptionPane.CANCEL_OPTION)
					;//do nothing; return to the program.
			}
			});

		final JTextField field = new JTextField();
		field.setBackground(new Color(0.025f,0.025f,0.025f));
		field.setForeground(new Color(0.4862f, 0.1882f, 0.1882f));
		field.setFont(new Font("DejaVu Sans", Font.BOLD, 26));

		field.setPreferredSize(new Dimension(width-14,88));

		field.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
				
					
					String text = e.getActionCommand().toLowerCase();
					
					String[] txt = text.split(" ");
					
					if (txt[0].equals("every"))
					{
						startEveryLine();
					}
					else if (txt[0].equals("load"))
					{
						everyLines.clear();
						mode = MODE_DEFAULT;
						currentShape = null;
						fc.setCurrentDirectory(new File("."));
						FileNameExtensionFilter filter = new FileNameExtensionFilter("Map files", "map");
						fc.setFileFilter(filter);
						int rv = fc.showOpenDialog(new JFrame());
						if (rv == fc.APPROVE_OPTION)
						{
						File file = fc.getSelectedFile();
						try{
							pm = new PlatipusMap(file.getName());
							for (Integer i : pm.mobs.keySet())
							{
								Mob m = pm.mobs.get(i);
								m.ID = i;
							}
							frame.setTitle("working on : " + pm.name);
						}catch(Exception exc){exc.printStackTrace();};
						}
					}
					else if (txt[0].startsWith("test"))
					{
						println("Testing!");
						
						new Thread() {public void run(){
							System.out.println(pm.name);
							String[] args = {pm.name};
							PlatipusServer.main(args);
						}}.start();
						
						new Thread() {public void run(){
							String[] args = {"test"};
							PlatipusClient.main(args);
						}}.start();
						
					}
					else if (txt[0].startsWith("stat"))
					{
						println("x:"+pm.minWidth+"-"+pm.maxWidth);
						println("y:"+pm.minHeight+"-"+pm.maxHeight);
						
					}
					else if (txt[0].equals("save"))
					{
						mode = MODE_DEFAULT;
						currentShape = null;
						pm.save();
						println("!!Saved as " + pm.name + "!!");
					}
					
					else if (txt.length == 1)		
					{
					printHelp(txt);
					}
					
					else if (txt[0].equals("new"))
					{
						if (txt[1].equals("map"))
						{
							if (txt.length != 3)
								println("syntax: new map [mapname]");
							else
							{
								String mapName =  txt[2];
								frame.setTitle("working on : " + mapName);
								pm = new PlatipusMap();
								pm.name = mapName;
								println("new map started: " + mapName);
							}
							
						}
						else if (txt[1].equals("mob"))
						{
							if (txt.length != 3)
							{
								println("syntax: new mob [mobname]");
								for (String s : Mob.names)
									println("\t"+s);
							}
							else
							{
							mode = MODE_PLACING_MOB;
							if (txt[2].equals("bloop"))
								currentMob = new Bloop();
							else if (txt[2].equals("bunny"))
								currentMob = new Bunny();
							else if (txt[2].equals("zomboo"))
								currentMob = new Zomboo();
							
							pm.addMob(currentMob);
							pm.mobs.remove(currentMob.ID);
							
							println("hit enter to place " + txt[2]);
							}
							
						}
						else if (txt[1].equals("door"))
						{
							if (txt.length != 3 || !"up down left right".contains(txt[2].toLowerCase()))
								println("syntax: new door [str:direction]");
							else
							{
							mode = MODE_DRAWING_SHAPE;
							currentShape = new Door();
							((Door)currentShape).setDirection(txt[2]);
							println("making a new door");
							println("hit enter to finish door");
							}
						}
						else if (txt[1].equals("wall"))
						{
							mode = MODE_DRAWING_SHAPE;
							currentShape = new PolyShape();
							println("making a new wall");
							println("hit enter to finish wall");
						}
						else if (txt[1].equals("pointzone"))
						{
							if (txt.length != 3)
								println("syntax: new pointzone [int:points]");
							else
							{
							mode = MODE_DRAWING_SHAPE;
							int value = pint(txt[2]);
							currentShape = new PointZone();
							((PointZone)currentShape).pointValue = value;
							println("making a new PointZone-" + value);
							println("hit enter to finish pointzone");
							}
						}
					}
					else if (txt[0].equals("set"))
					{
						if (txt[1].equals("name"))
						{
							
							if (txt.length != 3)
								println("syntax: set name [mapname]");
							else
							{
								String mapName = txt[2];
								frame.setTitle("working on : " + mapName);
								pm.name = mapName;
								println("map name set to " + mapName);
							}
							
							
							
						}
						try{
						if (txt[1].equals("minx"))
						{
							pm.minWidth = pint(txt[2]);
						}
						if (txt[1].equals("maxx"))
						{
							pm.maxWidth = pint(txt[2]);
						}
						if (txt[1].equals("miny"))
						{
							pm.minHeight = pint(txt[2]);
						}
						if (txt[1].equals("maxy"))
						{
							pm.maxHeight = pint(txt[2]);
						}
						}catch(Exception ex){println("syntax: set xxxx [value]"); ex.printStackTrace();};
						
						
					}
					field.setText("");
					
					
					
				}
			}
					);
		f.add(field);
		
		
		jsp = new JScrollPane(console);

		console.setBackground(new Color(0.025f,0.025f,0.025f));
		console.setForeground(new Color(0.4862f, 0.1882f, 0.1882f));
		console.setFont(new Font("DejaVu Sans", Font.BOLD, 12));

		jsp.setPreferredSize(new Dimension(width-14,height-88-42));

		f.add(jsp);




		f.setVisible(true);
	}
	
	
	public void clearScreen()
	{
		console.setText("");
	}
	private void printHelp(String[] txt)
	{
		if (txt[0].equals("controls"))
		{
			printControls(mode);
		}
		else if (txt[0].equals("new") && txt.length == 1)
		{
			this.println("--new commands--");
			this.println("map [mapname]");
			this.println("wall");
			this.println("door [str:direction]");
			this.println("pointzone [int:value]");
			this.println("mob [mobname]");
			return;
		}
		else if (txt[0].equals("set"))
		{
			this.println("--set commands--");
			String s = "set ";
			this.println(s + "name [mapname]");
			this.println(s + "minx [value]");
			this.println(s + "maxx [value]");
			this.println(s + "miny [value]");
			this.println(s + "maxy [value]");
			this.println("---------------------");
			return;
		}
		else if (txt[0].equals("help") || txt.length == 1)
		{
		this.println("--help listing--");
		this.println("new");
		this.println("set");
		this.println("controls");
		this.println("save");
		this.println("load");
		this.println("stats");
		this.println("test");
		return;
		}
		//txt.length > 1 from here on
		
		
		
		
		
		
		
	}
	
	public void printDetails(PolyShape s)
	{
		
	}

	public void print(String s)
	{
	console.append(s);
	}
	public void println(String s)
	{
	this.print(s);
	this.print("\r\n");
	console.scrollRectToVisible(new Rectangle(0,console.getBounds().height,1,1));
	}


	}

}

