#!/usr/bin/python

from os import listdir
from os.path import isfile, join
import sys
from bs4 import BeautifulSoup
import math
import json

def main(argv):
	folderName = argv[0]
	onlyfiles = [ f for f in listdir(folderName) if isfile(join(folderName,f)) ]
	revs = []
	for fin in sorted(onlyfiles):
		fileName = folderName + "/" + fin
		
		with open(fileName,"r") as fin:
			soup = BeautifulSoup(open(fileName),"lxml")
			reviews = soup.find("div",id="cm_cr-review_list")
			for review in reviews:
				if "review" not in review['class']:
					continue
				rev = {}
				rev["stars"] = (review.find("i",{"class":"a-icon-star"}).get_text())[0]
				rev["comment"] = review.find("span",{"class":"review-text"}).get_text()
				rev["date"] = (review.find("span",{"class":"review-date"}).get_text())[3:]
				rev["author"] = (review.find("a",{"class":"author"}).get_text())
				revs.append(rev)

				
	rpp = 500
	opdir = argv[1]
	fileName = 20

	for i in range(int(math.ceil(float(len(revs))/rpp))):
		j = (i+1)*rpp-1
		if j>len(revs):
			j = len(revs)
		print "file",fileName

		with open(opdir + "/motog" + str(fileName) + ".txt" , "w") as fout:
			op = {}
			op["amazon"] = revs[i*rpp:j]

			fout.write(json.dumps(op,indent=4))
		fileName = fileName + 1
		

if __name__=="__main__":
	main(sys.argv[1:])

