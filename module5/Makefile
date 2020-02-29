

all:
	gcc -I/usr/include/python3.4/ -fpic -std=gnu99 -c -O2 -Wall progressbar.c
	gcc -shared -o progressbar.so progressbar.o

win:
	C:\Users\Viktor\Anaconda3\Scripts\gcc.bat -I/Users/Viktor/Anaconda3/include -std=gnu99 -c -O2 -Wall progressbar.c
	C:\Users\Viktor\Anaconda3\Scripts\gcc.bat -shared -DMS_WIN64 -L/Users/Viktor/Anaconda3/libs -lpython34 -o progressbar.dll progressbar.o
    # /Users/Viktor/Anaconda3/libs/python34.lib
