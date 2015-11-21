__author__ = 'vijay'
from textblob import Word
import os

class SentenceCorrection:
    def __init__(self,fileName,outputfile):
        self.fileName = fileName
        self.features = ['battery','screen','touch','multi touch','moto','moto g','flipkart',"screen" , "graphics", "touch","brightness" , "headset" , "head set", "charger","charger" , "ports","data cable","cable","headphone" ,  "headphone jack" , "headphones jack","lcd","gorrila","touch","memory" , "upgradable", "RAM","battery" , "temperature", "heating","standby" , "stand by","camera" , "cam", "front cam","rear cam" , "picture" , "picture quality" , "image quality" ,"video","photos","software" , "ui", "os updates","os" , "os features","os compatibility","file manager","gaming" ,  "android experience","responsive" , "speed", "lag","hang" , "freeze" , "processor"]
        self.output_file = outputfile

    def load_sentences(self):
        file = open(self.fileName)
        writefile = open(self.output_file,"w")
        temp = dict()
        id = 0
        for line in file:
            line = line.strip()
            if line == '{':
                writefile.write(line+"\n")
                id = 1
            elif line == '}':
                writefile.write(line+"\n")
                id = 0
            elif id == 1:
                writefile.write(line+"\n")
                id = 2
            elif id == 2:
                writefile.write(self.split_sentences(line)+"\n")
        file.close()
        writefile.close()

    def split_sentences(self,line):
        correction_line = ""
        tokens = line.split()
        for token in tokens:
            if token.lower() in self.features:
                correction_line = correction_line +str(' ')+ token
                continue
            b = Word(token)
            possible_values = b.spellcheck()
            result = possible_values[0][0]
            for word in possible_values:
                if word[0].lower() in self.features:
                    result = word[0]
                    break
            correction_line = correction_line +str(' ')+ result
        return correction_line
folder = "../input"
op="../output"
files = os.listdir(folder)
for file in files:
    obj =  SentenceCorrection(folder+"/"+file,op+"/"+file)
    obj.load_sentences()

