__author__ = 'vijay'
from textblob import Word

class SentenceCorrection:
    def __init__(self,fileName,outputfile):
        self.fileName = fileName
        self.features = ['battery','screen','touch','multi touch','moto','motog','flipkart']
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

obj =  SentenceCorrection("/home/vijay/PycharmProjects/SentenceCorrection/input/processed.txt","/home/vijay/PycharmProjects/SentenceCorrection/output/result.txt")
obj.load_sentences()

