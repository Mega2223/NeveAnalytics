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


read = open('AreaPeriod.txt').read().split("\n")
# print(read)


col = ['orange','brown','blue','green']
stations = {
    "summer":[-1, 12, 21, 0, 3, 20],
    "fall":[0, 3, 21, 0, 6, 20],
    "winter":[0, 6, 21, 0, 9, 22],
    "spring":[0, 9, 23, 0, 12, 20]
}
stations = [stations["summer"], stations["fall"], stations["winter"], stations["spring"]]
station_names = ['Verão','Outono','Inverno','Primavera']

for m in range(0,4):
    xpoints = []
    xpoints_epoch = []
    ypoints = []
    for f in read:
        if not ':' in f: continue
        area = int(f.split(":")[1])
        print(f)
        ystart, mstart, dstart, yend, mend, dend = int(f[10:14]), int(f[15:17]), int(f[18:20]), int(f[24:28]), int(f[29:31]), int(f[32:34])
        print(ystart,mstart,dstart,"->",yend,mend,dend)
        start = datetime.date(ystart, mstart, dstart)
        end = datetime.date(yend, mend, dend)
        middle = (start + 0.5*(end-start))

        station_min = datetime.date(stations[m][0]+end.year,stations[m][1],stations[m][2])
        station_max =  datetime.date(stations[m][3]+end.year,stations[m][4],stations[m][5])
        print('station',stations[m],'=',str(station_min),'->',str(station_max))
        print("year",start.year,"month",start.month,"day",start.day,"-> year",end.year,"month",end.month,"day",end.day )
        if start.year < 1998 or middle < station_min or middle > station_max: continue
        ypoints.append(area/100000)
        xpoints_epoch.append(
            int(
                calendar.timegm(
                    middle.timetuple()
                )
            )
        )
        xpoints.append(
           middle.year + middle.month / 12 + middle.day / 30.5
        )

    ypoints = np.array(ypoints)
    # plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%Y'))

    plt.title("Cobertura de neve na área de estudo por estação")
    # plt.title("NDSI médio na área de estudo")
    plt.xlabel("Data")
    plt.ylabel("Área coberta (KM²)")
    # plt.ylabel("NDSI")

    xpoints_epoch = np.array(xpoints_epoch)
    #print(xpoints_epoch)
    z = np.polyfit(xpoints_epoch, ypoints, 1)
    p = np.poly1d(z)

    plt.scatter(xpoints, ypoints,color=col[int(m)])
    plt.plot(xpoints, p(xpoints_epoch),color=col[int(m)], label = station_names[m])

    plt.gca().get_yaxis().set_major_formatter(ticker.StrMethodFormatter("{x:.0f}"))#*10⁵
    plt.gca().yaxis.major.formatter._useMathText = True

    plt.gcf().autofmt_xdate()

plt.tight_layout()
plt.legend()
plt.show()
