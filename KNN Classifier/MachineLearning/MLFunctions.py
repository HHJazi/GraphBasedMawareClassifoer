'''
Created on Nov 22, 2015

@author: Hossein
'''
from __future__ import division
import os, sys
import ConfigParser
from MachineLearning import Report
import json as simplejson
import math
from platform import dist

from MachineLearning import utilities
from operator import itemgetter
import shutil
import csv



class ml(object):
    workSpacePath = "/home/hossein/Documents/LiClipse Workspace/CuckooTOMist/"
    def __init__(self, classif=0, cluster=0, proto=0):
        self.util = utilities.utility()

        self.mode = False
        config = ConfigParser.RawConfigParser()
        config.read(self.workSpacePath +'conf/machineLearningConf.cfg')


        self.max_dist_prototype = proto
        self.min_dist_cluster = cluster
        self.max_dist_classify = classif

        self.reject_num = 6
        self.max_num = 100
        print "Setting...."
        print "max_dist_prototype  " + str(self.max_dist_prototype)
        print "min_dist_cluster  " + str(self.min_dist_cluster)
        print "max_dist_classify  " + str(self.max_dist_classify)
        print "self.reject_num  " + str(self.reject_num)


        self.reports = []
        self.reportSize = 0 # number of current reports + previous prototypes if we are increamental
        self.reportSizeOriginal = 0 # number of reports
        self.prototypes = []
        self.prototypesName = []
        self.rejected_reports = []
        self.rejected_reports_no=0

        self.matrixInfo = []
        self.distanceMatrix =[[]]

        self.maxFeatureValue = {}

    def remove_pro(self, zz):
        rPro = self.prototypes[zz]
       #self.prototypes.remove(rPro)
       # self.prototypesName.remove(rPro.ID)
        rPro['IsProto'] = False

    def update_distances(self, distances, i, j):
        distances[i][j] = -1
        if i !=j:
            distances[j][i] = -1

    def assignClusterToReport(self, reportX, clusters):
        minDist = 999999
        protoInx = -1
        for i in range(len(self.prototypes)):
            dist = self.getreportsDistance(reportX, self.prototypes[i])
            if  dist < minDist:
                minDist = dist
                protoInx = i
        # check if the closest proto is very far(more than the threshold) from the cluster (doing classification part here)
        if minDist == 999999:
            print "ERROR there is no cluster to assign!!!"
            return None
        if minDist > self.max_dist_classify :
            self.rejected_reports_no = self.rejected_reports_no + 1
            self.rejected_reports.extend(reportX['ID'])
            reportX['clusterID'] = None
        else:
            member = {
                      'reportID' : reportX['ID'],
                      'distance': minDist,
                      'label': reportX['label']
                      }
            self.prototypes[protoInx]['members'].append(member)
            reportX['clusterID'] = self.prototypes[protoInx]['clusterID']

    def cluster(self, outFile):
        clusters = []
        clusterID  = -1
        distances = [[0 for i in range(len(self.prototypes))] for j in range(len(self.prototypes))]
        for i in range(len(self.prototypes)):
            z = self.prototypes[i]
            for j in range((i+1), len(self.prototypes)):
                zz = self.prototypes[j]
                dis = self.getreportsDistance(z, zz)
                distances[i][j] = dis
                distances[j][i] = dis

        prevSize = len(self.prototypes)
        mergeCount = 0
        minDist, i, j = self.min_distance(distances)
        while minDist < self.min_dist_cluster:
#             if (i == 247 or i == 266) and (j == 247 or j == 266 ):
#                 print "erorrrrrrrr"
            try:
                #print "merging " + str(i)+" and " + str(j)
                mergeCount += 1
                mergeHappend = False

                if self.prototypes[j]['clusterID'] !=None and self.prototypes[i]['clusterID']!=None:
                    self.update_distances(distances, i, j) # we should remove any distance between j to others or others to j
                    minDist, i, j = self.min_distance(distances)
                    continue
                    c1 = clusters[self.prototypes[i]['clusterID']]
                    c2 = clusters[self.prototypes[j]['clusterID']]
                    if c1!=c2: # no need to merge
                        canNotMerge = False
                        for pro1 in c1['protos']:

                            for pro2 in c2['protos']:

                                dist = self.getreportsDistance(pro1, pro2)
                                if dist > self.min_dist_cluster:
                                    canNotMerge = True
                                    break
                            if canNotMerge == True:
                                break
                        if canNotMerge == False:
                            for pro in c2['protos']:
                                pro['clusterID'] = c1['ID']
                            c2['ID'] = 'none'
                            c1['protos'].extend(c2['protos'])


                elif self.prototypes[i]['clusterID']!=None:
                    c = clusters[self.prototypes[i]['clusterID']]
                    if not self.prototypes[j] in c['protos']:
                        for pro in c['protos']:

                            dist = self.getreportsDistance(pro, self.prototypes[j])
                            if dist > self.min_dist_cluster:
                                break
                        if dist<= self.min_dist_cluster:
                            c['protos'].append(self.prototypes[j])
                            self.prototypes[j]['clusterID'] = c['ID']

                elif self.prototypes[j]['clusterID'] !=None:
                    c = clusters[self.prototypes[j]['clusterID']]
                    if not self.prototypes[i] in c['protos']:
                        for pro in c['protos']:

                            dist = self.getreportsDistance(pro, self.prototypes[i])
                            if dist > self.min_dist_cluster:
                                break
                        if dist <= self.min_dist_cluster:
                            c['protos'].append(self.prototypes[i])
                            self.prototypes[i]['clusterID'] = c['ID']
                else:
                    clusterID = clusterID+1
                    cluster = {}
                    cluster['ID'] = clusterID
                    cluster['protos'] = []
                    cluster['protos'].append(self.prototypes[i])
                    cluster['protos'].append(self.prototypes[j])
                    self.prototypes[i]['clusterID'] = clusterID
                    self.prototypes[j]['clusterID'] = clusterID
                    clusters.append(cluster)

                self.update_distances(distances, i, j) # we should remove any distance between j to others or others to j
                minDist, i, j = self.min_distance(distances)
            except:
                print "could not merge " + str(i) + " and " + str(j)
                break
        rowsLength = len(distances)

        for pro in self.prototypes:

            if pro['clusterID']==None:
                clusterID = clusterID+1
                cluster = {}
                cluster['ID'] = clusterID
                cluster['protos'] = []
                cluster['protos'].append(pro)
                pro['clusterID'] = clusterID
                clusters.append(cluster)

        print str(mergeCount) + " protos out of " + str(prevSize) + " were merged"


        #shouldBeRemoved = []
        for indx in range(len(self.prototypes)):
            x = self.prototypes[indx]

            member = {
                          'reportID' : x['ID'],
                          'distance': 0,
                          'label': x['label']
                          }
            x['members'].append(member)
            #x['clusterID'] = x['ID']
#         k = 0
#         for indx in shouldBeRemoved:
#             del self.prototypes[indx-k]
#             k +=1
        '''
        find nearest proto to this report
        also add this report to that proto member list
        update clusterID attribute of this report
        '''
        for x in self.reports:
            if x['IsProto']==False:
                self.assignClusterToReport(x, clusters) # and

        shouldBeRemovedIDs = []
        for cInx in range(len(clusters)):
            if clusters[cInx]['ID']=='none':
                shouldBeRemovedIDs.append(cInx)
            members = []
            for proto in clusters[cInx]['protos']:
                x = self.findReportByID(proto['ID'])
                members.extend(x['members'])
            if len(members) < self.reject_num:
                self.rejected_reports_no +=len(members)
                for mem in members:
                    self.rejected_reports.append(mem['reportID'])
                shouldBeRemovedIDs.append(cInx)
                for i in range(len(members)):
                    x = self.findReportByID(members[i]['reportID'])
                    if x:
                        x['clusterID'] = None
#                         self.rejected_reports.append(x['ID'])
            else:
                clusters[cInx]['members'] = members
                    #self.prototypes.remove(c)
        k = 0
        for indx in shouldBeRemovedIDs:
            del clusters[indx-k]
            k +=1
        self.util.writeClusters(clusters,  outFile)
        self.util.writeRejectedReports(self.rejected_reports)
        return clusters

    def findReportByID(self, ID):
        for i in range(len(self.reports)):
            if self.reports[i]['ID'] == ID:
                return  self.reports[i]
        print "we could not find your requested reports!\n"
        print ID
        return None

    def prototype_gonzalez(self):

        distance = [] # is the distance between each graph to its closest prototype
        for i in range(self.reportSize):
            distance.append(999999) # is equal to

        maxDist, index = self.max_distance(distance)
        while maxDist > self.max_dist_prototype:
            for i in range(self.reportSize):
                if i!=index:
                    disToCandidate = self.getreportsDistanceByIndex(index, i) # hossein
                    if distance[i] > disToCandidate:
                        distance[i] = disToCandidate
            distance[index] = 0 # since this report is selected as a prototype so its distance to the nearest proto is 0 i.e. itself . other wise this report would always be selected as the next prototype!
            self.prototypes.append(self.reports[index]) # should be actual graph with index of IndeX
            self.reports[index]['IsProto'] = True
            maxDist, index = self.max_distance(distance)
        #print "no of clusters: " + str(len(self.prototypes))
        return True


    '''
    finds two reports with the minimum distance to each other
    receive a two dimentional array as input
    returns the index of these two reports with their distance
    '''
    def min_distance(self, distances):
        minDist = 999999
        z = -1
        zz = -1
        if len(distances)> 1: # there are at least 2 protos counts the row of the array not the whole
            rowsLength = len(distances)
            for i in range(rowsLength): # this is size of rows
                for j in range(i+1, rowsLength):
                    if i!=j:
                        if  distances[i][j]!=-1 and (distances[i][j] < minDist): # members with -1 as distance means removed or merged clusters so they are no longer considered as prototype
                            minDist = distances[i][j]
                            z = i
                            zz = j
            return minDist, z, zz
        else:
            return minDist, z, zz



    '''
    finds a report with the maximum distance to its prototype and returns that distance
    '''
    def max_distance(self, distance):
        maxDist = 0
        ID = -1
        for i in range(len(distance)):
            if distance[i] > maxDist:
                maxDist = distance[i]
                ID = i
        return maxDist, ID

    def getreportsDistanceByIndex(self, srcInx, dstInx):
        return float(self.distanceMatrix[srcInx][dstInx])

    def getreportsDistance(self, src, dst):
        for i in range(len(self.reports)):
            if self.reports[i]['ID'] == src['ID']:
                x = i
            if self.reports[i]['ID'] == dst['ID']:
                y = i
        return self.getreportsDistanceByIndex(x, y)

    def retriveDistance(self):
        with open(self.workSpacePath + 'Outputs/ML/distances.txt') as file:
            content = file.readlines()
            file.close()
        reportSize = len(content)
        self.distanceMatrix = [[0 for i in range(reportSize)] for j in range(reportSize)]

        for i in range(self.reportSize):
            temp = content[i].split('\t')
            self.distanceMatrix[i] = temp


    def printToFile(self, P, R, F, clustersLen, tp, fp):
        P = P * 100
        R = R * 100
        F = F * 100
        with open(self.workSpacePath + "/Outputs/ML/evaluation.csv",  'a') as csvFile:
                csvWriter = csv.writer(csvFile, delimiter=',',
                            quotechar='|', quoting=csv.QUOTE_MINIMAL)
                csvWriter.writerow([tp, fp, P, R, F, self.max_dist_prototype, self.min_dist_cluster, self.max_dist_classify, clustersLen, self.rejected_reports_no])



    def printOutputInfo(self, P, R, F, clusterLen):

        P = P*100
        R = R * 100
        F = F * 100
        print "Number of cluster: " + str(clusterLen)
        print "Total Number of reports: " + str(self.reportSize)
        print "Total number of rejected reports: " + str(self.rejected_reports_no)
        print "Precision of clusters: "  + "%.2f" %P  + "%"
        print "Recall of clusters: " + "%.2f" %R  + "%"
        print "F-measure of clusters: " + "%.2f" %F + "%"

        #print "maximum report distance " + str(self.maxDistance)
       # print "\n Prototypes/Clusters Info:"
#         for pro in self.prototypes:
#             print "\n"
          #  print "cluster name: " + pro.ID + " # of members: " + str(len(pro.members)) + " [" + ', '.join(pro.members) + "]"
        #print "..............................................................."
#         print "\n rejected reports: \n"
#         for rep in self.rejected_reports:
#             print rep['ID'] + ", "


    def evaluate(self, clusters):

        n = self.reportSize - self.rejected_reports_no
        P = 0
        R = 0
        F = 0
        if n == 0:
            return P, R, F, 0, 0
        sum = 0
        labelsClasses = {} # keeps the maximum number of reports with this label in a same cluster
        for c in clusters:
           # print c['members']
            labelsInCluster = {}
            for rep in c['members']:
                label = rep['label']
                try:
                    value = labelsInCluster[label]
                    value +=1
                    labelsInCluster[label] = value
                except:
                    labelsInCluster[label] = 1
            max = 0
            for label in labelsInCluster:
                value = labelsInCluster[label]
                if value > max:
                    max = value # used for calculating P
                try:
                    valueInClassMap = labelsClasses[label]
                    if value> valueInClassMap:
                        labelsClasses[label] = value
                except:
                    labelsClasses[label] = value
            sum = sum + max

        P = float(sum/n)

        ''' when we exited all clusters'''
        sum2 = 0
        for label in labelsClasses:
            value = labelsClasses[label]
            sum2 = sum + value
        R = float(sum2/n)
        if P+R!=0:
            F = ((2 * P * R) / (P+R) )
        if F>1  or R>1 or P >1:
            print "Error in evaluation!"


        trueLabel = 0
        falseLabel = 0
        rejected = 0
        print "falsely labeled samples:"
        for pro in self.prototypes:
            for rep in pro['members']:
                x = self.findReportByID(rep['reportID'])
                if x['clusterID']!=None:
                    label = rep['label']


                    newLabel = pro['label']

                    if newLabel == label:
                        trueLabel+=1
                    else:
                        falseLabel+=1
                        #self.util.printFalseLabels(rep['reportID'] + " with label "  + label +"  is assigned as:  " + newLabel + "\n")


                else:
                    rejected +=1
                    #print "!Rejected report : " + rep['reportID']
        if rejected!=self.rejected_reports_no:
            print "EROR in evaluation " + str(rejected) + str(self.rejected_reports_no)
        truePositive = float(trueLabel) / n
        falsePositive  = float(falseLabel) / n
        print "true positive: " + str(truePositive)
        print "false positive: " + str(falsePositive)
        return P, R, F, truePositive, falsePositive

    def main(self, command, outputFile, incrementalMode):
        self.mode = incrementalMode
        previousProto = []

        self.reports = self.util.readBinaryReport(previousProto)
        if self.reportSize == len(self.reports):
            print "there is no report to process!"
            return

        if incrementalMode==True:
            previousProto = self.util.loadPreviousProtoInfo() # hossein
            self.incrementalClassification(previousProto)
        else:
            self.reportSize = len(self.reports)  # hossein
            self.retriveDistance()
            print "start extracting prototype...."
            self.prototype_gonzalez()
            print"number of extracted prototypes: " + str(len(self.prototypes))
            print "start clustering prototypes...."
            clusters = self.cluster(outputFile)


            print "finished clustering..."






        # self.util.writePrototype(self.prototypes,'write')

        P , R, F, tp, fp = self.evaluate(clusters)
        self.printToFile( P, R, F, len(clusters), tp, fp)
        self.printOutputInfo(P , R, F, len(clusters))
        print "ALL Done"



def loop():
        for i in range(12000,17000):
            classif = float(i)
            for j in range(5000, 8000):
                proto = float(j)
                j = j +100
                for k in range(12000, 18000):
                    cluster = float(k)
                    k= k + 100
                    command = ml(classif, cluster, proto)
                    i = i + 100
                #command = ml(0, 0, 0)
                #command.maxFeatureValue = command.util.retriveMapStat()
                    command.main("cluster", 'clusterResults.txt', False)  # name of output file



if __name__ == '__main__':
    loop()
    #command = ml(0.0899, 0.1555, 0.012)
    #command = ml(15000,15000,6000)
    #command.main("cluster", "clusters.txt", False)  # name of output file

