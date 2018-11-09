from subprocess import Popen, PIPE, STDOUT



p = Popen([r"/home/bitblaze/temu-1.0/tracecap/temu","-snapshot","-monitor","stdio","-m","1024", "/home/hossein/windowsxp.qcow"],stdout=PIPE, stdin=PIPE)
stdout_data = p.communicate(input='load_plugin /home/bitblaze/temu-1.0/tracecap/tracecap.so')[0]
print(stdout_data)
stdout_data1 = p.communicate("load_plugin /home/bitblaze/temu-1.0/tracecap/tracecap.so")[1]
print(stdout_data1)
stdout_data2 = p.communicate("enable_emulation")[2]
print(stdout_data2)
stdout_data3 = p.communicate("tracebyname foo.exe /home/hossein/traceexamples/python/foopython.trace")[3]
print(stdout_data3)
stdout_data4 = p.communicate("trace_stop")[4]
print(stdout_data4)