# XQueryEngine
This project is the construction of an XQuery processor. We consider a subset/modification of XML’s data model and of  XQuery, as described in this note  Download this [note](https://github.com/jerilynliu/XQueryEngine/blob/main/study-notes/xpathxquery-semantics.pdf). 

The processor receives an XQuery, parses it into an abstract tree representation, optimizes it and finally executes the optimized plan.

**Milestones 1 and 2 (Naïve Evaluation)**: A straightforward query execution engine receives the simplified XQuery and an input XML file and evaluates the query using a recursive evaluation routine which, given an XQuery expression (path, concatenation, element creation, etc) and a list of input nodes, produces a list of output nodes. For the XQuery parser, we generated it using the ANTLR 4 parser generator. Provided with a grammar, ANTLR generates a compiler which automatically constructs abstract syntax trees of  its input expressions.

Milestone 1 delivers a naive evaluator for XPath, while Milestone 2 extends it to XQuery.

**Milestone 3** (Efficient Evaluation) : Implement a join operator as defined in this [note](https://github.com/jerilynliu/XQueryEngine/blob/main/study-notes/join-optimization.pdf). Implement an algorithm which detects the fact that the FOR and WHERE clause computation can be implemented using the join operator. We assume that the input XQueries to be optimized are in the simplified syntax given in the note. The implementation hints can be seen [here](https://github.com/jerilynliu/XQueryEngine/blob/main/study-notes/Milestone-III-Hints.pdf).

> To access XML files we use the standard DOM interface. There are a number of XML DOM parser implementations.
The Java distribution includes one (see documentation [here](https://docs.oracle.com/javase/tutorial/jaxp/dom/index.html)). As an alternative, the Xerces-J project from Apache is quite mature and stable.

> The W3C specification of DOM is [here](https://www.w3.org/TR/DOM-Level-2-Core/).

The main architecture of this project is as the picture shows and is detailed [here](https://docs.google.com/presentation/d/16UMBRK0bavNXEnyqd-b-7BQQxZWgtfF0TYmdxALM2Vw/edit#slide=id.gb62a2893a6_0_52).

![alt p1](https://user-images.githubusercontent.com/50944218/158038028-b8a4aab7-3781-42c4-8b78-37fa5ae54702.png)


## Usage
### MileStone 1: XpathEngine
MileStone 1 has been accomplished.

usage: 

    java -jar CSE-232B-M1.jar one_xpath_query.txt
    
### MileStone 2: XQueryEngine
MileStone 2 has been accomplished.

usage:

    java -jar CSE-232B-M2.jar one_xquery_query.txt
    
### MileStone 3: XQueryEngine (Optimizied with join operation)
MileStone 3 has been accomplished.

usage: 

    usage java -jar [jarfile] [eva | rew] [filename]
    
eg:
    
step1 (rewrite original query):

    java -jar CSE-232B-M3.jar rew ori.txt
    
this instruction will generate a file "rewrite-ori.txt"
    
step2 (evaluate the rewritten query):

    java -jar CSE-232B-M3.jar eva rewrite-ori.txt
    
