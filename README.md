Demonstrating concept drifts in Amazon book reviews
===================================================

The aim of this project is to analyse semantic consistency using emergent self-organizing maps and study how consistency changes over time. We use a corpus spanning eighteen years and consisting of 12.5 million entries. 

First, obtain the [data set](http://snap.stanford.edu/data/amazon/amazon_readme.txt), and clone this repository. The outline of the processing steps is as follows:

1. Index the subsequent time periods by Lucene.
2. Build reflexive random indices.
3. Train emergent self-organizing maps.

The rest of this readme details these steps. The dependencies for the Java tools are lucene-core-4.10.3.jar, lucene-analyzers-common-4.10.3.jar, and edu.mit.jwi_2.3.3.jar, and semanticvectors-5.6.jar. It is assumed that they are in the ``CLASSPATH``.

Indexing
--------
The class concepDrifts.LuceneIndexer with setting the parameters in the ``main()`` function. The ``runIndexer`` static method takes an integer cut-off value. This defines the end of the time period in Unix time until which the documents should be indexed (not inclusive). To obtain a roughly even cut in three periods, we used 1043884800, 1217721600 and ``Integer.MAX_VALUE``.

Indexing takes a few hours. At the end of it, we should have three folders: ``data/index{1,2,3}``.

Generating the reflexive random indices
---------------------------------------
We build the reflexive random indices in the data folder. This is a memory-bound step.

    cd data
    java -Xmx22000m pitt.search.semanticvectors.BuildIndex -trainingcycles 2 -luceneindexpath index1
    mv termvectors2.bin termvectorsperiod1.bin
    mv docvectors2.bin docvectorsperiod1.bin

    java -Xmx22000m pitt.search.semanticvectors.BuildIndex -trainingcycles 2 -initialtermvectors termvectorsperiod1 -docindexing DocIndexingStrategy.INCREMENTAL -luceneindexpath index2
    mv termvectors2.bin termvectorsperiod2.bin
    mv docvectors2.bin docvectorsperiod2.bin

    java -Xmx22000m pitt.search.semanticvectors.BuildIndex -trainingcycles 2 -initialtermvectors termvectorsperiod2 -luceneindexpath index3
    mv termvectors2.bin termvectorsperiod3.bin
    mv docvectors2.bin docvectorsperiod3.bin

We need to convert the term vectors to text format:

    java pitt.search.semanticvectors.VectorStoreTranslater -lucenetotext termvectorsperiod1.bin termvectorsperiod1.txt
    java pitt.search.semanticvectors.VectorStoreTranslater -lucenetotext termvectorsperiod2.bin termvectorsperiod2.txt
    java pitt.search.semanticvectors.VectorStoreTranslater -lucenetotext termvectorsperiod3.bin termvectorsperiod3.txt
    
Then we transform the random index to suitable input files for Somoclu and ESOM Tools:

    cd ..
    java conceptDrifts.SvDense2Sparse data/termvectorsperiod1.txt data/termvectorsperiod1.svm data/termvectorsperiod1.names
    java conceptDrifts.SvDense2Sparse data/termvectorsperiod2.txt data/termvectorsperiod2.svm data/termvectorsperiod2.names
    java conceptDrifts.SvDense2Sparse data/termvectorsperiod3.txt data/termvectorsperiod3.svm data/termvectorsperiod3.names

Training the emergent self-organizing maps
------------------------------------------
For [Somoclu](https://peterwittek.github.io/somoclu/) versions prior to 1.4.1, change the function ``getWeight`` in mapDistanceFunctions.cpp of Somoclu to remove the compact support. This will yield smoother maps:

```cpp
float getWeight(float distance, float radius, float scaling)
{
//    if (distance <= radius)
//    {
        return scaling * gaussianNeighborhood(distance, radius, 2);
//    }
//    else
//    {
//        return 0.0;
//    }
}
```

Release 1.4.1 defaults to this behaviour. Compile Somoclu and train the emergent self-organizing maps:

```bash
somoclu -k 2 -m toroid -s 1 -x 253 -y 143 data/termvectorsperiod1.svm data/termvectorsperiod1
somoclu -k 2 -m toroid -s 1 -x 253 -y 143 data/termvectorsperiod2.svm data/termvectorsperiod2
somoclu -k 2 -m toroid -s 1 -x 253 -y 143 data/termvectorsperiod3.svm data/termvectorsperiod3
```

Acknowledgment
===
This work was supported by the European Commission Seventh Framework Programme under Grant Agreement Number FP7-601138 PERICLES.
