'''
Created on Nov 22, 2015

@author: hossein
'''

# coding: utf-8

import os, sys
import json
import ConfigParser
# -*- coding: utf-8 -*-
normalSample = "/home/hossein/academic/thesis/CuckooTOMist/input/single/normal-foo.json"

def write( config, path):
    # Writing our configuration file to 'example.cfg'
    with open(path, 'wb') as configfile:
        config.write(configfile)


def mlConfig():
    config = ConfigParser.RawConfigParser()
    
    config.add_section('prototypes')
    config.set('prototypes', 'max_dist', 0.65) # maximum distance to a prototype
    config.set('prototypes', 'max_num', 0) 
    ''' During analysis prototypes are selected until this value is reached. If too many prototypes are determined,
     this parameter can be used to reduce computational
     costs at the price of a coarser approximation. If set to 0 this parameter is ignored.
     '''
    config.add_section('cluster')
    config.set('cluster', 'link_mode', "complete" )
    config.set('cluster', 'min_dist', 0.95 )
    config.set('cluster', 'reject_num', 0 )
    config.set('cluster', 'shared_ngrams', 0.0 )
    
    config.add_section('input')
    config.set('input', 'format', "text" )
    config.set('input', 'mist_level', 0 )
    config.set('input', 'mist_rlen', 0 )
    config.set('input', 'mist_tlen', 0 )
    
    config.add_section('features')
    config.set('features', 'firstLevel', 1)
    config.set('features', 'secondLevel', 3)
    config.set('features', 'thirdLevel', 7)
    config.set('features', 'dynamicBehavior', 20)
    
    
    config.set('features', 'ngram_len', 2 )
    config.set('features', 'ngram_delim', "%20%0a%0d" )
    config.set('features', 'vect_embed', "bin" )
    config.set('features', 'lookup_table', 0 )
    config.set('features', 'hash_seed1', 0x1ea4501a )
    config.set('features', 'hash_seed2', 0x75f3da43 )
    
    config.set('features', 'firstLevelFeatureNo', 3 )
    config.set('features', 'secondLevelFeatureNo', 23 )
    config.set('features', 'thirdLevelFeatureNo', 35 )

    config.add_section('classify')
    
    config.set('classify', 'max_dist', 0.68)

    write(config,'conf/machineLearningConf.cfg')
    

if __name__ == '__main__':

    
    mlConfig()



    #IDT()

    #DeviceTree()



