import java.util.*;
import java.lang.Math;

public class NTRU{
	public static void main (String[] args){
	//args must be well chosen, i.e n>=2d and n is prime, P is prime (specifically its 3), q is coprime with p,
	//N must be large enough to carry a well encrypted message as well as a checksum
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

				case "privkeygen": 
				int n =Integer.parseInt(args[1]);
                int p =Integer.parseInt(args[2]);
                int q =Integer.parseInt(args[3]);
                int d =Integer.parseInt(args[4]);
                p =3; // p will remain locked at 3 until i make a more adaptive ternary encoding
                int[][] keys = privkeygen(n, p, q, d);
                for(int i=0; i<keys.length; i++){
                	if(i==0){
                		System.out.print("f: ");
                	}
                	else if(i==1){
                		System.out.print("fp: ");
                	}
                	else{
                		System.out.print("fq: ");
                	}
                	for(int j=0; j<keys[i].length; j++){
                		System.out.print(keys[i][j]+", ");
                	}
                	System.out.println();
                	
                } //generates f fp and fq from N and d
				break;

                case "newpubkeygen": //takes the private key f, inversekey fq, p and q and generates a new public key
                String[] keyarr = args[1].split(":");//args[1] contains f and inverse fp both of form {a, b, c, d, e, f} concatenated {contentsf}:{contentsfq}
                int[] dekeypkg = string2arr(keyarr[0]); 
                int[] inversekeyq = string2arr(keyarr[1]);
                int[] npqdpkg = string2arr(args[2]);//args[2] contains npqd in form {n, p, q, d}
                System.out.print(newpubkeygen(dekeypkg, inversekeyq, npqdpkg));
                break;

                case "randpolydebug":
                int na =Integer.parseInt(args[1]);
                int da =Integer.parseInt(args[2]);
                int[] rpd = randpoly(na,da,true);//n d
                for(int i=0; i<na; i++){
                	System.out.print(rpd[i]+", ");
                }
                break;

                case "starmultiplydebug":
                int[] e =string2arr(args[1]);
                int[] f =string2arr(args[2]);
                int[] b = starmultiply(e,f);
                for(int i=0; i<b.length; i++){
                	System.out.println(b[i]);
                }
                break;

                case "tristarmultiplydebug":
                int[] ee =string2arr(args[1]);
                int[] ff =string2arr(args[2]);
                int[] bbb = starmultiply(ee,ff);
                bbb = polymodtri(bbb);
                for(int i=0; i<bbb.length; i++){
                	System.out.println(bbb[i]);
                }
                break;

                case "polyinversepdebug":
                int[] j =string2arr(args[1]);
                int k =Integer.parseInt(args[2]);
                int[] l = polyinversep(j,k);
                System.out.println(" ");
                System.out.print("{");
                for(int i=0; i<l.length-1; i++){
                	System.out.print(l[i]+", ");
                }
                System.out.print(l[l.length-1]+"}");
                System.out.println("");
                break;

                case "xdivdebug":
                int[] aa =string2arr(args[1]);
                int[] bb = xdiv(aa);
                for(int i=0; i<bb.length; i++){
                	System.out.print(bb[i]+" ");
                }
                break;

                case "getdegdebug":
                int[] dd =string2arr(args[1]);
                System.out.println(getdeg(dd));
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

    public static int[] polysub(int[] a, int[] b){
        int[] c = new int[a.length];
        for (int i =0; i<c.length; i++){
            c[i]=a[i]-b[i];
            //System.out.println(c[i]);
        }
        return c;
    }

    public static int[] polyneg(int[] a){
    	int[] c = new int[a.length];
    	for (int i =0; i<c.length; i++){
            c[i]=a[i]*-1;
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
    	int out = a.length-1;
    	for (int i =0; i<a.length; i++){
            if(a[i]!=0){
            	out=i;
            }
            //System.out.println(c[i]);
        }
        return out;
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

    		degf =getdeg(f);
    		degg =getdeg(g);
    		if(degf<degg){ //exchange values
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
					return b;
					}
				//System.out.println("i:"+i+"   j:"+j);
				b = polymodtri(polyadd(b,ones));	
			}
			b = ringxdiv(b);
		}
    	
    	return zeros;
    }


    public static int[] polyinverseq(int[] a, int q){
    	//System.out.println("in");
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
    	while(mm<(5*f.length) && decided==false){

    		
    		while(f[0]==0){
    			System.out.println("Round "+mm+" f[0]==0 iteration "+k);
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

    		degf =getdeg(f);
    		degg =getdeg(g);
    		if(degf<degg){ //exchange values
    			//System.out.println("***EXCHANGE***");
    			tempfg = f;
    			f = g;
    			g = tempfg;
    			tempbc = b;
    			b = c;
    			c = tempbc;
    		}

			
    		
    		if(f[0]==g[0]){
    			f=polymod(polysub(f,g),q);
    			b=polymod(polysub(b,c),q);
    		}
    		else{
    			f = polymod(polyadd(f,g),q);
    			b = polymod(polyadd(b,c),q);
    		}
			
    		

    		
    		System.out.println(mm);
    		//System.out.println("f :"+f[0]+" "+f[1]+" "+f[2]+" "+f[3]+" "+f[4]+" "+f[5]+" "+f[6]+" "+f[7]+" "+f[8]+" "+f[9]+" "+f[10]);
    		//System.out.println("g :"+g[0]+" "+g[1]+" "+g[2]+" "+g[3]+" "+g[4]+" "+g[5]+" "+g[6]+" "+g[7]+" "+g[8]+" "+g[9]+" "+g[10]);
    		//System.out.println("b :"+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    		//System.out.println("c :"+c[0]+" "+c[1]+" "+c[2]+" "+c[3]+" "+c[4]+" "+c[5]+" "+c[6]+" "+c[7]+" "+c[8]+" "+c[9]+" "+c[10]);
    		mm++;	
    	}
    	       
    	//System.out.println("b :"+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    	//System.out.println("k :"+k);
    	System.out.println("Sorta done");
    	
		
		if(decided==false){
			return zeros;
		}
		 
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
    	
    	return zeros;
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

    public static int[][] privkeygen(int n, int p, int q, int d){ //done: generate f . fenerate fp    not done: generate fq
    	int[][] ret = new int[3][n];
    	int[] zeros = new int[n];
    	int[] f = randpoly(n, d, true);
    	int[] fp = polyinversep(f, 0);
    	while(Arrays.equals(fp,zeros)) {
    		f= randpoly(n, d, true);
    		fp = polyinversep(f, 0);
    	}
    	int[] fq=polyinverseq(f,q);
    	ret[0]=f;
    	ret[1]=fp;
    	ret[2]=fq;

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




/*
public static int[] polyinversepnythebook(int[] a, int inp){
    	System.out.println("in");
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
    	x[1]=1;
    	for(int i=0; i<g.length; i++){
    		g[i]=-1;
    	}
    	System.out.println("g:"+g[0]+" "+g[1]+" "+g[2]+" "+g[3]+" "+g[4]+" "+g[5]+" "+g[6]+" "+g[7]+" "+g[8]+" "+g[9]+" "+g[10]);
    			
    	b[0]=1;
    	goal[0]=1;
    	int degf; 
    	int degg; 
    	boolean er = false;
    	int mm = 900; //arbitrary long loop count
    	out:
    	while(mm>0 && er==false){

    		if(mm==889){//error catching
    			System.out.println("k:"+k);
    			System.out.println("f:"+f[0]+" "+f[1]+" "+f[2]+" "+f[3]+" "+f[4]+" "+f[5]+" "+f[6]+" "+f[7]+" "+f[8]+" "+f[9]+" "+f[10]);
    			System.out.println("g:"+g[0]+" "+g[1]+" "+g[2]+" "+g[3]+" "+g[4]+" "+g[5]+" "+g[6]+" "+g[7]+" "+g[8]+" "+g[9]+" "+g[10]);
    			System.out.println("b:"+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    			System.out.println("c:"+c[0]+" "+c[1]+" "+c[2]+" "+c[3]+" "+c[4]+" "+c[5]+" "+c[6]+" "+c[7]+" "+c[8]+" "+c[9]+" "+c[10]);
    			System.out.println();
    			System.out.println();
    		}
    		while(f[0]==0){
    			System.out.println("Round "+mm+" f[0]==0 iteration "+k);
    			f = ringxdiv(f);
    			c=starmultiply(c,x);
    			if(mm<850 || k>100){
    				er = true;
    				break out;
    			}
    			System.out.println("f:"+f[0]+" "+f[1]+" "+f[2]+" "+f[3]+" "+f[4]+" "+f[5]+" "+f[6]+" "+f[7]+" "+f[8]+" "+f[9]+" "+f[10]);
    			System.out.println("c:"+c[0]+" "+c[1]+" "+c[2]+" "+c[3]+" "+c[4]+" "+c[5]+" "+c[6]+" "+c[7]+" "+c[8]+" "+c[9]+" "+c[10]);
    			System.out.println("k: "+k);
    			k++;
    		}
    		if(f==goal){
    			return b;//to include shift this polynomial k places to the right(left?)
    		}
    		else if (f==polyneg(goal)){
    			return polyneg(b); //negative case
    			//to include shift this polynomial k places to the right(left?)
    		}
    		degf =getdeg(f);
    		degg =getdeg(g);
    		if(degf<degg){ //exchange values
    			System.out.println("***EXCHANGE***");
    			tempfg = f;
    			f = g;
    			g = tempfg;
    			tempbc = b;
    			b = c;
    			c = tempbc;
    		}

    		if(mm<850 || k>100){
    				er = true;
    				break out;
    		}

			
    		
    		if(f[0]==g[0]){
    			f=polymodtri(polysub(f,g));
    			b=polymodtri(polysub(b,c));
    		}
    		else{
    			f = polymodtri(polyadd(f,g));
    			b = polymodtri(polyadd(b,c));
    		}
			
    		//for(int i=0; i<b.length; i++){
            //    	System.out.print(" "+b[i]);
            //    }

    		
    		System.out.println(mm);
    		System.out.println("f :"+f[0]+" "+f[1]+" "+f[2]+" "+f[3]+" "+f[4]+" "+f[5]+" "+f[6]+" "+f[7]+" "+f[8]+" "+f[9]+" "+f[10]);
    		System.out.println("g :"+g[0]+" "+g[1]+" "+g[2]+" "+g[3]+" "+g[4]+" "+g[5]+" "+g[6]+" "+g[7]+" "+g[8]+" "+g[9]+" "+g[10]);
    		System.out.println("b :"+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]+" "+b[9]+" "+b[10]);
    		System.out.println("c :"+c[0]+" "+c[1]+" "+c[2]+" "+c[3]+" "+c[4]+" "+c[5]+" "+c[6]+" "+c[7]+" "+c[8]+" "+c[9]+" "+c[10]);
    		mm--;	
    	}
    	//for(int i=0; i<x.length; i++){
        //        	System.out.print("in "+x[i]);
        //       }

    	return b;
    }
*/
