
import sys

import numpy as np
import matplotlib.pyplot as plt

input_filename = sys.argv[1]
output_filename = sys.argv[2]

common_prefix_length = len('"com.oneeyedmen.kostings.')
data = np.genfromtxt(input_filename, delimiter=',', names=True, dtype=None)

x = data['Score']
y = np.arange(len(data['Benchmark']))
err = data['Score_Error_999']

labels = []
for name in data['Benchmark']:
  labels.append(name[common_prefix_length:-1])

unit = data['Unit'][0][1:-1]

plt.rcdefaults()
plt.figure(figsize=(16, 9))
plt.gca().invert_yaxis()
plt.barh(y, x, xerr=err, color='blue', ecolor='red', alpha=0.4, align='center')
plt.yticks(y, labels)
plt.xlabel("Performance / %s" % unit)
plt.title("Benchmark")
plt.tight_layout()
plt.savefig(output_filename)