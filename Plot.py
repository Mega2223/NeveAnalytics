import matplotlib.pyplot as plt
import numpy as np
from os import listdir
from os.path import isfile, join
import datetime
import calendar

def parse_date(name):
    date = name.split("_")[3]
    date = datetime.datetime(int(date[0:4]),int(date[4:6]), int(date[6:8]), 0,0,0)
    date = calendar.timegm(date.timetuple())
    return date

SRC_ROOT = "C:\\Users\\Imperiums\\Desktop\\Neve"

data = open(SRC_ROOT+"\\Coverage.txt").read().split("\n")

xpoints = []
ypoints = []

last_name = ''
for line in data:
    split = line.split(" ")
    if split[0] == 'SNOW':
        last_name = split[3].split(":")[0]
        continue
    if split[0] != 'TOTAL' or last_name.split("_")[2] == '232092': continue
    xpoints.append(float(parse_date(last_name)))
    ypoints.append(float(split[3].split(",")[0])) # I know, sorry
    

xpoints = np.array(xpoints)
ypoints = np.array(ypoints)

plt.xlabel("Data")
plt.ylabel("Metros cobertos")

plt.scatter(xpoints, ypoints)
z = np.polyfit(xpoints, ypoints, 1)
p = np.poly1d(z)
plt.plot(xpoints, p(xpoints))

plt.show()