
import time   
from subprocess import Popen, PIPE
import os
from fileinput import filename
from random import randint

f = open('/media/Drive/Executionlogfile','a+')
f.write('Start loging file execution\n')
f.write('###########################################################################\n')
for filename in os.listdir('/home/hossein/newmaldir/'):
    #newpath = r'/media/Drive/releasemaliciaTraceOutputs/'+ filename 
    #if not os.path.exists(newpath): os.makedirs(newpath)
    sambadirectory = '/srv/ftp/samba/'
    for the_file in os.listdir(sambadirectory):
        file_path = os.path.join(sambadirectory, the_file)
        try:
            if os.path.isfile(file_path):
                os.unlink(file_path)
            #elif os.path.isdir(file_path): shutil.rmtree(file_path)
        except Exception, e:
            print e
    os.rename("/home/hossein/newmaldir/"+ filename, "/srv/ftp/samba/"+ filename)
    try:
		#running different variant of qemu due to limitation of qemu
        randnumber = randint(0,10)
        if(randnumber == 0):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp0.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 1):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp1.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 2):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp2.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 3):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp3.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 4):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp4.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 5):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp5.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 6):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp6.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 7):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp7.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 8):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp8.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 9):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp9.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)
        elif(randnumber == 10):
            p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot" ,"-monitor","stdio","-m","512", "/media/e50cbc04-f148-4002-8257-692f5682cdfc/home/hossein/windows/windowsxp10.qcow2"],stdout=PIPE, stdin=PIPE)
            time.sleep(300)

        args = iter(["enable_emulation\n","tracebyname " + filename + " /media/db5d0906-b027-4187-ac8d-df97319a23a6/ethertraces/" + filename +".trace\n","trace_stop\n", "quit\n"])
        p.stdin.write("load_plugin /home/bitblaze/temu-1.0/tracecap/tracecap.so\n")
#         localtime = time.localtime(time.time())
#         print "Local current time :", localtime
      
        for line in iter(p.stdout.readline,""):
        # if (qemu) is at the prompt, enter a command
            if line.startswith("(qemu)"):
#                 if(line.startswith("(qemu) Could not find snapshot")):
#                     print("Could not load vm")
#                     f.write("Could not load vm ")
#                     print("  for file" +filename)
#                     f.write(filename+'\n')
#                     time.sleep(300)
                arg = next(args,"")
            # if we have used all args break
                if not arg:
                    break
            # else we write the arg with a newline
                test = "tracebyname " + filename + " /media/db5d0906-b027-4187-ac8d-df97319a23a6/ethertraces/" + filename +".trace\n"
                if(arg == test ):
                    print("run the file")
                    f.write('run the file \n')
                    p.stdin.write(arg)
                    time.sleep(500)
                elif(arg == "trace_stop\n"):
                    p.stdin.write(arg)
                    time.sleep(70);
                    
                else:
                    p.stdin.write(arg)
                    time.sleep(0.3)
            f.write(line)
            print(line)# just use to see the output  
        print("Processing " + filename + " is done!")
        f.write("Processing " + filename + " is done!\n")
        os.remove("/srv/ftp/samba/"+ filename)
        print("file: "+ filename + " is deleted")
        f.write("file: "+ filename + " is deleted\n")
        f.write('###################################################################\n')
    except:
        print("there was an exception")
        f.write("there was an exception\n")
        continue
f.close()
    









