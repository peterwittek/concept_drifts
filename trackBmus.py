# -*- coding: utf-8 -*-
"""
Created on Thu Feb  5 16:50:02 2015

@author: wittek
"""
from math import sqrt

threshold1 = 5
threshold2 = 11

def get_keywords(keyword_filename, bmu_filename):
    bmus = []
    bmu_file = open(bmu_filename,'r')
    n_rows, n_columns = bmu_file.readline()[1:].split(' ')
    bmu_file.readline()
    for line in bmu_file:
        line = line.strip()
        elements = line.split(' ')
        bmus.append((int(elements[1]), int(elements[2])))
    bmu_file.close
    keywords = {}
    keyword_file = open(keyword_filename,'r')
    keyword_file.readline()
    k = 0
    for line in keyword_file:
        line = line.strip()
        elements = line.split('\t')
        keywords[elements[1]] = bmus[k]
        k += 1
    keyword_file.close()
    return keywords, int(n_rows), int(n_columns)

def toroid_distance(coords1, coords2, n_rows, n_columns):
    x1 = min(coords1[0], coords2[0])
    y1 = min(coords1[1], coords2[1])
    x2 = max(coords1[0], coords2[0])
    y2 = max(coords1[1], coords2[1])
    xdist = min(x2-x1, x1+n_rows-x2)
    ydist = min(y2-y1, y1+n_columns-y2)
    return sqrt(xdist**2+ydist**2)


keywords1, n_rows, n_columns = get_keywords('data/termvectorsperiod1.names',
                                            'data/termvectorsperiod1.bm')
keywords2, _, _ = get_keywords('data/termvectorsperiod2.names',
                               'data/termvectorsperiod2.bm')
keywords3, _, _ = get_keywords('data/termvectorsperiod3.names',
                               'data/termvectorsperiod3.bm')

candidates = []
for keyword in keywords1:
    if keywords2.has_key(keyword) and keywords3.has_key(keyword):
        coords1 = keywords1[keyword]
        coords2 = keywords2[keyword]
        coords3 = keywords3[keyword]
        if toroid_distance(coords1, coords2, n_rows, n_columns) < threshold1:
            candidates.append(keyword)

for i, keyword1 in enumerate(candidates):
    for keyword2 in candidates:
        if keyword1 is not keyword2:
            coords1 = keywords1[keyword1]
            coords2 = keywords1[keyword2]
            if toroid_distance(coords1, coords2, n_rows, n_columns) < threshold2:
                print keyword1, keyword2, keywords1[keyword1], keywords1[keyword2], keywords2[keyword1], keywords3[keyword1]
