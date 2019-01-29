import os
import numpy as np
from lxml import html,etree
import json
import codecs
import math
from bs4 import BeautifulSoup
from nltk.tokenize import RegexpTokenizer
from nltk.corpus import stopwords
from nltk.corpus import wordnet

if __name__ == "__main__":
    path = os.getcwd() + '/WEBPAGES//WEBPAGES_RAW//bookkeeping.json'
    tokenizer = RegexpTokenizer('\w[a-z]{3,}')
    stop_words = set(stopwords.words('english'))
    inverted_index = {}
    
    with open(path) as f:
        data = json.load(f) # load bookkeeping
        
    docsCrawled = 0
    for x in data: # x is the directory folder/file number, data[x] is the url
        try:
            url_folder = x.split('/') # url_folder[0] <-- docID?
            traversal = os.getcwd() + '/WEBPAGES//WEBPAGES_RAW//' + url_folder[0] + '//'
            extract = traversal + url_folder[1] 
            f = codecs.open(extract, 'r', 'utf-8') # use some load function with traversal path + x[1] to extract the HTML
            soup = BeautifulSoup(f, 'html.parser')
            tokens = tokenizer.tokenize(soup.get_text(" ", strip=True).lower())
            filteredTokens = []
            
            for w in tokens:
                if w not in stop_words:
                        filteredTokens.append(w)
            
            for w in filteredTokens:
                if inverted_index.get(w) != None: # if index for word w exists already in inverted_index
                    if (data[x], filteredTokens.count(w)/len(filteredTokens)) not in inverted_index[w]: # if this url does not yet exist in word w's list
                        inverted_index[w].append((data[x], filteredTokens.count(w)/len(filteredTokens)))
                else: # else, index for word w doesn't exist yet--create it
                    inverted_index[w] = [(data[x], filteredTokens.count(w)/len(filteredTokens))]
        except:
            print("error")
            continue
                
        docsCrawled = docsCrawled + 1
        if docsCrawled % 100 == 0:
            print(docsCrawled)
            if docsCrawled == 5000:
                break
        
    writer = open('inverted_test.txt', 'w')
    
    for key in inverted_index:
        try:
            writer.write(key + " - ")
            i = 1
            for doc, tf in inverted_index[key]:
                if i == len(inverted_index[key]):
                    writer.write(doc + "," + str(tf * math.log10(37497/len(inverted_index[key]))) + "\n")
                else:
                    writer.write(doc + "," + str(tf * math.log10(37497/len(inverted_index[key]))) + " ; ")
                i = i + 1
        except:
            print("write to file error at " + key)
            continue
            
    writer.close()
    
    analytics = open('analytics.txt', 'w')
    analytics.write(str(docsCrawled) + "\n" + str(len(inverted_index)) + "\n")
    analytics.close()
