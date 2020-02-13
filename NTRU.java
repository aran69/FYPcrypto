import java.util.*;

public class NTRU{
	public static void main (String[] args){
		switch(args[0]){
			case "encrypt": System.out.print(encrypt(args[1],args[2]));
			break;

			case "decrypt": System.out.print(decrypt(args[1],args[2]));
			break;

			case "keygen": System.out.print(keygen(args[1]));
			break;

			default : System.out.println("Please append valid action and parameters to this class' call, i.e \"encrypt\", \"decrypt\" or \"keygen\". ");
			break;
		}
	}	

	public static int[] encrypt(String key, String message){
		int[] ret = {1,2};
		return ret;
	}

	public static int[] decrypt(String key, String message){
		int[] ret = {1,2};
		return ret;
	}

	public static int[] keygen(String seed){
		int[] ret = {1,2};
		return ret;
	}
}

