from collections import defaultdict

import numpy as np
import scipy.stats as st
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import json
import glob
import sys

# Plots histograms and means (wiht a 95% CI boundary) of data generated from JMH runs
# Uses wildcarding to join data together, e.g.:
#   python ./plot-stats.py canonical-results/strings-*.json

filepath = sys.argv[1]


subplot_data = defaultdict(dict)
for file_name in glob.glob(filepath):
    with open(file_name) as file:
        raw_data = json.load(file)
        named_data = ( (d['benchmark'].split('.')[-2:],d['primaryMetric']['rawData'][0],d['primaryMetric']['scoreUnit']) for d in raw_data )
        for name,data,unit in named_data:
            if name[0] not in subplot_data[name[1]]:
                subplot_data[name[1]][name[0]]=(data,unit)
            else:
                subplot_data[name[1]][name[0]]=(subplot_data[name[1]][name[0]][0]+data,unit)

num_subplots = len(subplot_data)
columns = int(np.sqrt(num_subplots))
rows = num_subplots//columns
rows += num_subplots - columns*rows
for i,(subplot_name,subplots) in enumerate(subplot_data.iteritems()):
    ax = plt.subplot(rows,columns,i+1)
    areas = {}
    for name,(data,unit) in subplots.items():
        plt.ylabel(subplot_name)
        plt.xlabel(unit)
        plt.hist(data,bins='auto',histtype='step', color = 'red' if 'otlin' in name else 'blue', label=name, linestyle = '-' if 'otlin' in name else '--')
        plt.axvline(np.mean(data), color='red' if 'otlin' in name else 'blue', linestyle='-' if 'otlin' in name else '--', linewidth=2)
        areas[name] = st.t.interval(0.95, len(data)-1, loc=np.mean(data), scale=st.sem(data))
    plt.legend()
    _, ymax = plt.ylim()
    area_height=ymax/4
    for name,(lower,upper) in areas.items():
        ax.add_patch( patches.Rectangle( (lower,area_height+(area_height if 'otlin' in name else 0)), upper-lower, area_height, color = 'red' if 'otlin' in name else 'blue', alpha=0.3 ) )

plt.show()