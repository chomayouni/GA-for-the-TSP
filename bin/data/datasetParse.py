input_file = "raw/wg59_dist.txt"
output_file = "WG59.txt"

matrix = []

matrix.append(['59'])
with open(input_file, 'r') as file:
    for line in file:
        if not line.startswith("#"):
            row = line.strip().split()
            matrix.append(row)

output = "\n".join([",".join(row) + "," for row in matrix])

with open(output_file, 'w') as file:
    file.write(output)


#  CURSED DATSET
# input_file = "raw/usca312_dist.txt"
# output_file = "USCA312.txt"

# matrix = []
# data = []

# matrix.append(['312'])
# with open(input_file, 'r') as file:
#     for line in file:
#         if not line.startswith("#"):
#             row = line.strip().split()
#             for element in row:
#                 if len(data) < 312:
#                     data.append(element)
#                 else:
#                     matrix.append(data)
#                     data = [element]

#     # Append the last row if it has less than 312 elements
#     if data:
#         matrix.append(data)

# output = "\n".join([",".join(row) + "," for row in matrix])

# with open(output_file, 'w') as file:
#     file.write(output)