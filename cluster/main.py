import random as rand
import pymysql
from clustering import clustering
from point import Point
import csv

conn = pymysql.connect(host='localhost', user='root', password='', db='testdb', charset='utf8')

curs = conn.cursor()

sql = "select * from test"
curs.execute(sql)

rows = curs.fetchall()
geo_locs = []

for line in rows:
    loc_ = Point(float(line[0]), float(line[1]), float(line[2]))  #tuples for location
    geo_locs.append(loc_)
#print len(geo_locs)
#for p in geo_locs:
#    print "%f %f %f" % (p.RSS1, p.RSS2, p.RSS3)
#let's run k_means clustering. the second parameter is the no of clusters
cluster = clustering(geo_locs, 8 )
flag = cluster.k_means(False)
if flag == -1:
    print("Error in arguments!")
else:
    #the clustering results is a list of lists where each list represents one cluster
    print("clustering results:")

    clust = cluster.print_clusters(cluster.clusters)

conn = pymysql.connect(host='localhost', user='root', password='', db='testdb', charset='utf8')

cursor = conn.cursor()

cursor.executemany("""INSERT INTO test2(values1, values2, values3, values4) VALUES (%s, %s, %s, %s)""", clust)

conn.commit()

conn.close()
