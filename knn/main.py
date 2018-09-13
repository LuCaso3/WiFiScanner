from csv import reader
from sys import exit
from math import sqrt
from operator import itemgetter
import pymysql
import time

conn = pymysql.connect(host='localhost', user='root', password='1234', db='testdb', charset='utf8')

def convert_to_float(data_set, mode):
    new_set = []
    try:
        if mode == 'training':
            for data in data_set:
                new_set.append([float(x) for x in data[:len(data)-1]] + [data[len(data)-1]])

        elif mode == 'test':
            for data in data_set:
                new_set.append([float(x) for x in data])

        else:
            print('Invalid mode, program will exit.')
            exit()

        return new_set

    except ValueError as v:
        print(v)
        print('Invalid data set format, program will exit.')
        exit()


def get_classes(training_set):
    return list(set([c[-1] for c in training_set]))


def find_neighbors(distances, k):
    return distances[0:k]


def find_response(neighbors, classes):
    votes = [0] * len(classes)

    for instance in neighbors:
        for ctr, c in enumerate(classes):
            if instance[-2] == c:
                votes[ctr] += 1

    return max(enumerate(votes), key=itemgetter(1))


def knn(training_set, test_set, k):
    locate = []
    distances = []
    dist = 0
    vote_rate = 0
    limit = len(training_set[0]) - 1

    # generate response classes from training data
    classes = get_classes(training_set)

    try:
        for test_instance in test_set:
            for row in training_set:
                for x, y in zip(row[:limit], test_instance):
                    dist += (x-y) * (x-y)
                distances.append(row + [sqrt(dist)])
                dist = 0

            distances.sort(key=itemgetter(len(distances[0])-1))

            # find k nearest neighbors
            neighbors = find_neighbors(distances, k)

            # get the class with maximum votes
            index, value = find_response(neighbors, classes)

            # Display prediction
            #print('The predicted class for sample ' + str(test_instance) + ' is : ' + classes[index])
            #print('Number of votes : ' + str(value) + ' out of ' + str(k))
            
            vote_rate = value/k
			
			#return locate set
            locate.append(str(test_instance))
            locate.append(str(classes[index]))
            lcoate.append(str(vote_rate))
            cursor = conn.cursor()
 
            cursor.executemany("""INSERT INTO test4(values1, values2, values3) VALUES (%s, %s, %s)""", [locate])
            con.commit()
		    # empty the distance list
            distances.clear()
			#empty the locate list
            locate.pop()
            locate.pop()
            locate.pop()
			#empty the input locate csv file
            conn = pymysql.connect(host='localhost', user='root', password='', db='testdb', charset='utf8')
            curs = conn.cursor()
            sql = "truncate test3"
            curs.execute(sql)

    except Exception as e:
        print(e)


def main():
    try:
        # get value of k
        k = int(5)
        
		# get training set in rows
        curs = conn.cursor()
        sql = "select * from test2"
        curs.execute(sql)
        rows = curs.fetchall()
		#load the training data set
        training_set = convert_to_float(rows, 'training')


        while 1:
		    #get test set in rows2
            curs2 = conn.cursor()
            sql2 = "select * from test3"
            curs2.execute(sql2)
            rows2 = curs2.fetchall()

            # load the test data set
            test_set = convert_to_float(rows2, 'test')
		
            if not training_set:
                print('Empty training set')

            elif not test_set:
                print('Empty test set')
				
            elif k > len(training_set):
                print('Expected number of neighbors is higher than number of training data instances')

            else:
                knn(training_set, test_set, k)
                time.sleep(5)
		#clear the output KNN locate csv file
        conn = pymysql.connect(host='localhost', user='root', password='', db='testdb', charset='utf8')
        curs = conn.cursor()
        sql = "truncate test4"
        curs.execute(sql)
        conn.close()

    except ValueError as v:
        print(v)

    except FileNotFoundError:
        print('File not found')


if __name__ == '__main__':
    main()