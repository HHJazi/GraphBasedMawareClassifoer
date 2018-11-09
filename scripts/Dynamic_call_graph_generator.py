'''
Created on Aug 19, 2015

@author: root
'''
'''
Created on Jun 18, 2015
process the dynamic assembly file and generate call graph in png, pdf and dot format
@author: Hossein Hadian
'''

import os
import networkx as nx
import matplotlib.pyplot as plt


assemblyReportFolder = '/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/hossein-problem/'  # remove the trailing '\'
TEMU = True
DECAF = False
PinTool = False
S2E = False
for fileName in os.listdir(assemblyReportFolder):  # go to folder containing cuckoo reports and extract their json files and copy them into a Json Folder path
    try:
        assemblyFilePath =  assemblyReportFolder + fileName + '/'+ 'new'+fileName + '.trace.disasm'
        print "file is:" + 'new'+fileName + '.trace.disasm'
    except:
        print "error"
    with open(assemblyFilePath, 'r+') as fo:
        sampleDiGraph = nx.DiGraph()
        lineElements = []
        #addressesList = []
        functionsList = []
        stack= []
        firstInstruction = True
        firstRet = True
        #file processing
        #TEMU file processing
        if(TEMU == True):
            i = 0
            line = fo.readline()
            while(line):
                    lineElements = line.split()
                    #first instruction processing
                    if(firstInstruction == True):
                        currentInstruction = lineElements[1]
                        if(currentInstruction == "call" or currentInstruction == "calll"):
                            #addressesList.append(lineElements[0].replace(":", ""))
                            nodeName = lineElements[7]
                            if( '@' in nodeName or '[' in nodeName or ']' in nodeName):
                                nodeName = lineElements[9]
                            #nodeName = nodeName.rpartition(':')
                            sampleDiGraph.add_node(nodeName[2])
                            stack.append(nodeName[2])
                            currentFunction = nodeName[2]
                            firstInstruction = False
                        elif(currentInstruction == "ret" or currentInstruction == "retl"):
                            sampleDiGraph.add_node(lineElements[4])
                            firstInstruction = False
                            stack.append(lineElements[4])
                            currentFunction = lineElements[4]
                            
                        else:
#                            addressesList.append(lineElements[0].replace(":", ""))
                            sampleDiGraph.add_node("sub_" + lineElements[0].replace(":", ""))
                            firstInstruction = False
                            stack.append("sub_" + lineElements[0].replace(":", ""))
                            currentFunction = "sub_" + lineElements[0].replace(":", "")
                    # other instructions processing 
                    else:
                        #process call instructions
                        currentAddress = lineElements[0].replace(":", "")
                        try:
                            currentInstruction = lineElements[1]
                        except: 
                            print "error"
                            print line
                            line = fo.readline() 
                            continue
                        # addressesList.append(currentAddress)
                        if(currentInstruction == "call" or currentInstruction == "calll"):
                                # addressesList.append(lineElements[0].replace(":", ""))
                                try:
                                    nodeName = lineElements[7]
                                    if( '@' in nodeName or '[' in nodeName or ']' in nodeName):
                                        nodeName = lineElements[9]
                                except:
                                    nodeName = "unknown"
                                #nodeName = nodeName.rpartition(':')
                                stack.append(nodeName)
                                sampleDiGraph.add_node(nodeName)
                                sampleDiGraph.add_edge(currentFunction, nodeName)
                                currentFunction = nodeName
                            #proceed the calls which are using the address
                        elif(currentInstruction == "push" or currentInstruction == "pushl"):
                            line = fo.readline()
                            lineElements = line.split()
                            #addressesList.append(lineElements[0].replace(":", ""))
                            currentAddress = lineElements[0].replace(":", "")
                            currentInstruction = lineElements[1]
                            if(currentInstruction == "ret" or currentInstruction == "retl"):
                                # addressesList.append(lineElements[0].replace(":", ""))
                                addressPart = lineElements[2]
                                if(addressPart[0][0] == '$'):
                                    nodeName = lineElements[7]
                                    sampleDiGraph.add_node(nodeName)
                                    stack.append(nodeName)
                                    sampleDiGraph.add_node(nodeName)
                                    sampleDiGraph.add_edge(currentFunction, nodeName)
                                    currentFunction = nodeName
                                else:
                                    nodeName = lineElements[4]
                                    sampleDiGraph.add_node(nodeName)
                                    stack.append(nodeName)
                                    sampleDiGraph.add_node(nodeName)
                                    sampleDiGraph.add_edge(currentFunction, nodeName)
                                    currentFunction = nodeName
                            elif(currentInstruction == "call" or currentInstruction == "calll"):
                                continue
                        elif(currentInstruction == "ret" or currentInstruction == "retl"):
                                try:
                                    stack.pop()
                                    currentFunction = stack[-1]
                                except:
                                    print "ok"
                    line = fo.readline()   
            nx.write_dot(sampleDiGraph, assemblyFilePath + ".dot")
            print("end of file")
            print("Wrote" + assemblyFilePath + ".dot")
            

                       
                                
                            
                            

                            

                    
                    
                
        