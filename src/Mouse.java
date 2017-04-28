import java.awt.Point;

class Mouse
{
//public String[] keyNames = "w a s d space z x c up down".split(" ");
public static final int LEFT_CLICK = 1, RIGHT_CLICK = 3;
public int[] keyNames = {0, 1, 3, 5, 4, 2};
public boolean[] keyDown;
public boolean[] keyTyped;
public boolean[] uniqueKeyDown;
public boolean[] uniqueReady;

public int mouseX, mouseY;
public int snappedX, snappedY;
public void setLocation(int x, int y)
{
	mouseX = x; mouseY = y;
}
public Point getLocation()
{
	return new Point(mouseX,mouseY);
}
public Point getScaledLocation(double rw, double rh, int h, int hShift, int vShift)
{
	return new Point((int)(mouseX/rw)+hShift, h - (int)(mouseY/rh)+vShift);
}
public Point snapToGrid(double x, double y, double width, double height, double grid)
{
	int outX = (int)round(x, width/grid);
	int outY = (int)round(y, height/grid);
	return new Point(outX, outY);
}
public double round(double x, double increment)
{
	boolean roundUp = (x%increment > increment/2);
	
	x -= x%increment;
	if (roundUp)
		x += increment;
	
	return x;
	
}
public Mouse()
{
keyDown = new boolean[keyNames.length];
keyTyped = new boolean[keyNames.length];

uniqueKeyDown = new boolean[keyNames.length];
uniqueReady = new boolean[keyNames.length];

for (int i = 0; i < uniqueReady.length; i++) uniqueReady[i]=true;
}

public void keyPress(int key)
{
for (int i = 0; i < keyNames.length; i++)
{
if (keyNames[i] == key)
{
keyDown[i] = true;

if (uniqueReady[i])
{
    uniqueKeyDown[i] = true;
    uniqueReady[i] = false;
}
}

}
}
public void keyRelease(int key)
{
for (int i = 0; i < keyNames.length; i++)
if (keyNames[i] == key)
{
keyDown[i] = false;
keyTyped[i] = true;
uniqueReady[i] = true;
}
}

public void untype()//must be called at the end of the code handling mouse presses.
{
    for (int i = 0; i < keyTyped.length; i++)
    {
        keyTyped[i] = false;
        uniqueKeyDown[i] = false;
    }
}

public boolean t(int key)//key typed
{
	for (int i = 0; i < keyTyped.length; i++)
		if (keyNames[i] == key)
			return keyTyped[i];
	
	return false;
}

public boolean k(int key)//key down / key press
{
for (int i = 0; i < keyNames.length; i++)
if (keyNames[i] == key)
return keyDown[i];

return false;
}

public boolean f(int key)//f is "first key down" for unique keydown
{
    for (int i = 0; i < keyNames.length; i++)
        if (keyNames[i] == key)	
        return uniqueKeyDown[i];
    
        return false;
}



}