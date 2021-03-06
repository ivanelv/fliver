package util;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64EncodingManager
{

	public static Base64EncodingManager instance;

	private Base64EncodingManager()
	{
	}

	public static Base64EncodingManager getInstance()
	{
		if (instance == null)
			instance = new Base64EncodingManager();
		return instance;
	}

	public String decode(String encodedText)
	{
		byte[] decodedBytes = Base64.getMimeDecoder().decode(encodedText);
		String result = "";
		try
		{
			result = new String(decodedBytes, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public String encode(String plainText)
	{
		String result = "";
		try
		{
			result = Base64.getMimeEncoder().encodeToString(plainText.getBytes("utf-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		return result;
	}
}
