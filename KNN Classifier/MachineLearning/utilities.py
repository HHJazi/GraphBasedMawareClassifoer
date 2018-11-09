'''
Created on Nov 22, 2015

@author: hossein
'''
import json as simplejson

import os
import math
import ConfigParser
import shutil
import io

class utility(object):
    '''
    classdocs
    '''

    workSpacePath = "/home/hossein/Documents/LiClipse Workspace/CuckooTOMist/"
    def __init__(self):
        '''
        Constructor
        '''
        config = ConfigParser.RawConfigParser()
        config.read(self.workSpacePath + 'conf/Cfg-General.cfg')
        #self.binaryReportsPath = "/home/ebiglarb/duckto/all"
        #self.binaryReportsPath = "/home/ebiglarb/academic/cuckoo2mist/cuckoo2MIST_new/Cuckoo2mist_v2.0/CuckooTOMist/numericBinary"
        self.binaryReportsPath = '/home/hossein/Documents/LiClipse Workspace/CuckooTOMist/temp/'
        self.outputPath = self.workSpacePath + 'Outputs/ML/'

    def loadPreviousProtoInfo(self):
        prototypes = {}
        print os.path.join(self.outputPath, 'prototypes')
        prototypes = self.read(os.path.join(self.outputPath, 'prototypes'))
        return prototypes

    def readBinaryReport(self, previousProto):
        reports = []
        reportSize = 0
        # first we check if we have prototypes from the previous run we add them as reports
        if len(previousProto) > 0 : # incremental
            for pro in previousProto:
                pro['clusterID'] = None
                pro['members'] = []
                pro['IsProto'] = False
                reports.append(pro)


        with open("/home/hossein/Documents/LiClipse Workspace/CuckooTOMist/Outputs/ML/MapSamples.txt") as file:
                    content = file.readlines()
                    file.close()
                    reportSize = len(content)
        self.distanceMatrix = [[0 for i in range(reportSize)] for j in range(reportSize)]


        with open("/home/hossein/Documents/LiClipse Workspace/CuckooTOMist/Outputs/ML/labels") as file:
                    content2 = file.readlines()
                    file.close()


        for i in range(reportSize):
            temp = content[i].split('\t') # has the name of file
            try:
                label = content2[i].split('/') # has the label of each index
                family = label[1].split('.')
                family = family[0]
            except:
                label = content2[i].split('\t')
                family = label[1]

            report = {
                              'ID': temp[0],
                              'value': None,
                              'clusterID': None,
                              'members':[],  # this is used when the report is a prototype, and keeps track of its members
                              'IsProto': False,
                              'label': family
                              }
            reports.append(report)
        return reports




    def writeDistances(self, distances, output):
        output = os.path.join(self.outputPath, output)
        print "writing distances into " + output + "..........\n"
        try:
            with open(output, 'w') as outfile:
                size = len(distances)
                for i in range(size):
                    if i == 0:
                        header = ""
                        for j in range(size):
                            header = header + str(j) + "                        "
                        #outfile.write(header + "\n")

                    row = ""
                    temp = distances[i]
                    row = '    '.join(str(x) for x in temp)
                    outfile.write(row + "\n")
        except:
            print "Error! can not write distance file"
        print "finished writing distances....."
        return


    def writeClusters(self, clusters, output):
        output = os.path.join(self.outputPath, output)
        print "writing clusters into " + output + "..........\n"
        try:
            with open(output, 'w') as outfile:
                outfile.write("<Cluster>            <report>            <distance>\n")
                for cluster in clusters:
                    if len(cluster['members'])==0:
                        outfile.write("C-"+cluster['ID'] + "            " + "-" + "            " + "-" + "\n")
                    else:
                        for member in cluster['members']:
                            outfile.write("C-"+ str(cluster['ID']) + "            " + member['reportID'] + "        "+ str(member['distance']) + "\n")
        except Exception, e:
                print "can not write clusters \n"
        print "finished writing clusters ....."
        return

    def writeProtos(self, prototypes):
        output = os.path.join(self.outputPath, "prototypesss")
        print "writing proto into " + output + "..........\n"
        try:
            with open(output, 'w') as outfile:
                outfile.write("<Cluster>            <report>            <distance>\n")
                for pro in prototypes:
                    if len(pro['members'])==0:
                        outfile.write("C-"+pro['ID'] + "            " + "-" + "            " + "-" + "\n")
                    else:
                        for member in pro['members']:
                            outfile.write("C-"+ str(pro['ID']) + "            " + member['reportID'] + "        "+ str(member['distance']) + "\n")
        except Exception, e:
                print "can not write clusters \n"
        print "finished writing clusters ....."
        return

    def read(self, path):
        with open(path) as f:  # it is foo
            enReport = simplejson.load(f)
        f.close()
        return enReport


    def writePrototype(self, prototypes, mode):
        print "writing prototypes......"
        path = os.path.join(self.outputPath, 'prototypes')
        if mode:
            try:
                prev = self.read(path)  # check if prev is a list or not
                prev.extend(prototypes)
                with open(path, 'a') as outfile:
                    simplejson.dump(prev, outfile, indent=2)
            except Exception, e:
                print "can not write prototypes \n"
        else:
            try:
                with open(path, 'w') as outfile:
                    simplejson.dump(prototypes, outfile, indent=2)
            except Exception, e:
                print "can not write prototypes \n"
        print "Finished writing prototypes......"
        return True



    def writeRejectedReports(self, rejectedReports):
        print "copying rejected reports......"
        path = os.path.join(self.workSpacePath, 'Outputs/rejectedReports')
        try:
            for pro in rejectedReports:
                fileName = pro
                src = os.path.join(self.binaryReportsPath,fileName)
                dst = os.path.join(path, fileName)
                shutil.copyfile(src, dst)
        except Exception, e:
            print "can not copy prototypes22 \n"

        return True

    def printFalseLabels(self, string):
        with open(self.workSpacePath + 'Outputs/falselyLabeled', 'a') as file:
            file.write(string)
            file.close()






