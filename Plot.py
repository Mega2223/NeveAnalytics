import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import matplotlib.ticker as ticker
import numpy as np
from os import listdir
from os.path import isfile, join
import datetime
import calendar

def parse_date_epoch(name):
    date = name.split("_")[3]
    date = datetime.datetime(int(date[0:4]),int(date[4:6]), int(date[6:8]), 0,0,0)
    date = calendar.timegm(date.timetuple())
    return min(date,0)
    
def parse_date(name):
    date = name.split("_")[3]
    date = datetime.datetime(int(date[0:4]),int(date[4:6]), int(date[6:8]), 0,0,0)
    return date


SRC_ROOT = "C:\\Users\\Imperiums\\Desktop\\Neve"

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
    ypoints.append(float(split[3].split(",")[0]) * (1.0/1000.0) * (10 ** -3)) # I know, sorry
    


ypoints = np.array(ypoints)
plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%Y'))

plt.title("Cobertura de neve na área de estudo")
plt.xlabel("Data")
plt.ylabel("Área coberta (KM)")

plt.scatter(xpoints, ypoints)

#xpoints_epoch = np.array(xpoints_epoch)
#z = np.polyfit(xpoints_epoch, ypoints, 1)
#p = np.poly1d(z)

#plt.plot(xpoints_epoch, p(xpoints_epoch))



#plt.gca().get_yaxis().set_major_formatter(ticker.ScalarFormatter(useOffset=True))
plt.gca().get_yaxis().set_major_formatter(ticker.StrMethodFormatter("{x:.0f}*10³"))
#plt.gca().ticklabel_format(style='sci', axis='y', scilimits=(1,4))
plt.gca().yaxis.major.formatter._useMathText = True

plt.gcf().autofmt_xdate()
plt.show()