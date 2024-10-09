import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import matplotlib.ticker as ticker
import numpy as np
from os import listdir
from os.path import isfile, join
import datetime
import calendar
import json

def parse_date_epoch(name):
    date = name.split("_")[3]
    date = datetime.datetime(int(date[0:4]),int(date[4:6]), int(date[6:8]), 0,0,0)
    date = calendar.timegm(date.timetuple())
    return date
    
def parse_date(name):
    date = name.split("_")[3]
    date = datetime.datetime(int(date[0:4]),int(date[4:6]), int(date[6:8]), 0,0,0)
    return date


SRC_ROOT = json.loads(open("Configs.json").read())["sources_dir"]

data = open(SRC_ROOT+"\\Coverage.txt").read().split("\n")

xpoints = []
xpoints_epoch = []
ypoints = []

last_name = ''
for line in data:
    split = line.split(" ")
    if split[0] == 'SNOW':
        last_name = split[3].split(":")[0]
        continue
    if split[0] != 'TOTAL' or last_name.split("_")[2] != '232093': continue
    xpoints.append(parse_date(last_name))
    xpoints_epoch.append(parse_date_epoch(last_name))
    ypoints.append(float(split[3].split(",")[0]) * (1.0/1000.0) * (10 ** -5))
    


ypoints = np.array(ypoints)
plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%Y'))

plt.title("Cobertura de neve na área de estudo")
plt.xlabel("Data")
plt.ylabel("Área coberta (KM³)")

xpoints_epoch = np.array(xpoints_epoch)
#print(xpoints_epoch)
z = np.polyfit(xpoints_epoch, ypoints, 1)
p = np.poly1d(z)

plt.plot(xpoints, p(xpoints_epoch),color='red')
plt.scatter(xpoints, ypoints)

plt.gca().get_yaxis().set_major_formatter(ticker.StrMethodFormatter("{x:.2f}*10⁵"))
plt.gca().yaxis.major.formatter._useMathText = True

plt.gcf().autofmt_xdate()
plt.show()