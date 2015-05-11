To use this tool, please follow instructions below:

first create data file by type below command in shell, where DATA_PATH is the folder path contains all data files
./generate.sh DATA_PATH

second run command below to setup the Query initialization
./query.sh DATA_PATH

then input below command to do the query
QUERY 192.168.1.10 1 2014-10-31 00:00 2014-10-31 00:05

input below command to exit this tool
EXIT

__________________________________________________
Requirement:
you need JVM and JRE7 in the system

there are 1000 servers assigned on IP: 192.168.x.x
target date is: 2014-10-31