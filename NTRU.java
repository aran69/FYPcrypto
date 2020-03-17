import java.util.*;
import java.lang.Math;

public class NTRU{
	public static void main (String[] args){//args must be well chosen, i.e n>=2d and n is prime, P is prime (specifically its 3), q is coprime with p,
		try{
			switch(args[0]){
                //the set L should possibly be well defined before this point with d 1s in every polynomial
				//in most cases N can be determined by the key length
                case "encrypt": 
                //args[1] pub key an array passed of form {a, b, c, d, ... z}, args[2] N p q d an array passed of form {N, p, q, d}, args[3] message (a plaintext string)
				int[] enkey = string2arr(args[1]);
                int[] npqden = string2arr(args[2]);
                int[] placeholderparsedplaintext = new int[] {1,-1,0,1,1,-1,0,1,-1};//converted from args[3]
                //break message into blocks, add checksum to the end of each block, and encrypt each one, To be implemented,
				System.out.print(encrypt(enkey,npqden,placeholderparsedplaintext));//writes text to polynomials, may need to be parsed back to text (ASCII) or just sent in poly form
				break;

				case "decrypt": 
                //decrypts message using f fp q p and encrypted message
				String[] dekeyarr = args[1].split(":"); //args[1] contains f, inverse fp, s and t both of form {a, b, c, d, e, f} concatenated {contentsf}:{contentsfp}:s:t
                int[] dekey = string2arr(dekeyarr[0]); 
                int[] inversekeyp = string2arr(dekeyarr[1]);
                int s = Integer.parseInt(dekeyarr[2]);
                int t = Integer.parseInt(dekeyarr[3]);
                int[] npqdde = string2arr(args[2]);
                //each ciphertext block has its checksum removed and is then fed into the function below
                //args[3] is the message and args[2] contains N,p,q,d 
                int[] placeholderparsedciphertext = new int[] {1,-1,0,1,1,-1,0,1,-1};//converted from args[3] , may come in plaintext or polynomial form
				System.out.print(decrypt(dekey,inversekeyp,npqdde,placeholderparsedciphertext,s,t));
				break;

				case "privkeygen": System.out.print(privkeygen(args[1])); //generates f fp and fq from N and d
				break;

                case "newpubkeygen": //takes the private key f, inversekey fq, p and q and generates a new public key
                String[] keyarr = args[1].split(":");//args[1] contains f and inverse fp both of form {a, b, c, d, e, f} concatenated {contentsf}:{contentsfq}
                int[] dekeypkg = string2arr(keyarr[0]); 
                int[] inversekeyq = string2arr(keyarr[1]);
                int[] npqdpkg = string2arr(args[2]);//args[2] contains npqd in form {n, p, q, d}
                System.out.print(newpubkeygen(dekeypkg, inversekeyq, npqdpkg));
                break;

                case "randpolydebug":
                int n =Integer.parseInt(args[1]);
                int d =Integer.parseInt(args[2]);
                int[] rpd = randpoly(n,d,true);//n d
                for(int i=0; i<n; i++){
                	System.out.println(rpd[i]);
                }
                break;
				

				default : System.out.println("Please append valid action and parameters to this class' call, i.e \"encrypt\", \"decrypt\" or \"keygen\". ");
				break;
			}
		}
		catch(Exception e){
			System.out.println(e);
			//System.out.println("Please call this class with either encrypt, decrypt or keygen as args, followed by additional args \n encrypt/decrypt requires two additional args, a key and a message \n the key should be an array of coefficients of form {a1, a2, a3, ... aN-1, aN} with each ak an integer \n keygen requires one additional argument, a seed.");
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

    public static int[] randpoly(int n, int d, boolean f){
    	int posdcount=d;
    	int negdcount=d;
    	int currpos=0;
    	if(f){posdcount++;}
        int[] randy = new int[n];
        
        for(int i=n; i>0; i--){
        	currpos= ((currpos+(int) Math.round(Math.random()*i))%n);//rand*i
        	while(randy[currpos]!=0){currpos=(currpos+1)%n;}
        	if(posdcount!=0){
        		randy[currpos]=1;
        		posdcount--;
        	}
        	else if(negdcount!=0){
        		randy[currpos]=-1;
        		negdcount--;
        	}
        	else{continue;}
        }
        //randomly set d positions in n to -1 and d positions to 1 
        //start at position 0, move a random number of places less than n-loop iteration, if this position is 0 replace it with a 1 until all 1s are distributed, if position is not a 0 move position up until a 0 is found, repeat for -1s
        //this randompoly algorithm could be improved for security, but at this level of abstraction it might be a non-issue
        return randy;
    }

	public static int[] encrypt(int[] key, int[] npqd, int[] message){
		int n = npqd[0];
        int p = npqd[1];
        int q = npqd[2];
        int d = npqd[3]; //unsure if actually need to know this yet
        int[] polyfuzz = randpoly(n,d,false);
        int[] encryptedpoly = polymod(polyadd(starmultiply(polyfuzz,key),message),q);
		return encryptedpoly;
	}


	public static int[] decrypt(int[] f, int[] fp, int[] npqd, int[] ciphertext, int s, int t){
		int p=npqd[1];
        int q=npqd[2];
        int[] a = polymod(starmultiply(f,ciphertext),q);
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
        //
        //
        //put code to ensure checksum
        //
        //
		return plaintextpoly;
	}

    public static int[] privkeygen(String seed){
    	int n =11;//placeholder
    	int d =5;//placeholder
    	int[] f =randpoly(n,d,false);
        int[] ret = {1,2};
        return ret;
    }


    public static int[] newpubkeygen(int[] f, int[] fq, int[] npqd){
        int[] g = randpoly(npqd[0],npqd[3],false);
        int[] fg = starmultiply(f,g);
        int[] pi = new int[f.length];
        fg = polymod(fg,npqd[1]);
        int dif;
        for (int i =0; i<pi.length; i++){
            dif = npqd[1] - fg[i]; //maybe replace this with polysubtract method if i add that later
            pi[i]=dif;
            //System.out.println(pi[i]);
        }
        
        int[] h = polymod(polyadd(starmultiply(pi,fq),g),npqd[2]);

        return h;
    }
}




/******************************
*TODO:
*.determine how the message string will be encoded to polynomial form (checksum will be added to each plaintext block and then encoded)
*.(important)implement polyinverse/privkeygen function
*.write checksum verifying code
*.(optional)add a polysubtract function?
*******************************/
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
