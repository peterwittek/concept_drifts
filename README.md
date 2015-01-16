Demonstrating concept drifts in Amazon book reviews
===================================================

Run concepDrifts.LuceneIndexer with setting the parameters in the ``main()`` function. Dependencies are lucene-core-4.10.3.jar, lucene-analyzers-common-4.10.3.jar, and edu.mit.jwi_2.3.3.jar.

Build the random index in the data folder:

```bash
export CLASSPATH=$HOME/wrk/java/semanticvectors-5.6.jar
java pitt.search.semanticvectors.BuildIndex -indexfileformat text -luceneindexpath sample_index
```
    
Transform the random index to suitable input files for Somoclu and ESOM Tools:

```bash
java conceptDrifts.SvDense2Sparse data/termvectors.txt data/termvectors.svm data/termvectors.names
```

Change the function ``getWeight`` in mapDistanceFunctions.cpp of Somoclu to remove the compact support. This wield yield smoother maps:

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

Compile Somoclu and train the emergent self-organizing map:

```bash
somoclu -k 2 -m toroid -s 1 -x 253 -y 143 data/termvectors.svm data/termvectors
```
