public class ByteConverter
{
	/*
	 * function twobytes(i)
' this will convert an integer into two characters.
' the low byte and the hi byte
  dim lobyte, hibyte
 hibyte =  int(i/256)
 lobyte = i - hibyte*256
' convert to ascii characters
  a = chr(lobyte)
  b = chr(hibyte)
 msgbox  " lobyte=" & lobyte & " hibyte=" & hibyte
 twobytes = a & b
end function
function byteToInt(sChar)
dim i, a, b, hb,lb
' take the char and split in two.
   a = mid(sChar,1,1)
   b = mid(sChar,2,1)
' convert to asii code
   hb = asc(b)
   lb = asc(a)
' convert to integers
   i = lb + hb*256
   bytetoInt = i
end function
 i = 31241
 h = twobytes(i)
 msgbox "char=" & h
' now convert back
  o = bytetoint(h)
 msgbox "int= " & o
	 */
	
	public static String charPair(int i)
	{
		short hi = (short)(i/256);
		short lo = (short)(i - hi*256);
		
		char a = (char)(lo);
		char b = (char)(hi);
		return "" + a + b;
	}
	
	public static int backToInt(String pair)
	{
		char a = pair.charAt(0);
		char b = pair.charAt(1);
		
		int hi = (int)b;
		int lo = (int)a;
		
		return lo + hi*256;
	}
	
	
	
	
	
}