
1. repartition is a friendly name that calls coalesce directly but passes shuffle to true explicitly.
defrepartition(numPartitions:Int)(implicitord:Ordering[T]=null):RDD[T]=withScope {coalesce(numPartitions,shuffle =true)}
2. Full logging -> TestArgument("-oF") remove existing test arguments for it to take effect. 
