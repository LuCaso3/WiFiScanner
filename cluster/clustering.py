import random as rand
import math as math
from point import Point
#import pkg_resources
#pkg_resources.require("matplotlib")
import numpy as np
#from mpl_toolkits.mplot3d import Axes3D
#import matplotlib.pyplot as plt

class clustering:
    def __init__(self, geo_locs_, k_):
        self.geo_locations = geo_locs_
        self.k = k_
        self.clusters = []  #clusters of nodes
        self.return_mean = []
        self.means = []     #means of clusters
        self.debug = False  #debug flag
    #this method returns the next random node
    def next_random(self, index, points, clusters):
        #pick next node that has the maximum distance from other nodes
        dist = {}
        for point_1 in points:
            if self.debug:
                print ('point_1: %f %f %f' % (point_1.rss1, point_1.rss2, point_1.rss3)) 
            #compute this node distance from all other points in cluster
            for cluster in clusters.values():
                point_2 = cluster[0]
                if self.debug:
                    print ('point_2: %f %f %f' % (point_1.rss1, point_1.rss2, point_1.rss3))
                if point_1 not in dist:
                    dist[point_1] = math.sqrt(math.pow(point_1.rss1 - point_2.rss1,2.0) + math.pow(point_1.rss2 - point_2.rss2,2.0) + math.pow(point_1.rss3 - point_2.rss3,2.0))       
                else:
                    dist[point_1] += math.sqrt(math.pow(point_1.rss1 - point_2.rss1,2.0) + math.pow(point_1.rss2 - point_2.rss2,2.0) + math.pow(point_1.rss3 - point_2.rss3,2.0))
        if self.debug:
            for key, value in dist.items():
                print ("(%f, %f, %f) ==> %f" % (key.rss1,key.rss2,key.rss3,value))
        #now let's return the point that has the maximum distance from previous nodes
        count_ = 0
        max_ = 0
        for key, value in dist.items():
            if count_ == 0:
                max_ = value
                max_point = key
                count_ += 1
            else:
                if value > max_:
                    max_ = value
                    max_point = key
        return max_point
    #this method computes the initial means
    def initial_means(self, points):
        #pick the first node at random
        point_ = rand.choice(points)
        if self.debug:
            print ('point#0: %f %f %f' % (point_.rss1, point_.rss2, point_.rss3))
        clusters = dict()
        clusters.setdefault(0, []).append(point_)
        points.remove(point_)
        #now let's pick k-1 more random points
        for i in range(1, self.k):
            point_ = self.next_random(i, points, clusters)
            if self.debug:
                print ('point#%d: %f %f %f' % (i, point_.rss1, point_.rss2, point_.rss3))
            #clusters.append([point_])
            clusters.setdefault(i, []).append(point_)
            points.remove(point_)
        #compute mean of clusters
        #self.print_clusters(clusters)
        self.means = self.compute_mean(clusters)
        if self.debug: 
            print ("initial means:")
            self.print_means(self.means)
    def compute_mean(self, clusters):
        means = []
        for cluster in clusters.values():
            mean_point = Point(0.0, 0.0, 0.0)
            cnt = 0.0
            for point in cluster:
                #print "compute: point(%f,%f)" % (point.latit, point.longit)
                mean_point.rss1 += point.rss1
                mean_point.rss2 += point.rss2
                mean_point.rss3 += point.rss3
                cnt += 1.0
            mean_point.rss1 = mean_point.rss1/cnt
            mean_point.rss2 = mean_point.rss2/cnt
            mean_point.rss3 = mean_point.rss3/cnt
            means.append(mean_point)
        return means
    #this method assign nodes to the cluster with the smallest mean
    def assign_points(self, points):
        if self.debug:
            print ("assign points")
        clusters = dict()
        for point in points:
            dist = []
            if self.debug:
                print ("point(%f,%f,%f)" % (point.rss1, point.rss2, point.rss3))
            #find the best cluster for this node
            for mean in self.means:
                dist.append(math.sqrt(math.pow(point.rss1 - mean.rss1,2.0) + math.pow(point.rss2 - mean.rss2,2.0) + math.pow(point.rss3 - mean.rss3,2.0)))
            #let's find the smallest mean
            if self.debug:
                print (dist)
            cnt_ = 0
            index = 0
            min_ = dist[0]
            for d in dist:
                if d < min_:
                    min_ = d
                    index = cnt_
                cnt_ += 1
            if self.debug:
                print ("index: %d" % index)
            clusters.setdefault(index, []).append(point)
        return clusters
    def update_means(self, means, threshold):
        #check the current mean with the previous one to see if we should stop
        for i in range(len(self.means)):
            mean_1 = self.means[i]
            mean_2 = means[i]
            if self.debug:
                print ("mean_1(%f,%f,%f)" % (mean_1.rss1, mean_1.rss2, mean_1.rss3))
                print ("mean_2(%f,%f,%f)" % (mean_2.rss1, mean_2.rss2, mean_2.rss3))            
            if math.sqrt(math.pow(mean_1.rss1 - mean_2.rss1,2.0) + math.pow(mean_1.rss2 - mean_2.rss2,2.0) + math.pow(mean_1.rss3 - mean_2.rss3,2.0)) > threshold:
                return False
        return True
    #debug function: print cluster points
    def print_clusters(self, clusters):
        cluster_cnt = 1
        return_clust = []
        for cluster in clusters.values():
            print ("nodes in cluster #%d" % cluster_cnt)
            cluster_cnt += 1
            for point in cluster:
                print ("point(%f,%f,%f,%d)" % (point.rss1, point.rss2, point.rss3,(cluster_cnt-1)))
                return_clust.append((point.rss1, point.rss2, point.rss3,(cluster_cnt-1)))
        return return_clust

    #print means
    def print_means(self, means):
        return_means = []
        for point in means:
            print ("means : %f %f %f" % (point.rss1, point.rss2, point.rss3))
            return_means.append((point.rss1, point.rss2, point.rss3))
        return return_means
    #k_means algorithm
    def k_means(self, plot_flag):
        if len(self.geo_locations) < self.k:
            return -1   #error
        points_ = [point for point in self.geo_locations]
        #compute the initial means
        self.initial_means(points_)
        stop = False
        while not stop:
            #assignment step: assign each node to the cluster with the closest mean
            points_ = [point for point in self.geo_locations]
            clusters = self.assign_points(points_)
            if self.debug:
                self.print_clusters(clusters)
            means = self.compute_mean(clusters)
            if self.debug: 
                print ( "means:")
                print (self.print_means(means))
                print ("update mean:")
            stop = self.update_means(means, 0.01)
            if not stop:
                self.means = []
                self.means = means
        self.clusters = clusters
'''        #plot cluster for evluation
        if plot_flag:
            fig = plt.figure()
            ax = fig.add_subplot(111)
            markers = ['o', 'd', 'x', 'h', 'H', 7, 4, 5, 6, '8', 'p', ',', '+', '.', 's', '*', 3, 0, 1, 2]
            colors = ['r', 'k', 'b', [0,0,0], [0,0,1], [0,1,0], [0,1,1], [1,0,0], [1,0,1], [1,1,0], [1,1,1]]
            cnt = 0
            for cluster in clusters.values():
                rss1_s = []
                rss2_s = []
		rss3_s = []
                for point in cluster:
                    rss1_s.append(point.rss1)
                    rss2_s.append(point.rss2)
                    rss3_s.append(point.rss3)
                ax.scatter(rss3_s, rss2_s, rss1_s, s=20, c=colors[cnt], marker=markers[cnt])
                cnt += 1
            plt.show()
        return 0
'''
