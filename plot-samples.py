
import sys

import numpy as np
import matplotlib.pyplot as plt

input_filename = sys.argv[1]
output_filename = sys.argv[2]

data = np.genfromtxt(input_filename, delimiter=',', names=True, dtype=None)

plt.rcdefaults()
plt.figure(figsize=(16, 9))
plots = [plt.plot(data[colname]) for colname in data.dtype.names]
plt.ylim(0, plt.ylim()[1])

plt.title("Benchmark Throughput")
plt.ylabel("Throughput")
plt.xlabel("Iteration")
plt.legend(data.dtype.names, loc='best')
plt.savefig(output_filename)

