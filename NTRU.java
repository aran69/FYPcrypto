import java.util.*;
import java.lang.Math;

public class NTRU{
	public static void main (String[] args){
	//args must be well chosen, i.e n>=2d and n is prime, P is prime (specifically its 3), q is coprime with p,
		try{
			switch(args[0]){
                //the set L should possibly be well defined before this point with d 1s in every polynomial
				//in most cases N can be determined by the key length
                case "encrypt": 
                //args[1] pub key an array passed of form {a, b, c, d, ... z}, args[2] N p q d an array passed of form {N, p, q, d}, args[3] message (a plaintext string)
				int[] enkey = string2arr(args[1]);
                int[] npqden = string2arr(args[2]);
                int[] plaintext = string2arr(args[3]);//converted from args[3]
                //break message into blocks, add checksum to the end of each block, and encrypt each one, To be implemented,
				printarr(encrypt(enkey,npqden,plaintext));//writes text to polynomials, may need to be parsed back to text (ASCII) or just sent in poly form
				break;

				case "decrypt": 
				int[] dekey = string2arr(args[1]); //args[1]
                int[] inversekeyp = string2arr(args[2]);//args[2]
                float s = Float.parseFloat(args[3]);//args[3]
                float t = Float.parseFloat(args[4]);//args[4]
                int[] npqdde = string2arr(args[5]);//args[5]
                int[] ciphertext = string2arr(args[6]);//args[6]
				printarr(decrypt(dekey,inversekeyp,npqdde,ciphertext,s,t));//f,fp,npqd,ciphertext,s,t
				break;

				case "privkeygen": 
				int[] npqd = string2arr(args[1]);
				int n =npqd[0];
                int p =npqd[1];
                int q =npqd[2];
                int d =npqd[3];
                p =3; // p will remain locked at 3 until i make a more adaptive ternary encoding
                float[] shiftST = new float[2];
                shiftST[0]=s(n,p,q,d);
                shiftST[0]=t(n,p,q,d);
                int[][] keys = privkeygen(n, p, q, d);

                System.out.print("f: ");
                printarr(keys[0]);
                System.out.print("fp: ");
                printarr(keys[1]);
                System.out.print("fq: ");
                printarr(keys[2]);
                System.out.print("h: ");
                printarr(keys[3]);
                System.out.println("s: "+shiftST[0]);
                System.out.println("T: "+shiftST[1]);
				break;
                
                case "newpubkeygen": //takes the private key f, inversekey fq, p and q and generates a new public key h
                int[] dekeyf = string2arr(args[1]); 
                int[] inversekeyq = string2arr(args[2]);
                int[] npqdpkg = string2arr(args[3]);//args[2] contains npqd in form {n, p, q, d}
                printarr(newpubkeygen(dekeyf, inversekeyq, npqdpkg));//f,fq,npqd
                break; 

                case "starmultiplydebug":
                int[] e =string2arr(args[1]);
                int[] f =string2arr(args[2]);
                int modul=Integer.parseInt(args[3]);
                int[] b = starmultiply(e,f);
                if(modul==3){
                    b=polymodtri(b);
                }
                else{
                    b=polymod(b,modul);
                }
                printarr(b);
                break;

                case "unittest":
                int[] adekey = string2arr(args[1]); //args[1]
                int[] ainversekeyp = string2arr(args[2]);//args[2]
                float as = Float.parseFloat(args[3]);//args[3]
                float at = Float.parseFloat(args[4]);//args[4]
                int[] anpqdde = string2arr(args[5]);//args[5]
                int[] aciphertext = string2arr(args[6]);//args[6]
                int[] agoal = string2arr(args[7]);
                while(false==Arrays.equals(decrypt(adekey, ainversekeyp, anpqdde, aciphertext, as, at),agoal)){
                    if(as<=0){
                        as*=-1;
                        as++;
                    }
                    else{
                        as*=-1;
                    }
                    //System.out.println("S: "+as);
                    if(Arrays.equals(decrypt(adekey, ainversekeyp, anpqdde, aciphertext, as, at),agoal)){
                        break;
                    }
                    if(as>(anpqdde[2]*2)){
                        break;
                    }
                }
                if(as>(anpqdde[2]*2)){
                    System.out.println("Gap failure, message incapable of being decoded.");
                }
                else{
                    printarr(decrypt(adekey, ainversekeyp, anpqdde, aciphertext, as, at));    
                }
                break;

				default : 
				System.out.println("Please append valid action and parameters to this class' call, i.e \"encrypt\", \"decrypt\" or \"keygen\". ");
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

    public static int[] polysub(int[] a, int[] b){
        
        int[] c = new int[a.length];
        for (int i =0; i<c.length; i++){
            c[i]=a[i]-b[i];
            //System.out.println(c[i]);
        }
        return c;
    }

    public static float s(int n, int p, int q, int d){
    	float fq =q;
    	float fd =d;
    	float fp =p;
    	float fn =n;
    	float exp = (float)Math.pow(fd,3);
    	float ret = 0f;
    	ret=(fq/2)+(fd*(fp-1))+(exp/fn);
    	//ret = (fq/2)+(fp-2);
        ret = ret%fq;
    	return ret;
    }

    public static float t(int n, int p, int q, int d){
    	float fq =q;
    	float fd =d;
    	float fp =p;
    	float fn =n;
    	float exp = (float)Math.pow(fd,3);
    	float ret = 0f;	
    	ret = (fd*(fp-1)+(exp/fn));
    	ret = ret%fp;
    	//float ret = 0.0f;
        return ret; 
    }

    public static int[] polyneg(int[] a){
    	int[] c = new int[a.length];
    	for (int i =0; i<c.length; i++){
            c[i]=a[i]*-1;
            //System.out.println(c[i]);
        }
        return c;
    }



    public static int[] dotmult(int[] a, int b){

        int[] ret = new int[a.length];
       	for(int i=0; i<ret.length; i++) {
    		
           ret[i] = a[i]*b;
       	}
       	return ret;
    }


    public static int[] dotdiv(int[] a, int b){

        int[] ret = new int[a.length];
       	for(int i=0; i<ret.length; i++) {

       		//System.out.println("dotdiv loop : "+b);
           ret[i] = a[i]/b;
       	}
       	return ret;
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

    public static int[] xdiv(int[] a){
    	int[] c = new int[a.length];
    	c[a.length-1]=0;
        for (int i=0; i<a.length-1; i++){
        	c[i]=a[i+1];
        	if(c[i]==-2){
        		c[i]=1;
        	}
        }
        return c;
    }

    public static int[] ringxdiv(int[] a){
    	int[] c = new int[a.length];
    	c[a.length-1]=a[0];
        for (int i=0; i<a.length-1; i++){
        	c[i]=a[i+1];
        	if(c[i]==-2){
        		c[i]=1;
        	}
        }
        return c;
    }

    public static int[] polymodtri(int[] a){
    	int[] c =a;
    	for (int i =0; i<c.length; i++){
         	while(c[i]>1){
         		c[i]-=3;
         	}
         	if(c[i]<-1){
         		c[i]+=3;
         	}
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



    public static int getdeg(int[] a){
    	int out = -1;
    	for (int i =0; i<a.length; i++){
            if(a[i]!=0){
            	out=i;
            }
            //System.out.println(c[i]);
        }
        return out;
    }



    

    public static int ringmod(int a, int b){
    	int ret;
    	int temp = a;
    	if(a>0){
    		ret=a%b;
    	}
    	else if(a==0){
    		ret=0;
    	}
    	else{
    		while(temp<0){
    			temp+=b;
    		}
    	}

    	return temp;
    }


    public static void printarr(int[] a){
    	for(int i=0; i<a.length-1; i++){
    		System.out.print(a[i]+", ");	
    	}
    	System.out.println(a[a.length-1]);
    }

    



    public static int[] one2onemult(int[] a, int[] b){
    	int[] ret = new int[a.length];
    	for(int i=0; i<a.length; i++){
    		ret[i] = a[i]*b[i];
    	}
    	return ret;
    }

    public static int elementsum(int[] a){
    	int out=0;
    	for(int i=0; i<a.length; i++){
    		out+=a[i];
    	}
    	return out;
    }


    public static int divtozero(int num, int div, int mod){
    	int dividend = 0;
    	int divdown = num;
    	//System.out.println("Entering divtozero...");
    	while(divdown!=0){
    		divdown-=div;
    		if(divdown<0){
    			divdown+=mod;
    		}
    		if(divdown>=mod){
    			divdown-=mod;
    		}
    		dividend++;
    	}
    	//System.out.println("Exiting divtozero...");
    	
    	return dividend;
    }

	
    public static int[] divtozeroarr(int[] arr, int div, int mod){
    	int[] ret = arr;
    	for(int i=0; i<arr.length; i++){
    		ret[i]=divtozero(ret[i],div,mod);
    	}
    	return ret;
    }


	public static int[] encrypt(int[] key, int[] npqd, int[] message){
		int n = npqd[0];
        int p = npqd[1];
        int q = npqd[2];
        int d = npqd[3]; 
        int[] polyfuzz = randpoly(n,d,false);
        int[] encryptedpoly = polymod(polyadd(starmultiply(polyfuzz,key),message),q);
		return encryptedpoly;
	}


	public static int[] decrypt(int[] f, int[] fp, int[] npqd, int[] ciphertext, float s, float t){//f,fp,npqd,ciphertext,s,t
		int p=npqd[1];
        int q=npqd[2];
        int[] a = polymod(starmultiply(f,ciphertext),q);
        int[] b = new int[a.length];
        
        int sum;
        int inT = Math.round(t);
        for (int i =0; i<b.length; i++){
            sum = a[i]+inT;
            if(a[i]>=s){
                sum=sum-q;
            }
            b[i]=sum;
            //System.out.println(b[i]);
        }
        int[] plaintextpoly = polymod(starmultiply(b,fp),p);
        

        for(int i=0; i<plaintextpoly.length; i++){
        	if(plaintextpoly[i]==2){
        		plaintextpoly[i]=(-1);
        	}
        }
        //
        //optional?
        //put code to ensure checksum
        //
        //
		return plaintextpoly;
	}

    public static int[][] privkeygen(int n, int p, int q, int d){ //done: generate f . fenerate fp    not done: generate fq
    	int[][] ret = new int[4][n];
    	int[] zeros = new int[n];
    	int[] f = randpoly(n, d, true);
    	int[] fp = polyinversep(f, 0);
    	while(Arrays.equals(fp,zeros)) {
    		f= randpoly(n, d, true);
    		fp = polyinversep(f, 0);
    	}
    	printarr(f);
    	printarr(fp);
    	int[] fq = brutpolyinverseq(f, q);
    	//int[] fq=polyinverseq(f,q)
    	int[] npqd = {n,p,q,d};
    	int[] h = newpubkeygen(f, fq, npqd);
    	ret[0]=f;
    	ret[1]=fp;
    	ret[2]=fq;
    	ret[3]=h;

        return ret;
    }


    public static int[] newpubkeygen(int[] f, int[] fq, int[] npqd){
        int[] g = randpoly(npqd[0],npqd[3],false);
        int[] fg = starmultiply(f,g);
        int[] pi = new int[f.length];
        fg = polymod(fg,npqd[1]);
        int dif;
        for (int i =0; i<pi.length; i++){
            dif = npqd[1] - fg[i]; //fg[i]=-16 dif = 32 -(-16)=48 pi[i]=48
            pi[i]=dif;
            //System.out.println(pi[i]);
        }
        
        int[] h = polymod(polyadd(starmultiply(pi,fq),g),npqd[2]);

        return h;
    }





    public static int[] brutpolyinverseq(int[] a, int q){
    	System.out.println("Searching for inverse fq...");
    	int[] one = new int[a.length];
    	int[] ret = new int[a.length];
    	one[0]=1;
    	int[] fin = new int[a.length];
    	boolean debugflag = true;
    	for(int i=0; i<fin.length; i++){
    		fin[i]=q-1;
    	}
    	if(a[0]==44){
    		debugflag=false;
    	}
    	
    	while(false==(Arrays.equals(ret,fin))){
    		if(Arrays.equals(one,polymod(starmultiply(a,ret),q))){
    			return ret;
    		}
            //printarr(ret);
    		for(int i=0; i<a.length; i++){
    		
    			if(ret[i]<q-1){
    				ret[i]++;
    				break;
    			}
    			else{
    				ret[i]=0;
    			}
    		}
    		
    	}
    	
    	
    	return one;
    	
    
    	
    }

    public static int[] polyinversep(int[] a, int inp){
    	//System.out.println("in");
    	int p = inp;
    	int k = 0;
    	int[] f = a;
    	int[] x = new int[f.length];
    	int[] c = new int[f.length];
    	int[] g = new int[f.length];
    	int[] b = new int[f.length];
    	int[] goal = new int[f.length];
    	int[] tempbc = new int[f.length];
    	int[] tempfg = new int[f.length];
    	int[] ones = new int[f.length];
    	int[] zeros = new int[f.length];
    	x[1]=1;
    	
    	for(int i=0; i<ones.length; i++){
    		ones[i]=1;
    	}

    	for(int i=0; i<g.length; i++){
    		g[i]=-1;
    	}
    			
    	b[0]=1;
    	goal[0]=1;
    	int degf; 
    	int degg; 
    	boolean decided = false;
    	int mm = 0; //arbitrary long loop count
    	out:
    	while(mm<(5*f.length) || decided==false){

    		
    		while(f[0]==0){
    			//System.out.println("Round "+mm+" f[0]==0 iteration "+k);
    			f = ringxdiv(f);
    			c=starmultiply(c,x);
    			if(k>100){
    				decided = true;
    				break out;
    			}
    			k++;
    			//System.out.println("f:"+f[0]+" "+f[1]+" "+f[2]+" "+f[3]+" "+f[4]+" "+f[5]+" "+f[6]+" "+f[7]+" "+f[8]+" "+f[9]+" "+f[10]);
    			//System.out.println("c:"+c[0]+" "+c[1]+" "+c[2]+" "+c[3]+" "+c[4]+" "+c[5]+" "+c[6]+" "+c[7]+" "+c[8]+" "+c[9]+" "+c[10]);
    			//System.out.println("k: "+k);
    			
    		}
    		
    		if(Arrays.equals(f, goal)){
    			decided =true;
    			break out;

    		}

    		else if (Arrays.equals(f, polyneg(goal))){
    			b=polyneg(b);
    			decided=true;
    			break out; //negative case
    		}



    		if(getdeg(f)<getdeg(g)){ //exchange values
    			//System.out.println("***EXCHANGE***");
    			tempfg = f;
    			f = g;
    			g = tempfg;
    			tempbc = b;
    			b = c;
    			c = tempbc;
    		}

			
    		
    		if(f[0]==g[0]){
    			f=polymodtri(polysub(f,g));
    			b=polymodtri(polysub(b,c));
    		}
    		else{
    			f = polymodtri(polyadd(f,g));
    			b = polymodtri(polyadd(b,c));
    		}
			
    		

    		
    		//System.out.println(mm);
    		//System.out.println("f :"+f[0]+" "+f[1]+" "+f[2]+" "+f[3]+" "+f[4]+" "+f[5]+" "+f[6]+" "+f[7]+" "+f[8]+" "+f[9]+" "+f[10]);
    		//System.out.println("g :"+g[0]+" "+g[1]+" "+g[2]+" "+g[3]+" "+g[4]+" "+g[5]+" "+g[6]+" "+g[7]+" "+g[8]+" "+g[9]+" "+g[10]);
    		//System.out.println("b :"+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    		//System.out.println("c :"+c[0]+" "+c[1]+" "+c[2]+" "+c[3]+" "+c[4]+" "+c[5]+" "+c[6]+" "+c[7]+" "+c[8]+" "+c[9]+" "+c[10]);
    		mm++;	
    	}
    	       
    	//System.out.println("b :"+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    	//System.out.println("k :"+k);
    	//System.out.println("Sorta done");
    	
		
		if(decided==false){
			return zeros;
		}
		 
		for(int i=0; i<=b.length; i++){
			for(int j=0; j<3; j++){
				if(Arrays.equals(polymodtri(starmultiply(a,b)), goal)){
					for(int kk=0; kk<b.length; kk++){
                        if(b[kk]==(-1)){
                            b[kk]=2;
                        }
                    }
                    return b;
					}
				//System.out.println("i:"+i+"   j:"+j);
				b = polymodtri(polyadd(b,ones));	
			}
			b = ringxdiv(b);
		}
    	
    	return zeros;
    }


    public static int[] altpolyinverseq(int[] a, int q){
    	//this method is brute force key finding, but will have to suffice due to a lack of understanding of the general "almost inverse" algorithm described by Hoffman
    	//the computation time is (q^d) q the modulus and d the number of non-zero coefficients in the private key
    	int firstnonzeroa = 0;
    	int lastnonzeroa =a.length-1;
    	for(int i=0; i<a.length; i++){
    		if(a[i]==0){
    			firstnonzeroa++;
    		}
    		else{
    			break;
    		}
    	}
    	for(int i=a.length-1; i>=0; i++){
    		if(a[i]==0){
    			lastnonzeroa--;
    		}
    		else{
    			break;
    		}
    	}
    	//7
    	//fnz=0 lnz =4 
    	// permits only 

    	boolean checked = true;
    	int[] handya = new int[a.length];
    	int[] ret = new int[a.length];
    	int[] x = new int[a.length];
    	int xdegree;
    	int[] one = new int[a.length];
    	one[0]=1;
    	int[] adjusteda = new int[a.length];
    	long debugcounter = 1;
    	int zeroloop;
    	boolean nextit = false;
    	boolean debugflag = false;
    	next1const:
    	//ret[0]=1;
    	//System.out.println(elementsum(one2onemult(a,ret))%q);
    	
    	while (true){


    		while(elementsum(one2onemult(a,ret))!=1||nextit==true){ //ret array elements at key positions 
    			nextit = false;
    			//System.out.println("looking to satisfy 1 constraint");
    			//satisfy 1 constraint
    			for(int i=0; i<a.length; i++){
    				if(a[i]!=0){
    					ret[i]++;
    					if(ret[i]==q){
    						ret[i]=0;
    						continue;	
    					}
    					break;
    				}

    				printarr(ret);
    			}
    		}
    		//[0 0 0 0 0 0 0 0 0 ]
    		
    		System.out.println("1st linear equation combination "+debugcounter);
    		printarr(ret);
    		debugcounter++;
    		
    		/*if(debugcounter==50){
    			return ret;
    		}*/
    		
			//consider making a simple rolling digit loop instead of the below
    		
    		xdegree =1;
    		x[xdegree] = 1;
    		adjusteda =starmultiply(a,x);
    		System.out.println("looking to satisfy 0 constraint");
   				
   				while(ringmod(elementsum(one2onemult(adjusteda,ret)),q)!=0){//o constraints
    				//satisfy 0 constraints
   					for(int i=0; i<a.length; i++){
    					if (adjusteda[i]!=0){
    						if (i==firstnonzeroa){
    							break;
    						}
    						ret[i]++;
    						if(ret[i]==q){
    							ret[i]=0;
    							continue;	
    						}
    						else{
    							break;
    						}
    					}
    				}

   				if(elementsum(one2onemult(adjusteda,ret))==0 && xdegree!=a.length-1){//if current iteration const met but not final: x[1]++ and continue
   					System.out.print("--constraintfound"+xdegree+"--");
   					x[xdegree]=0;
   					xdegree++;
   					x[xdegree]=1;
   					adjusteda =starmultiply(a,x);
   					
   				}
    		}
    		System.out.println("");
    		nextit=true;

    		if(Arrays.equals(one,starmultiply(a,ret))){
    			return ret;
    		}
    		else{
    			//jump
    			
    			continue;
    		}
    
    	}
    	

    }



    public static int[] polyinverseq(int[] a, int q){
    	//System.out.println("in");
    	int k = 0;
    	int[] f = a;
    	int[] x = new int[f.length];
    	int[] c = new int[f.length];
    	int[] g = new int[f.length];
    	int[] b = new int[f.length];
    	int u;
    	int[] goal = new int[f.length];
    	int[] tempbc = new int[f.length];
    	int[] tempfg = new int[f.length];
    	int[] ones = new int[f.length];
    	int[] zeros = new int[f.length];
    	x[1]=1;
    	
    	for(int i=0; i<ones.length; i++){
    		ones[i]=1;
    	}

    	for(int i=0; i<g.length; i++){
    		g[i]=1;
    	}

    	System.out.println("");
    			
    	b[0]=1;
    	goal[0]=1;
    	boolean decided = false;
    	int mm = 0; //arbitrary long loop count
    	out:
    	while(mm<(q*500*f.length) && decided==false){

    		
    		while(f[0]==0){
    			if(g[0]==0){
    				System.out.println("pre while f0 m "+mm);
    			}
    			System.out.println("Round "+mm+" f[0]==0 iteration "+k+"  g[0]="+g[0]);
    			f = ringxdiv(f);
    			c=starmultiply(c,x);
    			if(k>(100*q)){
    				decided = true;
    				break out;
    			}
    			k++;
    			if(g[0]==0){
    				System.out.println("post while f0 m "+mm);
    			}
    			
    		}

    		if(getdeg(f)==0){
    			
    			b=polymod(divtozeroarr(b,f[0],q),q);
    			decided = true;
    			return b;
    		}
    		
    		if(getdeg(f)<getdeg(g)){ //exchange values
    			System.out.println("***EXCHANGE***");
    			tempfg = f;
    			f = g;
    			g = tempfg;
    			tempbc = b;
    			b = c;
    			c = tempbc;
    			
    		}


    		u = divtozero(f[0],g[0],q)%q;

    		f = polymod(polysub(f,dotmult(g,u)),q); //this line is affecting g
    		
    		b = polymod(polysub(b,dotmult(c,u)),q);
    		
    		System.out.println("final statements post f[0]:"+f[0]+"  g[0]:"+g[0]+"   q:"+ q);
    					
    		if(g[0]==0){
    				System.out.println("post final "+mm);
    			}

    		if((mm%10)==0){
    			System.out.println(mm);
    			System.out.println("f :"+f[0]+" "+f[1]+" "+f[2]+" "+f[3]+" "+f[4]+" "+f[5]+" "+f[6]+" "+f[7]+" "+f[8]+" "+f[9]+" "+f[10]);
    			System.out.println("g :"+g[0]+" "+g[1]+" "+g[2]+" "+g[3]+" "+g[4]+" "+g[5]+" "+g[6]+" "+g[7]+" "+g[8]+" "+g[9]+" "+g[10]);
    			System.out.println("b :"+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    			System.out.println("c :"+c[0]+" "+c[1]+" "+c[2]+" "+c[3]+" "+c[4]+" "+c[5]+" "+c[6]+" "+c[7]+" "+c[8]+" "+c[9]+" "+c[10]);
    		
    		}
    		//System.out.println("f :"+f[0]+" "+f[1]+" "+f[2]+" "+f[3]+" "+f[4]+" "+f[5]+" "+f[6]+" "+f[7]+" "+f[8]+" "+f[9]+" "+f[10]);
    		//System.out.println("g :"+g[0]+" "+g[1]+" "+g[2]+" "+g[3]+" "+g[4]+" "+g[5]+" "+g[6]+" "+g[7]+" "+g[8]+" "+g[9]+" "+g[10]);
    		//System.out.println("b :"+b[0]+" "+b[1]+" "+b35[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    		//System.out.println("c :"+c[0]+" "+c[1]+" "+c[2]+" "+c[3]+" "+c[4]+" "+c[5]+" "+c[6]+" "+c[7]+" "+c[8]+" "+c[9]+" "+c[10]);
    		mm++;	
    	}
    	       
    	//System.out.println("b :"+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    	//System.out.println("k :"+k);
    	System.out.println("Sorta done");
    	
		
		if(decided==false){
			return zeros;
		}
		 /*
		for(int i=0; i<=b.length; i++){
			for(int j=0; j<q; j++){
				if(Arrays.equals(polymod(starmultiply(a,b),q), goal)){
					return b;
					}
				//System.out.println("i:"+i+"   j:"+j);
				b = polymod(polyadd(b,ones),q);	
			}
			b = ringxdiv(b);
		}
		*/
    	
    	return zeros; 
    	        
    }
}




/******************************
*TODO:
*.determine how the message string will be encoded to polynomial form (checksum will be added to each plaintext block and then encoded)
*.(important)implement polyinverse/privkeygen function
*.write checksum verifying code
*.(optional)add a polysubtract function?
*******************************/




