//platipus player
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.TreeMap;
public class Player implements Collidable //the Player object is created on both the Client and the Server, but the clientdata object is only created on the server.
{

public double x, y;
public double lx, ly;
public double llx, lly;
public int role = 0;
public static final Color SHIELD_BLUE = new Color(54,46,163);
public static final int ROLE_DEFAULT = 0, ROLE_SERVICE = 1;
//role_service is the guy that climbs the elevator and stops people from going up
public BufferedImage picture;
public BufferedImage getPicture()
{
	return picture;
}
public void setPicture(BufferedImage bi)
{
	picture = bi;
}
public String grabString = "null";

public int points = 2;
public int maxPoints = 2;
public double shieldPoints = 1;
public double maxShieldPoints = 1;
public long shieldRechargeTime = 5*1000;
public long timeSinceLastShieldRecharge = 0;
public double shieldRechargeRate = 0.025D;



public double dy = 0;//dy is used for jumps and pushbacks  (client side)

public double dx = 0;

public boolean collidable = true;
public int damageTakenThisLoop = 0;
public void addDamage(int amount)
{
	damageTakenThisLoop+=amount;
}


public boolean onGround; // (client side)

public Color color;//based on IP address

public String IP, mapName;//these are set when constructed

public int ID=-555;

public String destinationInfo;

public int levelIndex=-1;//used on server
public int clientArrayIndex=-1;//used on server

public static final int STANCE_DEFAULT = 0, STANCE_ON_OTHERS_HEAD = 1, STANCE_BEING_CARRIED = 2, STANCE_CARRYING_OTHER = 3;
public int stance;
public int otherPlayerID = -1;//the other player involved in the stance!

public void setFacingLeft(boolean b)//unused for Player ATM
{
	this.facingLeft = b;
}
boolean facingLeft = false;
public boolean facingLeft()
{
	return this.facingLeft;
}

public int swordSwing = -1;

public boolean onGround()
{
	return onGround;
}
public void setOnGround(boolean yes)
{
	onGround = yes;
}

public Player(String IP, String mapName)
{
x = 690; y = 420;
this.IP=IP;this.mapName=mapName;
int r = Integer.parseInt(IP.substring(1,IP.indexOf('.')));// "/127.0.0.1:52379"
System.out.println(IP);
///25.14.50.237:52494
String s2 = IP.substring(1,IP.indexOf('.'));//s2 = 14
//System.out.println("s2 = " + s2);
String xx = IP.substring(IP.indexOf('.'));
//System.out.println("xx = " + xx);
String x2 = xx.substring(1);
//System.out.println("x2 = " + x2);

String x3 = x2.substring(x2.indexOf('.'));
//System.out.println("x3 = " + x3);
String x4 = x3.substring(1);
//System.out.println("x4 = " + x4);
String x5 = x4.substring(0, x4.indexOf('.'));

int g = pint(x2.substring(0,x2.indexOf('.')));


int b = pint(x5);

color = new Color(r, g, b);
}

String name;

public Player()
{
	
}

public void set(String s)
{
	String[] z = s.split(" ");
	//broadcast("set player >ID< x " + clients.get(i).player.x);
	//			  0    1     2   3     4
	String var = z[3];
	int val = pint(z[4]);
	if (var.equals("x"))
		x = val;
	
}

public Player(String define)//same as toString
{
	String[] d = define.split(" ");
	ID = pint(d[2]);
	
	color = new Color(pint(d[3]), pint(d[4]), pint(d[5]));
	
	x = pint(d[6]);
	y = pint(d[7]);
	IP = d[8];
	mapName = d[9];
	
	levelIndex = pint(d[10]);
	
	clientArrayIndex = pint(d[11]);//used on the server
	
	stance = pint(d[12]);
	otherPlayerID = pint(d[13]);
	
	facingLeft = Boolean.parseBoolean(d[14]);
	
	swordSwing = pint(d[15]);
	
	points = pint(d[16]);
	
	grabString = d[17];
	
	role = pint(d[18]);
	
	shieldPoints = pint(d[19]);
	
}
public static int pint(String is)
{
	return Integer.parseInt(is);
}


public String toString()//sending data over socket
{
	String out = "";
	out += "new player " + ID + " " + color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " " + 
			(int)x + " " + (int)y + " " + IP + " " + mapName + " " + levelIndex + " " + clientArrayIndex + 
			" " + stance + " " + otherPlayerID + " " + facingLeft + " " + swordSwing + " " + points + " " + grabString + " " + role + " " + (int)shieldPoints;
	
	return out;
	
	
}

public String toServerGUIString()//for server console
{
	String out = "";
	String s = ", ";
	out += "ID="+ID+s+"map="+levelIndex+s+"role="+role;
	return out;
}

public void updatePlayer(String constructor)
{
	String[] d = constructor.split(" ");
	ID = pint(d[2]);
	
	color = new Color(pint(d[3]), pint(d[4]), pint(d[5]));
	
	x = pint(d[6]);
	y = pint(d[7]);
	IP = d[8];
	mapName = d[9];
	
	levelIndex = pint(d[10]);
	
	clientArrayIndex = pint(d[11]);//used on the server
	
	stance = pint(d[12]);
	otherPlayerID = pint(d[13]);
	
	facingLeft = Boolean.parseBoolean(d[14]);
	
	swordSwing = pint(d[15]);
	
	points = pint(d[16]);
	
	grabString = d[17];
	
	role = pint(d[18]);
	
	shieldPoints = pint(d[19]);
}




public double getX() {
	return x;
}


public double getY() {
	return y;
}


public void setX(double x) {
	this.x=x;
	
}


public void setY(double y) {
	this.y=y;
	
}


public void setDX(double dx) {
	this.dx=dx;
	
}


public void setDY(double dy) {
	this.dy = dy;
	
}

public double getLLY()
{
	return lly;
}
public double getLLX()
{
	return llx;
}
public double getLX()
{
	return lx;
}
public double getLY()
{
	return ly;
}

public void setLLX(double llx) 
{
	this.llx = llx;
	
}

public void setLLY(double lly)
{
	this.lly = lly;
	
}

public void setLX(double lx)
{
	this.lx = lx;
	
}

public void setLY(double ly)
{
	this.ly = ly;
	
}

public double getDY() 
{
	return dy;
}

public double getDX()
{
	return dx;
}



}