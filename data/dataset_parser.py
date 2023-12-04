import os
import gzip
import shutil
import tempfile
import tsplib95
import numpy as np
import networkx as nx
from tqdm import tqdm

input_paths = ['TSP_RAW_GZ/Symmetric', 'TSP_RAW_GZ/Asymmetric']
output_paths = ['TSP_DATASET/Symmetric', 'TSP_DATASET/Asymmetric']

exempt_list = ['brd14051.tsp', 'd15112.tsp', 'd1291.tsp', 'd1655.tsp', 
               'd18512.tsp', 'd2103.tsp', 'fl1400.tsp', 'fl1577.tsp', 
               'fl3795.tsp', 'fnl4461.tsp', 'nrw1379.tsp', 'pcb3038.tsp', 
               'pla33810.tsp', 'pla7397.tsp', 'pla85900.tsp', 'pr2392.tsp', 
               'rl11849.tsp', 'rl1304.tsp', 'rl1323.tsp', 'rl1889.tsp', 'rl5915.tsp', 
               'rl5934.tsp', 'u1432.tsp', 'u1817.tsp', 'u2152.tsp', 'u2319.tsp', 
               'usa13509.tsp', 'vm1748.tsp']
for input_path in input_paths:
    output_path = output_paths[0]
    file_end = ".tsp"
    if (input_path == 'TSP_RAW_GZ/Asymmetric'):
        output_path = output_paths[1]
        file_end = ".atsp"
    print("Processing files in: ", input_path)
    for filename in tqdm(os.listdir(input_path), desc="Processing files"):
        if filename.endswith(file_end + ".gz"):
            # Unzip the file
            with gzip.open(os.path.join(input_path, filename), 'rb') as f_in:
                # Create a temporary file
                with tempfile.NamedTemporaryFile(delete=False) as tmp_file:
                    shutil.copyfileobj(f_in, tmp_file)
                # Remove the '.gz' extension from the filename
                filename_without_extension = os.path.splitext(filename)[0]
                if (filename_without_extension not in exempt_list):
                    # print("Converting file: ", filename_without_extension)  
                    # Load the problem from the temporary file
                    problem = tsplib95.load(tmp_file.name)
                    if (problem.dimension > 1200):
                        print("File too big: ", filename_without_extension)
                        exempt_list.append(filename_without_extension)
                        continue
                    # if problem.edge_weight_type == 'EXPLICIT':
                    distances = nx.to_numpy_matrix(problem.get_graph())
                    distances = np.array(distances, dtype=int)

                    # Capilize and Replace the '.tsp' extension with '.txt'
                    filename_txt = filename_without_extension.upper()
                    filename_txt = filename_without_extension.replace(file_end, '.txt')

                    # Convert the numpy array to a list of strings, adding a comma at the end of each row
                    distances_str_list = [','.join(map(str, row.tolist())) + ',' for row in distances]

                    # Add the dimension at the top
                    distances_str_list.insert(0, str(problem.dimension) + ',')

                    # Join the list into a single string with newlines
                    distances_str = '\n'.join(distances_str_list)

                    # Write the string to the file
                    with open(os.path.join(output_path, filename_txt), 'w') as f:
                        f.write(distances_str)

# print("Exempt list: ", exempt_list)