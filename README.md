This document details the operation of NTRU.java

Step 1) 
compile in command line/terminal:
	
	javac NTRU.java


step 2.1) 
Decide on the method you wish to use:

	"encrypt" ; 
		accepts: a public polynomial key h, public variables n,p,q and d, and a plaintext message polynomial block.

		returns: a ciphertext message polynomial block
	

	"decrypt" ; 
		accepts: a private polynomial key f, inverse 
		polynomial fp, shift value s and shift value t, public variables n,p,q and d, and a ciphertext message polynomial block.

		returns: a plaintext message polynomial block
	

	"privkeygen" ; 
		accepts: public variables n,p,q and d

		returns:  a random private polynomial key f, inverse polynomial fp, inverse polynomial fq, a random public polynomial key h corresponding to f, shift value s and shift value t. 
	
	
	"newpubkeygen" ; 
		accepts: a private polynomial key f, inverse polynomial fq, and public variables n,p,q and d.

		returns: a new random public polynomial key h corresponding to f.

step 2.2)
Depending on your selected method, enter your arguments to the command line as follows


	encrypt:
		java NTRU encrypt <h> <npqd> <plaintextblock>


	decrypt:
		java NTRU decrypt <f> <fp> <s> <t> <npqd> <ciphertextblock>

	privkeygen:
		java NTRU privkeygen <npqd>

	newpubkeygen:
		java NTRU newpubkeygen <f> <fq> <npqd>



the format of these arguments is:

	<npqd>: "{n, p, q, d}"
		the values n, p, q and d are integers and public knowledge, make sure to keep a comma and space between each integer, and that this is passed as an "array-like string", that is it is entered with the quotation marks and curly brackets in the command line/terminal.

	<f>/<fp>/<plaintextblock>: "{f1, f2, ... fn-1, fn}"
		each of these "array-like strings" have length of n, as passed in the string array <npqd> described above, furthermore, each element fx is either a '0', '1', or '-1'. (the modulus of each element is p as passed in the string array <npqd> but is currently hardcoded at 3 regardless of input).
		note: fp can instead use '0', '1' and '2' instead of '0', '1' and '-1';
		Ensure quotation marks, brackets, commas and spaces are entered as mentioned above.

	<h>/<fq>/<ciphertextblock>: "{h1, h2, ... hn-1, hn}"
		each of these "string arrays" have length of n, similar to the "array-like strings" described above. However, each element is an integer between 0 and (q-1) as passed in the "array-like string" <npqd>, that is to say, 0 <= hx < q .
		Again ensure entry with brackets and punctuation as above.

	<s>/<t>: x
		s and t are simply entered as decimal point numbers, quotation marks unnecesary.




