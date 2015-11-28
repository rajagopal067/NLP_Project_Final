#!/usr/bin/python
import os,sys,json










def main(argv):
	inpF = argv[0]
	s  = {}
	s["vis"] =  []
	for f in os.listdir(inpF):
		sentiment = {}
		fHandle = open(inpF+f,"r")
		pos = 0
		neg = 0
		lines = fHandle.readlines();
		for line in lines:
			if line.find("POS")==0:
				pos+=1
			else : 
				neg+=1

		sentiment["feature"] = f
		sentiment["POS"] = pos
		sentiment["NEG"] = neg
		s["vis"].append(sentiment)
		fHandle.close()

	fHandle = open(argv[1],"w")
	fHandle.write(json.dumps(s,indent=4))
	fHandle.close()
	
			
			




if __name__=="__main__":
	main(sys.argv[1:])
