import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Scanner;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;




@SuppressWarnings("serial")
public class PlatipusClient extends JApplet implements KeyListener, MouseListener
{
	public static int pint(String is)
	{
		return Integer.parseInt(is);
	}
private BufferedImage bi;
private Graphics2D g;
private Graphics2D g2;

public PlayerStats stats;//for this player

public PrintWriter out;
public BufferedReader in;
public Socket onlysocket;

public Keyboard k = new Keyboard();
public Mouse m;

public int user = -1;//the user's ID in the TreeMap. note that the user is referenced by user().
public TreeMap<Integer, Player> players = new TreeMap<Integer, Player>();

public PlatipusMap pm;

public ArrayList<ParticleEffect> particles = new ArrayList<ParticleEffect>();


public static final TreeMap<Integer, BufferedImage> mobImages = new TreeMap<Integer, BufferedImage>();
public TreeMap<Integer, BufferedImage> resizedMobImagesRight = new TreeMap<Integer, BufferedImage>();
public TreeMap<Integer, BufferedImage> resizedMobImagesLeft = new TreeMap<Integer, BufferedImage>();

public static void defineMobImages(TreeMap<Integer, BufferedImage> mobImages)
{
	
    try {
    	mobImages.clear();
        
            BufferedImage player = ImageIO.read(new File("carrot.png"));
            BufferedImage bloop = ImageIO.read(new File("bloop.png"));
            BufferedImage bunny = ImageIO.read(new File("bunny.png"));
            BufferedImage zomboo = ImageIO.read(new File("zomboo.png"));
            player = ImageHelper.makeTransparent(player, Color.WHITE);
            bloop = ImageHelper.makeTransparent(bloop, Color.WHITE);
            bunny = ImageHelper.makeTransparent(bunny, Color.WHITE);
            mobImages.put(ImageHelper.IMAGE_PLAYER_DEFAULT, ImageHelper.resize(player, 32, 32));
            mobImages.put(ImageHelper.IMAGE_BLOOP, ImageHelper.resize(bloop, Bloop.DEFAULT_SIZE, Bloop.DEFAULT_SIZE));
            mobImages.put(ImageHelper.IMAGE_BUNNY, ImageHelper.resize(bunny, Bunny.DEFAULT_SIZE, Bunny.DEFAULT_SIZE));
            mobImages.put(ImageHelper.IMAGE_ZOMBOO, ImageHelper.resize(zomboo, Zomboo.DEFAULT_WIDTH, Zomboo.DEFAULT_HEIGHT));

        	
    } catch (Exception ex) {
        ex.printStackTrace();System.exit(0);
    }
}

public void printWarning(String s)
{
	
}

public String grabString()
{
	ArrayList<String> chars = new ArrayList<String>();
	chars.add("q");
	chars.add("w");
	chars.add("e");
	String out = "";
	for (int i = 0; i < 15; i++)
	{
		int c = (int)(chars.size()*Math.random());
		
		out += chars.get(c);
	}
	
	return out;
}

public boolean editorTestRun = false;
public PlatipusClient(String test) throws Exception
{
	if (test.equals("test"))
	{
		editorTestRun = true;
	}
	
}


public PlatipusClient() throws Exception
{


}//end Constructor

public static void createFile(String fileName, String whatToWrite)//creates new File fileName, populated with String whatToWrite. If you want new lines, you will have to use \r\n's in the whatToWrite String.
{
try{
FileWriter fstream = new FileWriter(fileName);
BufferedWriter writer = new BufferedWriter(fstream);

System.out.println(whatToWrite);
writer.write(whatToWrite + "\r\n");

writer.close();

}catch(Exception e){e.printStackTrace();};
}

private JFrame frame;
private int fwidth, fheight;

private long CTmain,LTpaint,LTkeyPress,LTwindowCheck;
public static final long INTERVAL_KEY_PRESS = 32-1;
public static final long INTERVAL_PAINT = 32-1;
public static final long INTERVAL_WINDOW_CHECK = 500-1;

private volatile boolean runTheGame = true;

public volatile boolean connected = false;//needed the volatile! -- tells whether or not the client launcher has connected to the server.

public boolean connect(String ip, int port)
{
	onlysocket = null;
	try{
    onlysocket = new Socket();
    onlysocket.connect(new InetSocketAddress(ip,port), 3500);
    out = new PrintWriter(onlysocket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(onlysocket.getInputStream()));
    
	}catch(Exception e){return false;};
	
	if (onlysocket != null)
	{
		connected = true;
	}
	
	return (onlysocket != null);
}


public void mousePressed(MouseEvent e)
{
	m.keyPress(e.getButton());
}
public void mouseReleased(MouseEvent e)
{
	m.keyRelease(e.getButton());
}
public void mouseClicked(MouseEvent e)
{
	
}



private ClientGUI cgui = null;
public void begin()//the main method calls this, and this is where the main looping of the entire program occurs.
{
	k.setKeyNames("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z a b c d e f g h i j k l m n o p q r s t u v w x y z 1 2 3 4 5 6 7 8 9 0 space backspace up down left right enter shift escape".split(" "));
	
	if (editorTestRun)
		this.connect("localhost",8123);
	else
		cgui = new ClientGUI(this);
	
	
	
	while(!connected);//pause code until the client connects to the server
	
	if (editorTestRun == false)
	cgui.frame.setVisible(false);
	
	
frame = new JFrame("PlatipusClient");
frame.setSize((int)normalWidth+38, (int)normalHeight+16);
int totalScreenWidth = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
int totalScreenHeight = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
frame.setLocation(totalScreenWidth / 2 - frame.getWidth() / 2 - 100, totalScreenHeight / 2 - frame.getHeight() * 3/4);
if (frame.getLocation().y < 0)
	frame.setLocation(frame.getLocation().x, 0);
//if (editorTestRun == false)
//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	frame.add(this);
	

	
	
WindowListener windowlisten = new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
		
		runTheGame = false;
		out.println("stop server");
	}
	
};
if (editorTestRun)
frame.addWindowListener(windowlisten);
	
fwidth = frame.getSize().width; 
fheight = frame.getSize().height;


this.addKeyListener(this);
this.addMouseListener(this);
this.setFocusable(true);

m = new Mouse();

frame.setVisible(true);

bi = new BufferedImage(getSize().width,getSize().height, 5);
g = bi.createGraphics();
g2 = (Graphics2D)(this.getGraphics());

rh = getSize().height/normalHeight;//ratioheight scalar
rw = getSize().width/normalWidth;//ratiowidth scalar

for (Integer i : mobImages.keySet())//just so they're not null
	{
	resizedMobImagesLeft.put(i, ImageHelper.resize(mobImages.get(i),(int)(mobImages.get(i).getWidth()*rw), (int)(mobImages.get(i).getHeight()*rh)));
	resizedMobImagesRight.put(i, ImageHelper.resize(mobImages.get(i),(int)(mobImages.get(i).getWidth()*rw), (int)(mobImages.get(i).getHeight()*rh)));
	}
new Thread(receivedWriter()).start();

CTmain = System.currentTimeMillis();
LTpaint = System.currentTimeMillis();
LTkeyPress = System.currentTimeMillis();
LTwindowCheck = System.currentTimeMillis();
while(runTheGame)//the main loop of the entire program.
{  
try{Thread.sleep(1);}catch(Exception e){};
if (pm == null)
	continue;
	
CTmain = System.currentTimeMillis();
if (CTmain - LTkeyPress > INTERVAL_KEY_PRESS)
{
	double timeMultiplier = ((double)(CTmain-LTkeyPress))/((double)(INTERVAL_KEY_PRESS));
	if (user == -1)
		continue;



user().llx = user().lx; user().lly = user().ly;
user().lx = user().x; user().ly = user(). y;


this.handleKeyPresses(timeMultiplier);
this.handleMousePresses();
this.applyPhysics(timeMultiplier);//moves the x and y, leaving the lx and ly untouched.

this.applyStance();

this.doCollisions();//collides using x,y,lx,ly. sets x and y to the right position
tryToDropDown = false;

this.applyDamage(CTmain - LTkeyPress, timeMultiplier);
this.sendUpdatedPlayer();//broadcasts the final correct position and other info
LTkeyPress = CTmain;
}

if (CTmain-LTwindowCheck > INTERVAL_WINDOW_CHECK)
{
	LTwindowCheck = CTmain;
	if (frame.getSize().height != (int)(frame.getSize().width*(double)(normalHeight/normalWidth)))
		fixWindowAspectRatio();
	if (fwidth != frame.getSize().width || fheight != frame.getSize().height)
		onWindowResized();
}



if (CTmain-LTpaint > INTERVAL_PAINT)
{
ticParticles(CTmain-LTpaint);
this.paint(CTmain-LTpaint);
LTpaint = CTmain;
}

}

frame.dispose();



}

public void applyDamage(long time, double timeMultiplier)//also tic's the shield re-charge
{
	int d = user().damageTakenThisLoop;
	
	if (d == 0)//if he didn't take any damage
	{
		user().timeSinceLastShieldRecharge += time;
		if (user().timeSinceLastShieldRecharge > user().shieldRechargeTime)
		{
			user().shieldPoints += user().shieldRechargeRate*timeMultiplier;
			if (user().shieldPoints > user().maxShieldPoints)
			{
				user().shieldPoints = user().maxShieldPoints;
			}
		}
	}
	else//he took damage
	{
		user().timeSinceLastShieldRecharge = 0;//reset the shield timer
		
	if (user().shieldPoints > 0)// no bleed-through
	{
		user().shieldPoints -= d;
		if (user().shieldPoints <= 0)
		{
			user().shieldPoints = 0;
			//shield break
			this.broadcastParticles(Player.SHIELD_BLUE, 250, user().x, user().y);
		}
	}
	else
	{
		user().points -= d;
		if (user().points < 0)
			user().points = 0;
	}
	}
	
	user().damageTakenThisLoop = 0;
}

public void swingBat()
{
	
	if (user().swordSwing == -1 && user().stance == Player.STANCE_DEFAULT || user().stance == Player.STANCE_ON_OTHERS_HEAD)
	{
		user().swordSwing = 39;
	}
}
public void handleMousePresses()
{
	if (m.f(Mouse.LEFT_CLICK))
	{
		swingBat();
	}
	
	m.untype();
	
}

private void ticParticles(long time)
{
	try{
	for (int i = 0; i < particles.size(); i++)
	{
		ParticleEffect p = particles.get(i);
		p.tic(time);
		if (p.lifeLeft < 0)
			particles.remove(i);
	}
	}catch(Exception e){};
}

private void applyStance()
{
	if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
	{
		user().x = players.get(user().otherPlayerID).x;
		user().y = players.get(user().otherPlayerID).y+32;
	}
	
	if (user().stance == Player.STANCE_BEING_CARRIED)
	{
		user().x = players.get(user().otherPlayerID).x;
		user().y = players.get(user().otherPlayerID).y+32;
	}
	
	
	
}

private void fixWindowAspectRatio()
{
	double w = frame.getSize().width; double h = frame.getSize().height;
	
	if (h > (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight())//if the user tries to maximize the window or make it too big
	{
		h = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 50;//cus the windows bar is like 40 pixels or something
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
	
	rh = getSize().height/normalHeight;//ratioheight scalar
	rw = getSize().width/normalWidth;//ratiowidth scalar
	
	
	
	for (Integer i : mobImages.keySet())
	{
		resizedMobImagesRight.put(i, ImageHelper.resize(mobImages.get(i),(int)(mobImages.get(i).getWidth()*rw), (int)(mobImages.get(i).getHeight()*rh)));
		resizedMobImagesLeft.put(i, ImageHelper.flipHorizontal(ImageHelper.resize(mobImages.get(i),(int)(mobImages.get(i).getWidth()*rw), (int)(mobImages.get(i).getHeight()*rh))));
	}
	
	
		
	
	pm.defineDrawableShapes(rw,rh,getSize().height,hShift,vShift);
}

public Player user()
{
	return players.get(user);
}


public void sendUpdatedPlayer()
{
	out.println(user());
}

public void populateDoors()
{
	//doors.clear();
		int doorIndex = -1;
		for (PolyShape s : pm.shapes)
		{
			
			if (s instanceof Door)
			{	
				doorIndex++;
				//((Door)s).levelFromIndex = i;
				((Door)s).doorFromIndex = doorIndex;
				//doors.add((Door)s);
					
			
			}
		}
}

public void doCollisions()//calculate collisions and set player position
{
	if (user().stance == Player.STANCE_BEING_CARRIED)
		return;//don't reposition or go through doors -- handled elsewhere.
	
	int hits = 0;
	
	CollisionHelper ch = new CollisionHelper(user());
	
	int relativeDoorIndex = -1;
	
	if (pm == null)
		return;
	
	boolean pointZoneCollided = false;
	PointZone pz = null; 
	
	ArrayList<PolyShape> collidedGridShapes = new ArrayList<PolyShape>();
	
	try{
	//populate which grid shapes were collided
	/*if (user().ly > pm.grid.negativeMinimumHeight - 1  && user().ly < pm.grid.negativeMinimumHeight+1+pm.grid.height && user().lx > pm.grid.negativeMinimumWidth-1 && user().lx < pm.grid.negativeMinimumWidth+1+pm.grid.width)
	for (PolyShape p : pm.grid.field[pm.grid.getRow(user().ly)][pm.grid.getCol(user().lx)])
	{
		collidedGridShapes.add(p);
	}
	if (user().y > pm.grid.negativeMinimumHeight - 1  && user().y < pm.grid.negativeMinimumHeight+1+pm.grid.height && user().x > pm.grid.negativeMinimumWidth-1 && user().x < pm.grid.negativeMinimumWidth+1+pm.grid.width)
	for (PolyShape p : pm.grid.field[pm.grid.getRow(user().y)][pm.grid.getCol(user().x)])
	{
		if (!collidedGridShapes.contains(p))
		collidedGridShapes.add(p);
	}*/
	
	/*
	 * rectangle check, lines
	 */
	
	int minRow = pm.grid.getRow(Math.min(user().y,user().ly));
	//System.out.print("minrow="+minRow);
	int maxRow = pm.grid.getRow(Math.max(user().y,user().ly));
	//System.out.print("maxRow="+maxRow);
	int minCol = pm.grid.getCol(Math.min(user().x, user().lx));
	//System.out.print("minCol="+minCol);
	int maxCol = pm.grid.getCol(Math.max(user().x, user().lx));
	//System.out.println("maxCol="+maxCol);
	
	Line2D.Double playerPath = new Line2D.Double(user().lx, user().ly, user().x, user().y);
	
	for (int r = minRow; r <= maxRow; r++)
	{
		if (r < 0 || r > pm.grid.numRows-1)
			continue;
		for (int c = minCol; c <= maxCol; c++)
		{
			if (c < 0 || c > pm.grid.numCols - 1)
				continue;
			
			Rectangle2D.Double rect = new RectGridCell(pm.grid, r, c).bounds;
			//Rectangle2D.Double rect2 = new Rectangle2D.Double(pm.grid.cellWidth*c, pm.grid.cellHeight*r, pm.grid.cellWidth, pm.grid.cellHeight);

			
			
			if (playerPath.intersects(rect))
			{
				for (PolyShape p : pm.grid.field[r][c])
					collidedGridShapes.add(p);
			}
			
		}
	}
	
	
	
	}catch(Exception eee){eee.printStackTrace();};
	
	
	
	boolean collidedWithANonFloor = false;//isnt on ground -- for carrying 
	
	for (PolyShape s : collidedGridShapes)     
	{
		
		if (s instanceof Door)
		{
			relativeDoorIndex = ((Door)s).doorFromIndex;
		}
		if (s instanceof PointZone)
		{
			if (s.collisionShape.contains(new Point((int)user().x,(int)user().y)))
			{
				pointZoneCollided = true;
				pz = (PointZone)s;
			}
			continue;//not solid
		}
		
		
		
		if (s instanceof ServiceLedge && user().role != Player.ROLE_SERVICE)
			continue;//just fall through it
		
		boolean shapeAlreadyAdded = false;
		Line2D.Double playerPath = new Line2D.Double(user().lx,user().ly,user().x,user().y);
		for (CollisionLine c : s.collisionLines)
		{
			Line2D.Double wall = c.wallLine;
			if (playerPath.intersectsLine(wall))
			{
				Collision collision = new Collision();
				
					
				if (s instanceof Platform)
				{
					if (tryToDropDown == true)
					{
						collision.cy = ((Platform)s).bottom;
						collision.iy = -1;
						user().onGround = false;
					}
					else
					{
						user().onGround = true;
						collision.cy = ((Platform)s).top;
						collision.iy = 1;
					}
					collision.cx = user().x;
					user().dy = 0;
					collision.ix = 0;
					collision.type = CollisionLine.TYPE_PLATFORM;
					ch.collisions.add(collision);
					break;
				}
				if (!shapeAlreadyAdded)
				collision.involvedShapes.add(s);
				
				shapeAlreadyAdded = true;
				
				if (s instanceof Door)
				{
					collidedWithANonFloor = true;
					Door d = (Door)(s);
					
					pm = null;
					user().lx = user().x; user().ly = user().y;
					user().llx = user().x; user().lly = user().y;
					out.println("map " + relativeDoorIndex);//request for the map
					
					
					return;
				}
				
				
				
				hits++;
				
				
				
				 switch (c.type)
				{
				
				
				case (CollisionLine.TYPE_HORIZONTAL):
					{
					collision.type = CollisionLine.TYPE_HORIZONTAL;
					boolean falling = user().ly > user().y;
					
					double ix = user().x, iy = wall.y1;
					
					//user().x = ix; user().y = iy;
					collision.cx = ix; collision.cy = iy;
					if (falling)
					{
						//user().y++;
						collision.iy = 1;
						user().onGround = true;
					}
					else
					{
						collision.iy = -1;//user().y--;
						user().dy = 0;//hit a ceiling
						collidedWithANonFloor = true;
					}
					
					}
					ch.collisions.add(collision);
					break;
					
				case (CollisionLine.TYPE_VERTICAL):
					{boolean movingLeft = user().lx > user().x;
					
					double ix = wall.x1, iy = user().y;
					
					//user().x = ix; user().y = iy;
					collision.cx = ix; collision.cy = iy;
					collidedWithANonFloor = true;
					if (movingLeft)
						collision.ix = 1;//user().x++;
					else
						collision.ix = -1;//user().x--;
					}
					ch.collisions.add(collision);
					break;
					
				case (CollisionLine.TYPE_POSITIVE):
				{
					
				collision.involvedShapes.remove(s);
				collision.type = CollisionLine.TYPE_POSITIVE;
					
				boolean movingLeft = user().lx > user().x;
				boolean falling = user().ly > user().y;
				
				boolean justFalling = user().lx == user().x;
				boolean above = falling || (!movingLeft && !justFalling);
				
				
				if (above)//if (justFalling)//solves the undefined issue
				{
					collision.cy = (wall.y2-wall.y1)/(wall.x2-wall.x1) * (user().x - wall.x1) + wall.y1; 
					collision.cx = user().x;
					user().onGround = true;
				}
				else
				{
					collidedWithANonFloor = true;
					collision.cx = user().lx;//collision.cx = (user().y*wall.x2 - wall.y1*wall.x2 - user().y*wall.x1 + wall.y2*wall.x1)/(wall.y2-wall.y1);
					collision.cy = user().ly;
				}
				
				if (above)//always called
				{
					collision.iy = 1;//user().y++;
				}
				
				
				else
				{
					collision.iy = -1;//user().y--;
				}
				
				}
				ch.collisions.add(collision);
				break;
				
				case (CollisionLine.TYPE_NEGATIVE)://mostly copy pasted from type_positive
				{
					
					collision.involvedShapes.remove(s);
					
				collision.type = CollisionLine.TYPE_NEGATIVE;
				
				boolean movingLeft = user().lx > user().x;
				boolean falling = user().ly > user().y;
				
				
				
				boolean justFalling = user().lx == user().x;
				
				boolean above = falling || (movingLeft && !justFalling);
				
				
				if (above)//if (justFalling)//solves the undefined issue
				{
					collision.cy = (wall.y2-wall.y1)/(wall.x2-wall.x1) * (user().x - wall.x1) + wall.y1; 
					collision.cx = user().x;
					user().onGround = true;
				}
				else
				{
					collidedWithANonFloor = true;
					/*collision.cx = (user().y*wall.x2 - wall.y1*wall.x2 - user().y*wall.x1 + wall.y2*wall.x1)/(wall.y2-wall.y1);//changed to fix acute angle jump clip
					collision.cy = user().y;*/
					collision.cx = user().lx;
					collision.cy = user().ly;
					
				}
				
				if (above)//always called
				{
					collision.iy = 1;//user().y++;
				}
				else
				{
					collision.iy = -1;//user().y--;
				}
				
				}
				ch.collisions.add(collision);
				break;
				
				
				case (-555):
					System.out.println("what the?");
					break;
				case (-1):
					System.out.println("Invalid line somehow");
					break;
					
				}
			}
			
			
			
		}
		
	}
	
	
	double xChanged = user().x;
	double yChanged = user().y;
	ch.perform();//magic
	if (user().x != xChanged)
	{
		user().dx = 0;
	}
	else if (pointZoneCollided && user().y == yChanged)
	{
		user().addDamage(pz.pointValue);
		broadcastParticles(Color.red.darker().darker(), 1000, user().x, user().y);
		user().x = pz.respawnX;
		user().y = pz.respawnY;
		user().lx = user().x; user().ly = user().y;user().llx = user().x; user().lly = user().y;
		user().dx = 0;user().dy = 3;
		out.println("throw? " + user().facingLeft);//when the player falls through a portal, if he's carrying someone then he is forced to throw him.
	}
	
	
	
	if (!(user().stance == Player.STANCE_ON_OTHERS_HEAD && collidedWithANonFloor == false))//otherwise do nothing
	{
		user().ly = user().y;user().lx = user().x;
		if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
		{
			setStance(Player.STANCE_DEFAULT);
		}
	}
	
		
	
}

int count = 0;

public void broadcastParticles(Color c, int amount, double x, double y)
{
	out.println("particles " + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + " " + amount + " " + (int)x + " " + (int)y);
}

public void addParticles(Color c, int amount, double x, double y)
{
	for (int i = 0; i < amount; i++)
	{
	double angle = 2*Math.PI*Math.random();
	
	double dx = Math.cos(angle) * 70;
	double dy = Math.sin(angle) * 70;
	long initialLife = 3000L;
	double ddx = -15*ParticleEffect.RP();
	double ddy = 31*Math.random() + 15;
	
	ParticleEffect p = new ParticleEffect(x,y,dx,dy,ddx,ddy,initialLife);
	p.color = c;
	particles.add(p);
	}
}

public void applyPhysics(double multiplier)
{
	doCollisions();//so floor and wall combo's work, call it twice
	
	if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
		return;
	
	if (user().onGround)
	{
		user().dx = 0;
	}
	
	
	user().x += user().dx;
	
	user().y += user().dy*multiplier;
	user().dy += Mob.ddy*multiplier;
	
	if (user().dy < -47.5/2)
	{
		user().dy = -47.5/2;
	}
	
	
	
}


public int hShift=0, vShift=0;

public void paint(long timePassed)
{
	try{
g.setPaint(new Color(43,15,1));g.fillRect(0,0,getSize().width,getSize().height);//background

this.setShift();
this.drawMap();
this.drawPlayers();
this.drawParticles();
this.drawAnnouncements();
this.drawGUI(timePassed);

g2.drawImage(bi, null, 0, 0);
	}catch(ConcurrentModificationException e){};
}


public void drawParticles()
{
	try{
	for (int i = 0; i < particles.size(); i++)
	{
		ParticleEffect p = particles.get(i);
		p.draw(g, rw, rh, getSize().height, hShift, vShift);
	}
	}catch(NullPointerException e){
	particles = new ArrayList<ParticleEffect>();
	};
}

public void drawAnnouncements()
{
	g.setFont(new Font("Times New Roman", Font.BOLD, (int)(75*rw)));
	
	
	int x = 0;//getSize().width/2;
	int y = (int)(100*rh);
	
	//draw grabString bigger
		if (!user().grabString.equals("null"))//draw user's grabString if he's being carried
		{
			g.setPaint(Color.WHITE);
			g.drawString(user().grabString.substring(0,1),0,y);
			g.setPaint(Color.BLUE.darker());
			g.drawString(user().grabString.substring(1), (int)(75*rw/2), y);
		}
		if (user().otherPlayerID != -1 && !players.get(user().otherPlayerID).grabString.equals("null"))//draw grabString on carrier's screen
		{
			String grabString = players.get(user().otherPlayerID).grabString;
			g.setPaint(Color.WHITE);
			g.drawString(grabString.substring(0,1),0,y);
			g.setPaint(Color.BLUE.darker());
			g.drawString(grabString.substring(1), (int)(75*rw/2), y);
		}
		
		
		if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
		{
			g.setPaint(new Color(49,49,49));
			
			y = getSize().height/2 - (int)(100*rh);
			
			x = getSize().width/2-(int)(250*rw);
			
			g.drawString("Press F to pick up", x, y);
		}
		
		if (user().stance == Player.STANCE_CARRYING_OTHER)
		{
			g.setPaint(new Color(49,49,49));
			
			y = getSize().height/2 - (int)(100*rh);
			
			x = getSize().width/2-(int)(250*rw);
			
			g.drawString("Press T to throw", x, y);	
		}
		
	
}

public long chatLogOpaqueTimeLeft = 5000L;
public ArrayList<String> chatLog = new ArrayList<String>();
public String currentWord = "";
public int beginLogIndex = 0;

private void makeChatLogOpaqueAgain()
{
	chatLogOpaqueTimeLeft = 5000L;
}

public void drawGUI(long timePassed)
{
	chatLogOpaqueTimeLeft -= timePassed;
	//draw chat log
	//
	boolean opaque = (chatLogOpaqueTimeLeft > 0) || (keyboardMode == KEYBOARD_CHAT_MODE);
	if (opaque)
	g.setPaint(new Color(0.25f, 0.80f, 0.80f, 0.25f));
	else
	g.setPaint(new Color(0.25f, 0.80f, 0.80f, 0.05f));
	Rectangle log = new Rectangle((int)(25*rw),(int)(25*rh),(int)(350*rw),(int)(200*rh));
	g.fill(log);
	Color fontColor = null;
	if (opaque)
	fontColor = Color.CYAN;
	else
		fontColor = new Color(0, 1f, 1f, 0.10f);
	g.setPaint(fontColor);
	
	int numLines = 10;
	int fontSize = (int)((200*rh)/numLines);
	g.setFont(new Font("Times New Roman", Font.BOLD, fontSize));
	
	
	
	for (int i = beginLogIndex; i < beginLogIndex + numLines; i++)
	{
		if (i < chatLog.size())
		g.drawString(chatLog.get(i), log.x, log.y + fontSize + fontSize*(i-beginLogIndex));	
	}
	
	if (keyboardMode == KEYBOARD_CHAT_MODE)//user is typing something
	{
	g.setPaint(new Color(212,175,55,102));//gold
	g.fillRect(log.x, log.y+log.height, log.width, fontSize);
	g.setPaint(fontColor);
	if (currentWord == null || currentWord.equals(""))
		g.drawString("<Text Message>",log.x, log.y+log.height+fontSize);
	else
		g.drawString(currentWord, log.x, log.y+log.height+fontSize);
	}
	
	
	
	
	
	
	
	
}

public void setShift()//sets the map's shift -- moves the camera around (to follow the player)
{
	if (pm == null)
		return;
	int y = (int)user().y;
	if (y > pm.minHeight + (normalHeight/2) && y < pm.maxHeight - (normalHeight/2))
	{
		vShift = y - (int)(normalHeight/2);
	}
	else if (y < pm.minHeight + normalHeight/2)
		{
		vShift = pm.minHeight;
		}
	else if (y > pm.maxHeight - normalHeight/2)
		vShift = pm.maxHeight - (int)normalHeight;
	
	int x = (int)user().x;
	
	if (x > pm.minWidth + (normalWidth/2) && x < pm.maxWidth - (normalWidth/2))
	{
		hShift = x - (int)(normalWidth/2);
	}
	else if (x < pm.minWidth + normalWidth/2)
		{
		hShift = pm.minWidth;
		}
	else if (x > pm.maxWidth - normalWidth/2)
		hShift = pm.maxWidth - (int)normalWidth;
	
	
}

public void drawMap()
{
	if (pm == null)
		return;
	pm.defineDrawableShapes(rw, rh, getSize().height, hShift, vShift);
	for (PolyShape s : pm.shapes)
	{
		if (!(s instanceof ServiceLedge && user().role != Player.ROLE_SERVICE))
		s.draw(g, rw, rh, getSize().height, hShift, vShift);
	}
	
	
	
	
	
}


public void drawPlayers()
{
	
	try{
		for (Integer i : players.keySet())	
		{
			Player p = players.get(i);
			
			int x = (int)p.x-16;//left side of player square
			int y = (int)p.y+15+16;//top side of player square
			
			
			
			//draw swords
			if (p.swordSwing != -1)
			{
				
				double r = 100;
				int swordSwing = p.swordSwing;
				if (!p.facingLeft)
				{
					swordSwing = 180 - swordSwing;
				}
				int[] xs = {(int)(p.x+r*Math.cos(Math.toRadians(swordSwing+5))),(int)(p.x+r*Math.cos(Math.toRadians(swordSwing-5))),(int)p.x};
				int[] ys = {(int)(p.y+16+r*Math.sin(Math.toRadians(swordSwing+5))),(int)(p.y+16+r*Math.sin(Math.toRadians(swordSwing-5))),(int)p.y+16};
				
				PolyShape poly = new PolyShape(xs,ys);
				poly.solid = false;
				poly.color = Color.black;
				poly.defineDrawableShape(rw,rh,getSize().height,hShift,vShift);
				g.setColor(poly.color);
				g.fill(poly.drawableShape);
				g.setColor(Color.GRAY);
				g.draw(poly.drawableShape);
			}
			
			//draw health bars
			int extstretch = 2;
			int extfloat = 5;
			int width = 32+2*extstretch;
			int height = 8;
			//draw full-part
			g.setPaint(new Color(136,202,0));
			this.fillRect(x-extstretch,y+extfloat*2,(int)(width*((double)p.points/p.maxPoints)),height);
			//draw empty-part
			g.setPaint(new Color(200,13,0));
			int xstart = (int)(width*((double)p.points/p.maxPoints));
			this.fillRect(x-extstretch+xstart, y+extfloat*2, width-xstart, height);
			//draw shields over health-bars
			g.setPaint(new Color(54, 46, 163, 200));
			this.fillRect(x-extstretch,y+extfloat*2,(int)(width*((double)p.shieldPoints/p.maxShieldPoints)),height);
			
			
		}
			}catch(ConcurrentModificationException e){};

	
	try{
for (Integer i : players.keySet())	
{
	Player p = players.get(i);
	if (p.facingLeft())
		ImageHelper.draw(resizedMobImagesLeft.get(ImageHelper.IMAGE_PLAYER_DEFAULT), (int)p.x, (int)p.y, g, rw, rh, getSize().height, hShift, vShift);
	else
		ImageHelper.draw(resizedMobImagesRight.get(ImageHelper.IMAGE_PLAYER_DEFAULT), (int)p.x, (int)p.y, g, rw, rh, getSize().height, hShift, vShift);
}
	}catch(ConcurrentModificationException e){};


if (pm == null)
	return;

try{
for (Integer i : pm.mobs.keySet())
{
	Mob m = pm.mobs.get(i);
	if (m.facingLeft())
	ImageHelper.draw(resizedMobImagesLeft.get(m.getImageIndex()), (int)m.x, (int)m.y, g, rw, rh, getSize().height, hShift, vShift);
	else
	ImageHelper.draw(resizedMobImagesRight.get(m.getImageIndex()), (int)m.x, (int)m.y, g, rw, rh, getSize().height, hShift, vShift);
}
}catch(Exception e){};//nullpointer or concurrent





}

double normalWidth = 800, normalHeight = 600;
double rh, rw;
public void fillRect(int x, int y, int w, int h)
{
	//g.fillRect(x,getSize().height-y,w,h);
	fillScaledRect(x, y, w, h);
}
public void fillScaledRect(int x, int y, int w, int h)
{
	
	g.fillRect((int)((x-hShift)*rw),getSize().height-(int)((y-vShift)*rh),(int)(rw*w),(int)(rh*h));
}

public void drawRect(int x, int y, int w, int h)
{
	drawScaledRect(x, y, w, h);
}
public void drawScaledRect(int x, int y, int w, int h)
{
	
	g.drawRect((int)((x-hShift)*rw),getSize().height-(int)((y-vShift)*rh),(int)(rw*w),(int)(rh*h));
}


public static int KEYBOARD_DEFAULT_MODE = 0;
public static int KEYBOARD_CHAT_MODE = 1;
public int keyboardMode = KEYBOARD_DEFAULT_MODE;
public boolean tryToDropDown = false;

private void handleKeyPresses(double multiplier)
{
	double speed = 10*multiplier;
	
	/*
	 * swingSword!
	 */
	if (user().swordSwing == 200)
		user().swordSwing = -1;
	
	if (user().swordSwing != -1)
	{
		
	double swordIncrement = 17*multiplier;
	user().swordSwing += swordIncrement;
	if (user().swordSwing > 200)
		user().swordSwing = 200;
	
	
	
	}
	//end swingSword
	
	ø+=5;
	
	if (k.f("enter"))
	{
		if (keyboardMode == KEYBOARD_DEFAULT_MODE)
		{
			keyboardMode = KEYBOARD_CHAT_MODE;
			currentWord = "";
		}
		else if (keyboardMode == KEYBOARD_CHAT_MODE)
		{
			keyboardMode = KEYBOARD_DEFAULT_MODE;
			if (currentWord != null && !currentWord.equals(""))
				out.println("chat " + user + "*" + currentWord);
			currentWord = "";
			makeChatLogOpaqueAgain();
		}
	}
	if (keyboardMode == KEYBOARD_CHAT_MODE)
	{
		if (k.f("space"))
			currentWord += " ";
		if (k.f("backspace"))
		{
			if (currentWord.length() > 0)
			currentWord = currentWord.substring(0, currentWord.length()-1);
		}
		
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890.";
		String key = "";
		for (int i = 0; i < alphabet.length(); i++)
		{
			if (k.f(""+alphabet.charAt(i)))
				{
				key = ""+alphabet.charAt(i);
				break;
				}
		}
		if (k.k("shift") || Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK))
			key = key.toUpperCase();
		
		currentWord += key;
	}
	if (k.t("escape"))
	{
		if (keyboardMode == KEYBOARD_CHAT_MODE)
		{
			keyboardMode = KEYBOARD_DEFAULT_MODE;
			currentWord = "";
			makeChatLogOpaqueAgain();
		}
	}
	
	if (keyboardMode == KEYBOARD_CHAT_MODE)
	{
		if (k.f("up"))
		{
			beginLogIndex--;
			if (beginLogIndex < 0)
				beginLogIndex = 0;
		}
		if (k.f("down"))
		{
			beginLogIndex++;
			if (beginLogIndex > chatLog.size()-10 && chatLog.size() >= 10)
				beginLogIndex = chatLog.size()-10;
			else if (chatLog.size() < 10)
				beginLogIndex = 0;
		}
	}
	
	if (user().stance == Player.STANCE_BEING_CARRIED)//keyboard sequence
	{
		if (k.f(user().grabString.substring(0,1)))
		{
			if (user().grabString.length() == 1)
			{
				user().grabString = "null";
				setStance(Player.STANCE_DEFAULT);
				out.println("breakfree " + user().otherPlayerID);
			}
			else
			{
			user().grabString = user().grabString.substring(1);
			}
		}
		
		
	}
	
	
	if (keyboardMode == KEYBOARD_DEFAULT_MODE)
	{
	
	if (k.f("r"))
	{
		swingBat();
	}
	
	if (k.f("f"))//try to pick up
	{
		if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
		{
			out.println("pickup?");
		}
	}
	
	if (k.f("t"))//throw
	{
		if (user().stance == Player.STANCE_CARRYING_OTHER)
		{
			System.out.println("throw1");
			out.println("throw? " + user().facingLeft);
		}
	}
	
	if (k.k("s") || k.k("down"))//drop down a platform
	{
		tryToDropDown = true;
	}
	
	if (user().onGround && (k.f("space") || k.f("up")) )
	{
		user().dx = 0;
		
		user().onGround = false;
		user().dy = 24;
		if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
			setStance(Player.STANCE_DEFAULT);
	}
	if (user().dx == 0 && k.k("a") || k.k("left"))
	{
		
		user().facingLeft = true;
		if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
			setStance(Player.STANCE_DEFAULT);
		
		if (user().stance != Player.STANCE_BEING_CARRIED)
			user().x -= speed;
	}
	if (user().dx == 0 && k.k("d") || k.k("right"))
	{
		user().facingLeft = false;
		if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
			setStance(Player.STANCE_DEFAULT);
		
		if (user().stance != Player.STANCE_BEING_CARRIED)
		user().x += speed;
	}
	
	}
	
	k.untype();

}

private void setStance(int stance)
{
	user().stance = stance;
	
	if (stance != Player.STANCE_BEING_CARRIED)
	{
		user().grabString = "null";
	}
	
	
}

double ø = 0;
public void swingSword()
{
	double r = 100;
	int[] xs = {(int)(user().x+r*Math.cos(Math.toRadians(ø+15))),(int)(user().x+r*Math.cos(Math.toRadians(ø-15))),(int)user().x};
	int[] ys = {(int)(user().y+r*Math.sin(Math.toRadians(ø+15))),(int)(user().y+r*Math.sin(Math.toRadians(ø-15))),(int)user().y};
	
	PolyShape p = new PolyShape(xs,ys);
	p.solid = false;
	p.color = Color.black;
	p.defineDrawableShape(rw,rh,getSize().height,hShift,vShift);
	
	pm.shapes.add(p);
	
	
}


public void keyPressed(KeyEvent e)
{

String key = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
k.keyPress(key);
}
public void keyReleased(KeyEvent e)
{
k.keyRelease(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase());

	

}

public void keyTyped(KeyEvent e){}



public static void checkIfImagesExist()
{
	try{
		ArrayList<String> fileStrings = new ArrayList<String>();
		fileStrings.add("bunny");
		fileStrings.add("carrot");
		fileStrings.add("bloop");
		fileStrings.add("zomboo");
		for (String s : fileStrings)
		{
		
	if (!new File(s + ".png").isFile())
	{
		BufferedImage pic = ImageIO.read(new URL("https://dl.dropboxusercontent.com/u/51610798/" + s + ".png"));
		ImageHelper.saveImageToFile(s + ".png", pic);
		System.out.println("Creating " + s + ".png");
	}
		}
	
	}catch(Exception e){e.printStackTrace();};
	
}

public void addToChatLog(String text)
{
	if (chatLog.size() > 10-1)
	{
	if (beginLogIndex == chatLog.size()-10)
		beginLogIndex++;
	}
chatLog.add(text);
makeChatLogOpaqueAgain();
}


public static void main(String[]args)
{
checkIfImagesExist();
defineMobImages(mobImages);
	
try{
PlatipusClient b;
if (args.length == 1)
	b = new PlatipusClient(args[0]);
else
	b = new PlatipusClient();

b.begin();
}catch(Exception e){e.printStackTrace();System.exit(-1);}
}



public void execute(String s)//Command from server, associated with "private Runnable receivedWriter()"
{
	
	if (s.startsWith("npc "))//massive mob update string for player's level
	{
		try{
		if (pm == null)
			return;
		
		s = s.substring(4);
		
		Scanner scan = new Scanner(s).useDelimiter("[+]");
		
		while(scan.hasNext())
		{
			
			String str = scan.next();
			
			String[] spl = str.split(" ");
			
			
			int mobID = pint(spl[0]);
			Mob m = pm.mobs.get(mobID);
			
			
			m.x = pint(spl[1]);
			m.y = pint(spl[2]);
			
			m.setFacingLeft(Compactor.booleanFromDigit(pint(spl[4])));
			
			//mob intersects with a player
			m.playerIntersected = pint(spl[3]);
			int ID = m.playerIntersected;
			if (ID != -1)//-1 means no player intersected
			{
				m.onPlayerCollideClient(players.get(ID));
				
				boolean flyLeft = (m.x < user().x);
				for (int i = 0; i < 117; i++)
				{
					int pol = -1;
					if (!flyLeft)
						pol = 1;
					pol*=-1;
					
					
					double dx = ParticleEffect.negative(0.15)*pol*(Math.random()*70+20);
					double dy = (Math.random()*50);
					long initialLife = 1500L;
					double ddx = -15*pol;
					double ddy = 31;
					
					double x = players.get(ID).x;
					double y = players.get(ID).y;
					
					Color color = players.get(ID).color.brighter().brighter();
					
					ParticleEffect p = new ParticleEffect(x,y,dx,dy,ddx,ddy,initialLife);
					p.color = color;
					particles.add(p);
					
					
					
				}
			}
			
			
			
			
			
			m.definePolyShape(m.x,m.y);
			
			
			
		}
		}catch(Exception e){};//sometimes the player is picked up to another map and the array becomes null and threw errors
		
		
		return;
	}
	else if (s.startsWith("removemob"))
	{
		
		String[] split = s.split(" ");
		int lvl = pint(split[1]);
		int mob = pint(split[2]);
		
		if (lvl == user().levelIndex)
		{
			if (pm != null)
			pm.mobs.remove(mob);
		}
	}
	if (s.startsWith("map "))
	{
		//map ø ù [mapdata]
		//the map being literally the string "map".
		//the ø being the door index on the new map
		//the ù being the level index of the new map
		//the [mapdata] being the "[+]" delimited map constructor data.
		
		String[] split = s.substring(0,15).split(" ");
		
		pm = new PlatipusMap(true, s.substring(s.indexOf("*")));
		
		if (pm.name.equals("saferoom.map"))
		{
			addToChatLog("--safe room--");
		}
		populateDoors();
		pm.populateGrid();
		System.out.println(pm.grid);
		//Point p = pm.doors.get((int)s.charAt(4)).spawnLocation();//for where to place the player upon entering door
		
		if (pint(split[1]) != -1)//-1 means the player was picked up, bringing him to this map.
		{
		Point p = pm.doors.get(pint(split[1])).spawnLocation();
		
		user().x = p.x; user().lx = p.x;
		user().y = p.y; user().ly = p.y;
		}
		else
			System.out.println("player " + user + " picked up to level " + pint(split[2]));
		
		user().levelIndex = pint(split[2]);
		
		
		return;
	}
	String[] z = s.split(" ");
	
	if (z[0].equals("particles"))//add particles at specified location and amount
	{
		//particles r g b amount x y
		Color c = new Color(pint(z[1]),pint(z[2]),pint(z[3])); int amount = pint(z[4]); int x = pint(z[5]); int y = pint(z[6]);
		this.addParticles(c, amount, x, y);
		
	}
	
	if (z[0].equals("new"))
	{
		
		if (z[1].equals("player"))
		{
			int ID = pint(z[2]);
			if (!players.containsKey(ID))
			{
				Player p = new Player(s);
				if (p.ID == user)
					return;
				players.put(p.ID,p);
				return;
			}
			else
			{
				if (ID != user)
				players.get(ID).updatePlayer(s);
				
			}
			
			
		}
		if (z[1].equals("user"))
		{
			Player p = new Player(s.replace("user", "player"));
			players.put(p.ID, p);
			user = p.ID;
			
			return;
		}
		
		
		return;
	}//end z[0].equals("new")
	
	if (z[0].equals("mobsmash"))
	{
		int smasher = pint(z[1]);
		int mobSmashedID = pint(z[2]);
		
		try{
		//draw particles for attack
		if (players.get(smasher).levelIndex == user().levelIndex)
		{
			Mob m = pm.mobs.get(mobSmashedID);
			
			boolean flyLeft = !(m.x < players.get(smasher).x);
			for (int i = 0; i < 117; i++)
			{
				int pol = -1;
				if (!flyLeft)
					pol = 1;
				pol*=-1;
				
				double dx = ParticleEffect.negative(0.15)*pol*(Math.random()*70+20);
				double dy = (Math.random()*50);
				long initialLife = 1500L;
				double ddx = -15*pol;
				double ddy = 31;
				
				double x = m.x;
				double y = m.y;
				
				Color color = m.p.color.brighter().brighter();
				
				ParticleEffect p = new ParticleEffect(x,y,dx,dy,ddx,ddy,initialLife);
				p.color = color;
				particles.add(p);
				
				
				
			}
			
			
		}
		}catch(Exception e){};//pokemon catch -- was getting NullPointer for nonexistant mob. it's just particles.
	}
	
	if (z[0].equals("stance"))
	{
		if (z.length > 2)
		{
		int otherPlayerID = pint(z[2]);
		user().otherPlayerID = otherPlayerID;
		}
		setStance(pint(z[1]));
		if (user().stance == Player.STANCE_ON_OTHERS_HEAD)
			{
			user().dy = 0;
			user().onGround = true;
			}
		if (user().stance == Player.STANCE_CARRYING_OTHER)
		{
			
			
			
		}
		if (user().stance == Player.STANCE_BEING_CARRIED)
		{
			user().grabString = this.grabString();
			
			
		}
		
		return;
	}//end z[0].equals("top")
	
	if (z[0].equals("smash"))//got hit by a sword swing
	{
		int smashee = pint(z[1]);
		int smashedBy = pint(z[2]);
		
		boolean flyLeft = (players.get(smashedBy).x < players.get(smashee).x);
		
		//add new particle effects
		for (int i = 0; i < 117; i++)
		{
			int pol = -1;
			if (!flyLeft)
				pol = 1;
			pol*=-1;
			
			double dx = ParticleEffect.negative(0.15)*pol*(Math.random()*70+20);
			double dy = (Math.random()*50);
			long initialLife = 1500L;
			double ddx = -15*pol;//double ddx = -(dx/((double)initialLife));
			double ddy = 31;//double ddy = -(dx/((double)initialLife));
			
			double x = players.get(smashee).x;
			double y = players.get(smashee).y;
			
			//Color color = new Color(167,252,0);//Spring Bud (green)
			Color color = players.get(smashee).color.brighter().brighter();
			
			ParticleEffect p = new ParticleEffect(x,y,dx,dy,ddx,ddy,initialLife);
			p.color = color;
			particles.add(p);
		}
		
		
		
		if (smashee == user)
		{
		user().dy = 20;
		
		if (flyLeft)
			user().dx = 12;
		else
			user().dx = -12;
		
		setStance(Player.STANCE_DEFAULT);
		user().onGround = false;
		}
		
		return;
	}
	
	if (z[0].equals("throw?"))
	{
		boolean left = Boolean.parseBoolean(z[1]);
		
		if (playerInsideAWall(user()))
		{
		Player other = players.get(user().otherPlayerID);
		user().x = other.x;
		user().y = other.y;
		}
		
		
		user().dy = 20;
		
		if (left)
		user().dx = -24;
		else
		user().dx = 24;
		
		setStance(Player.STANCE_DEFAULT);
		user().onGround = false;
		return;
	}
	if (z[0].equals("chat"))
	{
		String text = "";
		int playerID = -539;
		try{
		playerID = pint(s.substring(s.indexOf(" ")+1,s.indexOf("*")));
		}catch(Exception e){};
		if (playerID==-1)
			text = "[server]:";
		else if (playerID != -539)
			text = playerID + ":";
		if (playerID != -539)
		text += s.substring(s.indexOf("*") + 1);
		else
			text += s.substring(s.indexOf(" ") + 1);
		if (chatLog.size() > 10-1)
			{
			if (beginLogIndex == chatLog.size()-10)
				beginLogIndex++;
			}
		chatLog.add(text);
		makeChatLogOpaqueAgain();
	}
	



if (z[0].equals("set"))
{
if (z[1].equals("player"))
{
	int id = pint(z[2]);
	System.out.println("the id is " + id);
	players.get(id).set(s);
	return;
}
return;
	
}
if (z[0].equals("stop"))
{
	if (z[1].equals("client"))
	{
		runTheGame = false;
	}
}





}

public boolean playerInsideAWall(Player p)
{
	for (PolyShape poly : pm.shapes)
	{
		if (poly.solid == false)
			continue;
		if (poly.collisionShape.contains(p.x,p.y))
		{
			return true;
		}
	}
	
	return false;
	
	
	
}





class ClientGUI
{
private final PlatipusClient plat;
private JFrame frame;
private JTextArea console;
private JScrollPane jsp;
private final JTextField IPInput;

public ClientGUI(final PlatipusClient plat)
{
this.plat = plat;
frame = new JFrame("~Panicards Client Menu~");
 
if (editorTestRun == false)
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setSize(800,600);
frame.setLocation(200,50);
frame.setResizable(false);
frame.setLayout(new FlowLayout());

console = new JTextArea();
console.setFocusable(false);

jsp = new JScrollPane(console);
jsp.setPreferredSize(new Dimension(800-14, 500-21));
frame.add(jsp);

IPInput = new JTextField();
IPInput.setBackground(new Color(0.025f,0.025f,0.025f).darker());
IPInput.setForeground(new Color(0.4862f, 0.1882f, 0.1882f).brighter());
IPInput.setFont(new Font("DejaVu Sans", Font.BOLD, 26));
IPInput.setPreferredSize(new Dimension(800-14, 100-21));
IPInput.setText("98.201.3.67");

IPInput.addActionListener(
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)//on the Enter keypress
			{
			String text = e.getActionCommand().trim();
			if (text.equals(""))
				return;
			
			if (plat.connect(text, 8123))
			{
				console.append("Successfully connected to " + text + "\r\n");
				
			}
			else
				console.append("Couldn't connect to " + text + "\r\n");
			
			IPInput.setText("");
			
			}
			
			
		}
				);
frame.add(IPInput);


//chocolate sundae color scheme from notepad++
//43,15,1 background
//188,187,128 text
 
console.setBackground(new Color(0.1686274509803922f,0.0588235294117647f, 0.003921568627451f));
console.setForeground(new Color(0.7372549019607843f,0.7333333333333333f, 0.5019607843137255f));
console.setFont(new Font("Times New Roman", Font.BOLD, 40));

console.append("Enter an IP address to connect to on port 8123\r\n");

frame.setVisible(true);
IPInput.requestFocus();
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








private Runnable receivedWriter()//receives Strings from server, and passes them into public void execute(String command)
{
return new Runnable() {
public void run() {
try{
String received;
while(true)
{
received = in.readLine();
execute(received);
}
}catch(Exception e){
	e.printStackTrace();System.exit(-1);};


}};
}




public void mouseEntered(MouseEvent e){}public void mouseExited(MouseEvent e){}


}