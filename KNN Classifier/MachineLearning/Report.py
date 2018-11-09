'''
Created on Nov 22, 2015

@author: hossein
'''
import ConfigParser
import sys
import math
from cuckooToMist.cuckoo2mist import workSpacePath


class report(object):
    '''
    classdocs
    '''


    def __init__(self, value, ID, firstLCo , secondLCo, thirdLCo, dynamicCo, maxDistance):
        '''
        Constructor
        '''
        self.ID = ID
        self.value = value
        self.clusterID = None
        self.members = [] # this is used when the report is a prototype, and keeps track of its members 
        self.IsProto = False
        
  
        
    def reportsDistance(self, dstReport):
        dist = -1
        resultVec = []
        dstContent = dstReport.value
        try:
            for item in dstContent['firstLRules']:
                
                if item == "Dynamic MIST Sequence" :
                    reg = self.strDist(self.value['firstLRules'][item] , dstContent['firstLRules'][item])
                    resultVec.append(reg * self.dynamicBehaviorCoef)

                else:
                    reg = self.strDist(str(self.value['firstLRules'][item]) , str(dstContent['firstLRules'][item]))
                    resultVec.append(reg * self.firstLevelCoef)
            
            for item in dstContent['secondLRules']:
                reg = self.strDist(str(self.value['secondLRules'][item]) , str(dstContent['secondLRules'][item]))
                resultVec.append(reg * self.secondLevelCoef)
            
            for item in dstContent['thirdLRules']: 
                
                if item == 'Frequency of Access/Modifictaion on ASEP':
                    try:
                        reg = self.asepDist(str(self.value['thirdLRules'][item]) , str(dstContent['thirdLRules'][item]))
                        resultVec.append(reg * self.thirdLevelCoef)
                    except:
                        print "Debgug" + self.ID + " and " + dstReport.ID
                else: 
                    reg = self.strDist(str(self.value['thirdLRules'][item]), str(dstContent['thirdLRules'][item]))
                    resultVec.append(reg * self.thirdLevelCoef)
        except:
            print "error in feature :" 
            print item
        sum = 0.0 

        for i in range(len(resultVec)):
            sum = sum + (resultVec[i]**2)
        dist = math.sqrt(sum)/self.maxDistance
        
#         print "****************************************************"
#         print "length: " + str(len(resultVec)) + " sum : " + str(sum)
#         print "the dist vector between " + self.ID + "and " + dstReport.ID + "is :"
#         print resultVec
#         print "****************************************************"
        return dist 
    
    def asepDist(self, src, dst):
        disVecval = []
        if len(src)==0 and len(dst)==0:
            return 0
        
        src = src.split(" ")
        dst = dst.split(" ")
        try:
            src.remove("")
        except:
            pass
        try:    
            dst.remove("")
        except:
            pass
        
        Max = max(len(src),len(dst))
        Min = min(len(src),len(dst))   
        
        if len(src) == Max:
            LongerList = src
        else:
            LongerList = dst
        if len(src)== 0 or len(dst)==0:
            for i in LongerList:
                if i !=0:
                    disVecval.append(0)
                else:
                    temp = i.split("|")
                    disVecval.append(temp[1]) # maximum possible value for a key frequency 
        else:
            for i in range(Min):
                s = src[i].split("|")
                d = dst[i].split("|")
                if len(s)!=2:
                    if len(d)!=2: # both are 0 
                        disVecval.append(0)
                    else:
                        disVecval.append(d[1])
                else:
                    if len(d)!=2: # d is  0 and s has key
                        disVecval.append(s[1])
                    else: # both has key 
                        disVecval.append(math.fabs(int(d[1])-int(s[1])))
               
            temp = Max-Min
            for i in range(temp):
                d = LongerList[Min+i].split("|")
                if len(d)==2:
                    disVecval.append(d[1])
                else:
                    disVecval.append(0)
        
        normFactor = math.sqrt((50*50) * len(disVecval)) # we suppose the average maximum frequency is 50 so we calculate the maximum possible distance 
        sum = 0
        for i in range(len(disVecval)): # calculate euclidean distance 
            sum = sum + disVecval[i]* disVecval[i]
        
        temp = math.sqrt(float(sum**2) / float(normFactor))
        return temp 

        
    def strDist(self, srcStr, dstStr):
        srcStr = list(srcStr)
        dstStr = list(dstStr)
        disVecval = []
        if len(srcStr)==0 or len(dstStr)==0:
            return 1
                        
        Max = max(len(srcStr),len(dstStr))
        Min = min(len(srcStr),len(dstStr)) 
        
        if len(srcStr) == Max:
            LongerList = srcStr
        else:
            LongerList = dstStr 
                      
        for i in range(Min):
            if srcStr[i] == dstStr[i]:
                disVecval.append(0)
            else:
                disVecval.append(1)
        
        temp = Max-Min
        for i in range(temp):
            if LongerList[Min+i]=='1':
                disVecval.append(1)
            else:
                disVecval.append(0)
        
        normFactor = len(disVecval)
        sum = 0
        for i in range(len(disVecval)):
            sum = sum + disVecval[i]
        temp = math.sqrt(float(sum**2) / float(normFactor**2)) # normalized part   
        return  temp   