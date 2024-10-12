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


read = open('Area.txt').read().split("\n")
print(read)

xpoints = []
xpoints_epoch = []
ypoints = []

for f in read:
    area = int(f.split(":")[1])
    year = int(f[0:4])
    # print("year",year)
    if year < 1998: continue
    print(f[0], area)
    ypoints.append(area/100000)
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

plt.title("Cobertura de neve na área de estudo por ano")
# plt.title("NDSI médio na área de estudo")
plt.xlabel("Data")
plt.ylabel("Área coberta (KM²)")
# plt.ylabel("NDSI")

xpoints_epoch = np.array(xpoints_epoch)
#print(xpoints_epoch)
z = np.polyfit(xpoints_epoch, ypoints, 1)
p = np.poly1d(z)

plt.scatter(xpoints, ypoints)
plt.plot(xpoints, p(xpoints_epoch),color='red')

plt.gca().get_yaxis().set_major_formatter(ticker.StrMethodFormatter("{x:.0f}"))#*10⁵
plt.gca().yaxis.major.formatter._useMathText = True

plt.gcf().autofmt_xdate()
plt.tight_layout()
plt.show()

print(xpoints)
print(xpoints_epoch)
print(ypoints)