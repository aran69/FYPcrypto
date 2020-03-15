import java.util.*;

public class NTRU{
	public static void main (String[] args){
		try{
			switch(args[0]){
                //the set L should possibly be well defined before this point with d 1s in every polynomial
				//in most cases N can be determined by the key length
                case "encrypt": 
                //args[1] pub key, args[2] N p q, args[3] message
				int[] enkey = string2arr(args[1]);
                //break message into blocks and encrypt each one
				System.out.print(encrypt(enkey,args[2],args[3]));
				break;

				case "decrypt": 
				int[] dekey = string2arr(args[1]); //decrypts message using f fp q p and encrypted message
                //each ciphertext block is fed into the function below
                //args[3] is the message and args[2] contains fp,q,p and maybe n?
				System.out.print(decrypt(dekey,args[2],args[3]));
				break;

				case "privkeygen": System.out.print(privkeygen(args[1])); //generates f fp and fq from N and d
				break;

                //case "newpubkeygen" takes the private key f d and fq and generates a new public key
                //break;

				default : System.out.println("Please append valid action and parameters to this class' call, i.e \"encrypt\", \"decrypt\" or \"keygen\". ");
				break;
			}
		}
		catch(Exception e){
			System.out.println("Please call this class with either encrypt, decrypt or keygen as args, followed by additional args \n encrypt/decrypt requires two additional args, a key and a message \n the key should be an array of coefficients of form {a1, a2, a3, ... aN-1, aN} with each ak an integer \n keygen requires one additional argument, a seed.");
		}
		
	}	

	public static int[] string2arr(String in){
		String[] stringarr = in.split(", ");
        int[] retarray = new int[stringarr.length];
		for (int i =0; i<stringarr.length; i++){
			if(i==0){
				stringarr[i]=stringarr[i].substring(1);
			}
			else if(i==stringarr.length-1){
				stringarr[i]=stringarr[i].substring(0,stringarr[i].length()-1);
			}
            retarray[i]=Integer.parseInt(stringarr[i]);
            //System.out.println(retarray[i]);
        }
		return retarray;
	}

    public static int[] polyadd(int[] a, int[] b){
        int[] c = new int[a.length];
        for (int i =0; i<c.length; i++){
            c[i]=a[i]+b[i];
            //System.out.println(c[i]);
        }
        return c;
    }

    public static int[] starmultiply(int[] a, int[] b){
        int[] c = new int[a.length];
        int summedcoeff;
        for (int i =0; i<c.length; i++){
            summedcoeff = 0;
            for (int j =0; j<c.length; j++){
                if((i-j)<0){
                    summedcoeff+=(a[j]*b[(i-j)+c.length]);
                }
                else{
                    summedcoeff+=(a[j]*b[i-j]);
                }
                
            }    
            c[i]=summedcoeff;
            //System.out.println(c[i]);
        }
        return c;
    }


    public static int[] polymod(int[] a, int mod){
        int[] c = a;
        for (int i =0; i<c.length; i++){
            while(c[i]<0){
                c[i]+=mod;
            }
            while(c[i]>=mod){
                c[i]-=mod;
            }
            //System.out.println(c[i]);
        }
        return c;
    }


	public static int[] encrypt(int[] key, String npq, String message){
		int[] messag = new int[] {1,-1,0,1,1,-1,0,1,-1}; //placeholder for message converted to polynomial still fuzzy on the technique here
        int[] polyfuzz = new int[] {1,1,1,-1,-1,-1,0,0,1};
        //the following values are placeholders and should be extracted from args[2]
        int n = 9;
        int p = 3;
        int q = 256;
        int[] encryptedpoly = polymod(polyadd(starmultiply(polyfuzz,key),messag),q);
		return encryptedpoly;
	}


	public static int[] decrypt(int[] key, String fqp, String ciphertext){
		int[] ciph = new int[] {1,-1,0,1,1,-1,0,1,-1}; //placeholder for ciphertext converted to polynomial still fuzzy on the technique here
        int[] fp = new int[] {1,-1,0,1,1,-1,0,1,-1};
        int p=3;
        int q=256;
        int s =100;
        int t =2;
        int[] a = polymod(starmultiply(key,ciph),q);
        int[] b = new int[a.length];
        int sum;
        for (int i =0; i<b.length; i++){
            sum = a[i]+t;
            if(a[i]>=s){
                sum=sum-q;
            }
            b[i]=sum;
            //System.out.println(b[i]);
        }
        int[] plaintextpoly = polymod(starmultiply(b,fp),p);

		return plaintextpoly;
	}

    public static int[] privkeygen(String seed){
        int[] ret = {1,2};
        return ret;
    }


    public static int[] newpubkeygen(String seed){
        int[] ret = {1,2};
        return ret;
    }
}

/*

public PolynomialMod inverse(int N, int mod) {
    int loop = 0;
    PolynomialMod G = PolynomialMod.ZERO.clone();
    G.setNMod(N, mod);
    PolynomialMod newG = (PolynomialMod) PolynomialMod.ONE.clone();
    newG.setNMod(N, mod);
    int[] coeffR = { 1, 1, 0, 1, 1, 0, 0, 0, 1 };

    PolynomialMod quotient = null;
    PolynomialMod newR = this.clone();
    PolynomialMod R = this.getRing(N, mod);
    R.setNMod(N, mod);
    newR.setNMod(N, mod);

    while (!newR.equalsZero()) {
        if (DEBUG && loop != 0)
            System.out.println("loop: " + loop);
        if (DEBUG && loop == 0)
            System.out.println("========Initial Values========");
        if (DEBUG)
            System.out.println("R   : " + R);
        if (DEBUG)
            System.out.println("newR: " + newR);
        if (DEBUG)
            System.out.println("Quotient: " + quotient);
        if (DEBUG)
            System.out.println("G   : " + G);
        if (DEBUG)
            System.out.println("newG: " + newG);
        if (DEBUG && loop == 0)
            System.out.println("========Initial Values========");
        if (DEBUG)
            System.out.println("\n");

        quotient = R.div(newR)[0];
        PolynomialMod help = R.clone();
        R = newR.clone();
        PolynomialMod times = quotient.times(newR);
        times.reduceBetweenZeroAndQ();
        newR = help.sub(times);
        newR.deleteLeadingZeros();
        newR.degree = newR.values.size() - 1;
        help = G.clone();
        G = newG.clone();
        PolynomialMod times2 = quotient.times(newG);
        times2.reduceBetweenZeroAndQ();
        newG = help.sub(times2);
        loop++;

    }
    if (R.getDegree() > 0)
        throw new ArithmeticException("irreducible or multiple");

    return G.div(R)[0];
}



public void inverseEuclid(int N, int mod) {
    PolynomialMod a= this.clone();
    PolynomialMod b= getRing(N,mod);
    PolynomialMod u = PolynomialMod.ONE.clone();
    u.setNMod(N, mod);
    PolynomialMod v1 = PolynomialMod.ZERO.clone();
    v1.setNMod(N, mod);
    PolynomialMod d = this.clone();
    PolynomialMod v3 = b.clone(); 

    while(!v3.equalsZero()) {
        System.out.println("========values========");
        System.out.println("d : "+d);
        System.out.println("v3: "+v3);
        PolynomialMod [] div = d.div(v3);
        PolynomialMod q =  div[0].clone();
        System.out.println("q : "+q);
        PolynomialMod t3 =  div[1].clone();
        System.out.println("t3: "+t3);
        PolynomialMod t1 = u.sub(q.convolution(v1));
        System.out.println("t1: "+t1);
        System.out.println("========values========\n\n");

        u = v1.clone();
        d = v3.clone();
        v1= t1.clone();
        v3=t3.clone();

        u.deleteLeadingZeros();
        d.deleteLeadingZeros();
        v1.deleteLeadingZeros();
        v3.deleteLeadingZeros();
    }
    PolynomialMod v = d.sub(a.convolution(u)).div(b)[0];
    System.out.println("u: "+u);
    System.out.println("v: "+v);
    System.out.println("d: "+d);
}



public PolynomialMod[] div(final PolynomialMod other) {
    if (other.isZero())
        throw new ArithmeticException("division by zero");
    final int degreeDifference = this.getDegree() - other.getDegree() + 1;
    if (degreeDifference <= 0)
        return new PolynomialMod[] { PolynomialMod.ZERO, this };

    final PolynomialMod rest = this.clone();
    final PolynomialMod quotient = new PolynomialMod(degreeDifference - 1, N, mod);
    final int otherDegree = other.getDegree();
    final int coeff = other.values.get(otherDegree);
    for (int i = degreeDifference - 1; i >= 0; i--) {
        final int q = MyMath.divMod(rest.values.get(otherDegree + i), coeff, mod);

        quotient.values.set(i, q);
        for (int j = 0; j <= otherDegree; j++) {
            int restHelp = ((rest.values.get(i + j) - q * other.values.get(j)) + mod) % mod;
            rest.values.set(i + j, restHelp);
        }
    }
    return new PolynomialMod[] { new PolynomialMod(quotient.values, N, mod),
            new PolynomialMod(rest.values, N, mod) };
}

*/ 
