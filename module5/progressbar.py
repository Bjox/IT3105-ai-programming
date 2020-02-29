import sys


def progressbar(length, current, max):
    if "stat" not in progressbar.__dict__: progressbar.stat = int(0)
    pc = current / max
    ipc = int(pc*100)
    #if ipc > 98: print("{} {}\n".format(ipc, pc), end="")
    if ipc == progressbar.stat: return
    progressbar.stat = ipc
    #if ipc > 98: print("{}\n".format(progressbar.stat), end="")
    pos = int(length * pc)
    sys.stdout.write("\r[")
    for i in range(length):
        sys.stdout.write("=" if i < pos else " ")
    sys.stdout.write("] {:.0f}%".format(pc*100))
    sys.stdout.flush()