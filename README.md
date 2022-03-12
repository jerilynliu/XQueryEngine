# XQueryEngine
Building an XQuery Engine from scratch, using ANTLR4 and JAVA. 

## MileStone
### MileStone 1: XpathEngine
MileStone 1 has been accomplished.
    usage: java -jar CSE-232B-M1.jar one_xpath_query.txt
    
### MileStone 2: XQueryEngine
MileStone 2 has been accomplished.
    usage: java -jar CSE-232B-M2.jar one_xquery_query.txt
    
### MileStone 3: XQueryEngine (Optimizied with join operation)
MileStone 3 has been accomplished.
    usage: usage java -jar [eva | rew] [filename]
    
    eg: 
    step1 (rewite original query):
    java -jar CSE-232B-M3.jar rew ori.txt
    this instruction will generate a file "rewrite-ori.txt"
    
    step2 (evaluate the rewritten query):
    java -jar CSE-232B-M3.jar eva rewrite-ori.txt
    
