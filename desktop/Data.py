import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import matplotlib.ticker as ticker
import numpy as np
from os import listdir
import os
from os.path import isfile, join
import datetime
import calendar
import json
from utils import file_manager, data_utils, misc, calculator

def parse_date_epoch(name):
    date = name.split("_")[3]
    date = datetime.datetime(int(date[0:4]),int(date[4:6]), int(date[6:8]), 0,0,0)
    date = calendar.timegm(date.timetuple())
    return date
    
def parse_date(name):
    date = name.split("_")[3]
    date = datetime.datetime(int(date[0:4]),int(date[4:6]), int(date[6:8]), 0,0,0)
    return date

def isJSON(file: str):
    return file[len(file) - 5:len(file)].upper() == ".JSON"


read = json.loads(open("Configs.json").read())
print(read)
FILES = file_manager.doRecursiveSearch(read["src_dir"], isJSON)

xpoints = []
xpoints_epoch = []
ypoints = []

for f in FILES:
    act = open(f[1]+"\\"+f[0]).read()
    act = json.loads(act)
    mean = act["bands"][0]["mean"]
    year = int(f[0][0:4])
    if year < 1998: continue
    print(f[0], mean)
    ypoints.append(mean)
    xpoints_epoch.append(
        int(
            calendar.timegm(
                datetime.datetime(year, 1, 1, 0, 0, 0).timetuple()
            )
        )
    )
    xpoints.append(year)

ypoints = np.array(ypoints)
# plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%Y'))

# plt.title("Cobertura de neve na área de estudo")
plt.title("NDSI médio na área de estudo")
plt.xlabel("Data")
# plt.ylabel("Área coberta (KM²)")
plt.ylabel("NDSI")

xpoints_epoch = np.array(xpoints_epoch)
#print(xpoints_epoch)
z = np.polyfit(xpoints_epoch, ypoints, 1)
p = np.poly1d(z)

plt.plot(xpoints, p(xpoints_epoch),color='red')
plt.scatter(xpoints, ypoints)

plt.gca().get_yaxis().set_major_formatter(ticker.StrMethodFormatter("{x:.2f}"))#*10⁵
plt.gca().yaxis.major.formatter._useMathText = True

plt.gcf().autofmt_xdate()
plt.tight_layout()
plt.show()

print(xpoints)
print(xpoints_epoch)
print(ypoints)