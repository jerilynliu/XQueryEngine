grammar XQuery;
import XPath;
xq : var                                                   #varXQ
   | StringConstant                                        #scXQ
   | ap                                                    #apXQ
   | '(' xq ')'                                            #braceXQ
   | xq ',' xq                                             #commaXQ
   | xq '/' rp                                             #singleSlashXQ
   | xq '//' rp                                            #doubleSlashXQ
   | startTag '{' xq '}' endTag                            #tagXQ
   | forClause letClause? whereClause? returnClause        #FLWR
   | letClause xq                                          #letXQ
   ;

forClause : 'for' var 'in' xq (',' var 'in' xq)* ;
letClause : 'let' var ':=' xq (',' var ':=' xq)* ;
whereClause : 'where' cond ;
returnClause : 'return' xq ;

cond : xq EQ xq                                                  #eqCond
     | xq IS xq                                                  #isCond
     | 'empty' '(' xq ')'                                           #emptyCond
     | 'some' var 'in' xq (',' var 'in' xq)* 'satisfies' cond    #satisfyCond
     | '(' cond ')'                                              #braceCond
     | cond 'and' cond                                           #andCond
     | cond 'or' cond                                            #orCond
     | 'not' cond                                                #notCond
     ;

startTag: '<' tagName '>';
endTag: '<' '/' tagName '>';
var: '$' ID;
StringConstant: '"'+[a-zA-Z0-9,.!?; '"-]+'"';
