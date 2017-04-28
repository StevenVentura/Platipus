class Keyboard
{
private String[] keyNames = "w a s d q e f t r c z x space up down left right enter shift escape".split(" ");//default. can be changed with "setKeyNames"
public boolean[] keyDown;
public boolean[] keyTyped;
public boolean[] uniqueKeyDown;
public boolean[] uniqueReady;

public Keyboard()
{
keyDown = new boolean[keyNames.length];
keyTyped = new boolean[keyNames.length];
uniqueKeyDown = new boolean[keyNames.length];
uniqueReady = new boolean[keyNames.length];

for (int i = 0; i < uniqueReady.length; i++) uniqueReady[i]=true;
}

public void keyPress(String name)
{
for (int i = 0; i < keyNames.length; i++)
if (keyNames[i].equals(name))
{
keyDown[i] = true;
if (uniqueReady[i])
{
	uniqueKeyDown[i] = true;
	uniqueReady[i] = false;
}
}
}
public void keyRelease(String name)
{
for (int i = 0; i < keyNames.length; i++)
if (keyNames[i].equals(name))
{
keyDown[i] = false;
keyTyped[i] = true;
uniqueReady[i] = true;
}
}

public boolean k(String name)//k is short for "isKeyDown" -- (while down)
{
for (int i = 0; i < keyNames.length; i++)
if (keyNames[i].equals(name))
return keyDown[i];

return false;
}

public boolean t(String name)//t is short for "keyTyped" -- (on release)
{
	for (int i = 0; i < keyNames.length; i++)
		if (keyNames[i].equals(name))
		return keyTyped[i];

		return false;
}

public boolean f(String name)//f is "first key down" for unique keydown
{
	for (int i = 0; i < keyNames.length; i++)
		if (keyNames[i].equals(name))
		return uniqueKeyDown[i];

		return false;
}

public boolean isKeyDown(String name)
{
	return this.k(name);
}

public void untype()//release all of the typed keys, and unique keys (because they have been used once)
{
	for (int i = 0; i < keyTyped.length; i++)
		{
		keyTyped[i] = false;
		uniqueKeyDown[i] = false;
		}
}

public void setKeyNames(String[] kn)
{
	keyNames = kn;
	
	keyDown = new boolean[kn.length];
	keyTyped = new boolean[kn.length];
	uniqueKeyDown = new boolean[kn.length];
	uniqueReady = new boolean[kn.length];
	
	for (int i = 0; i < uniqueReady.length; i++) uniqueReady[i]=true;
}


}