'''
Created on Jul 14, 2015

@author: root
'''
import subprocess
import os
import time

time.sleep(10)
for filename in os.listdir('\\10.0.2.2\samba\\'):
#for filename in os.listdir('/srv/ftp/samba/test/'):
    os.chdir( 'c:\\documents and settings\\flow_model' )
    os.system( '"C:\\Documents and Settings\\flow_model\\flow.exe"' )
   
