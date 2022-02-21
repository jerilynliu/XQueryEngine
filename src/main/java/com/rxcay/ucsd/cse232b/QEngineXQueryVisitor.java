package com.rxcay.ucsd.cse232b;

import com.rxcay.ucsd.cse232b.antlr4.XQueryBaseVisitor;
import com.rxcay.ucsd.cse232b.antlr4.XQueryParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.rxcay.ucsd.cse232b.XPathEvaluator.evaluateXPathRPByPNodesWithRtException;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 2/20/22 12:28 PM
 * @description
 */
public class QEngineXQueryVisitor extends XQueryBaseVisitor<List<Node>>{
    private final Document tmpDoc;
    private Map<String, List<Node>> contextMap = new HashMap<>();

    void setContextMap(Map<String,List<Node>> c) {
        this.contextMap = new HashMap<>(c);
    }

    // TODO:need another getDescendents method here to parse #doubleSlashXQ. Get proper pNodes then call XPathEvaluator.

    // helper function to get all strict descendents of a node
    public LinkedList<Node> getStrictDescendents(Node node) {

        LinkedList<Node> res = new LinkedList<>(); // result

        NodeList childrenNodes = node.getChildNodes();
        for (int i = 0; i < childrenNodes.getLength(); i++) {
            Node curtChild = childrenNodes.item(i);
            res.add(curtChild); // add a child
            res.addAll(getStrictDescendents(curtChild));
        }
        return res;
    }

    // helper function to obtain the self-and-descendents set of all the input nodes
    public LinkedList<Node> getSelfAndDescendents(List<Node> nodes){

        LinkedList<Node> res = new LinkedList<>(nodes);

        // store all the strict descendents of nodes into tmp
        LinkedList<Node> tmp = new LinkedList<>();
        for (Node node : nodes) {
            tmp.addAll(getStrictDescendents(node));
        }

        // add all descendents of nodes to res while removing duplicates
        for (Node node : tmp) {
            if (!res.contains(node)) {
                res.add(node);
            }
        }

        return res;
    }



    private List<String> varsInForClause = new ArrayList<>();

    private Node makeElem(String tag, List<Node> children) {
        Node r = tmpDoc.createElement(tag);
        for (Node n : children) {
            if (n != null) {
                Node newNode = this.tmpDoc.importNode(n,true);
                r.appendChild(newNode);
            }
        }
        return r;
    }

    public QEngineXQueryVisitor(Document tmpDoc) {
        this.tmpDoc = tmpDoc;
    }

    @Override
    public List<Node> visitFLWR(XQueryParser.FLWRContext ctx) {
        List<String> oldVarsInForClause = this.varsInForClause;
        Map<String,List<Node>> currentContext;
        visit(ctx.forClause()); //after this, varsInForClause is set. return is null
        List<String> varsInCurrentFLWR = this.varsInForClause;
        int varCnt = varsInCurrentFLWR.size();
        currentContext = this.contextMap;
        XQueryParser.LetClauseContext letClauseCtx = ctx.letClause();
        if (letClauseCtx != null) {
            visit(ctx.letClause());
            currentContext = this.contextMap;
        }
        // start permutation for all var
        int per = 1;
        List<List<Node>> varNodes = new ArrayList<>();
        for (int i = 0; i < varCnt; i++) {
            String name = varsInCurrentFLWR.get(i);
            List<Node> nodes = currentContext.get(name);
            varNodes.add(nodes);
            per *= nodes.size();
        }
        List<Node> res = new LinkedList<>();
        // once any for var is empty, then the result will be empty.
        if(per > 0) {
            for (int i = 0; i < per; i++) {
                int[] indices = new int[varCnt];
                int fac = 1;
                for (int j = varCnt - 1;j >= 0;j --) {
                    indices[j] = i / fac % varNodes.get(j).size();
                    fac *= varNodes.get(j).size();
                }
                Map<String,List<Node>> permMap = new HashMap<>(currentContext);
                for (int p = 0;p < varCnt;p++){
                    List<Node> oneNodeList = new LinkedList<>();
                    oneNodeList.add(varNodes.get(p).get(indices[p]));
                    permMap.put(varsInCurrentFLWR.get(p), oneNodeList);
                }
                this.setContextMap(permMap);
                // if no where clause, then where clause returns always true.
                XQueryParser.WhereClauseContext whereCtx = ctx.whereClause();
                List<Node> nullIfFalseList = whereCtx != null ? visit(ctx.whereClause()) : new LinkedList<>();
                if (nullIfFalseList != null) {
                    // where return true
                    this.setContextMap(permMap);
                    List<Node> onePermRes = visit(ctx.returnClause());
                    res.addAll(onePermRes);
                }
            }
        }
        this.varsInForClause = oldVarsInForClause;
        return res;
    }

    @Override
    public List<Node> visitSingleSlashXQ(XQueryParser.SingleSlashXQContext ctx) {

        String rpText = ctx.rp().getText();
        InputStream i = new ByteArrayInputStream(rpText.getBytes());

        return evaluateXPathRPByPNodesWithRtException(i, visit(ctx.xq()));

    }

    @Override
    public List<Node> visitTagXQ(XQueryParser.TagXQContext ctx) {
        String tag = ctx.startTag().tagName().ID().getText();
        LinkedList<Node> res = new LinkedList<>();

        setContextMap(contextMap);
        List<Node> nodeList = visit(ctx.xq());

        Node node = makeElem(tag, nodeList);
        res.add(node);

        return res;
    }

    @Override
    public List<Node> visitApXQ(XQueryParser.ApXQContext ctx) {
        setContextMap(contextMap);
        String ap = ctx.getText();
        InputStream is = new ByteArrayInputStream(ap.getBytes());
        return XPathEvaluator.evaluateXPathAPWithRtException(is);
    }

    @Override
    public List<Node> visitLetXQ(XQueryParser.LetXQContext ctx) {
        // prepare vars
        visit(ctx.letClause());
        Map<String,List<Node>> currentContext = this.contextMap;
        setContextMap(currentContext);
        List<Node> res = visit(ctx.xq());
        return res;

    }

    @Override
    public List<Node> visitCommaXQ(XQueryParser.CommaXQContext ctx) {
        Map<String, List<Node>> currentCtxMap = contextMap;
        setContextMap(currentCtxMap);
        List<Node> res1 = visit(ctx.xq(0));
        setContextMap(currentCtxMap);
        List<Node> res2 = visit(ctx.xq(1));

        res1.addAll(res2);
        return res1;
    }

    @Override
    public List<Node> visitVarXQ(XQueryParser.VarXQContext ctx) {
        setContextMap(contextMap);
        // TODO: bugfix, get terminate node ID's text as key [fixed]
        return this.contextMap.get(ctx.var().ID().getText());
    }

    @Override
    public List<Node> visitScXQ(XQueryParser.ScXQContext ctx) {
        String str = ctx.StringConstant ().getText();
        // TODO: test whether this is necessary
        str = str.substring(1, str.length() - 1); // remove the left parenthesis and the right parenthesis

        LinkedList<Node> res = new LinkedList<>();
        res.add(this.tmpDoc.createTextNode(str));   // performs makeText()

        return res;
    }

    @Override
    public List<Node> visitBraceXQ(XQueryParser.BraceXQContext ctx) {
        setContextMap(contextMap);
        return visit(ctx.xq());
    }

    @Override
    public List<Node> visitDoubleSlashXQ(XQueryParser.DoubleSlashXQContext ctx) {

        String rpText = ctx.rp().getText();
        InputStream i = new ByteArrayInputStream(rpText.getBytes());

        List<Node> xqRes = getSelfAndDescendents(visit(ctx.xq()));  // get the proper context nodes for rp parsing
        return evaluateXPathRPByPNodesWithRtException(i, xqRes);

    }

    @Override
    public List<Node> visitForClause(XQueryParser.ForClauseContext ctx) {
        // for should update context in step and return the updated context and set varsInForClause
        Map<String, List<Node>> currentContext = this.contextMap;
        List<XQueryParser.VarContext> vars = ctx.var();
        int varCnt = vars.size();
        List<String> varsInFor = new ArrayList<>(varCnt);
        for (XQueryParser.VarContext varCtx : vars) {
            varsInFor.add(varCtx.ID().getText());
        }
        // set for FLWR
        this.varsInForClause = varsInFor;
        List<XQueryParser.XqContext> xqInForClause = ctx.xq();
        for (int i = 0; i < varCnt; i++) {
            setContextMap(currentContext);
            String varName = varsInFor.get(i);
            List<Node> xqResult = visit(xqInForClause.get(i));
            currentContext.put(varName, xqResult);
        }
        setContextMap(currentContext);
        return null;
    }

    @Override
    public List<Node> visitLetClause(XQueryParser.LetClauseContext ctx) {
        Map<String, List<Node>> currentContext = this.contextMap;
        List<XQueryParser.VarContext> vars = ctx.var();
        int varCnt = vars.size();
        List<XQueryParser.XqContext> xqs = ctx.xq();
        for (int i = 0; i < varCnt; i++) {
            String varName = vars.get(i).getText();
            setContextMap(currentContext);
            List<Node> xqResult = visit(xqs.get(i));
            currentContext.put(varName, xqResult);
        }
        setContextMap(currentContext);
        return null;
    }

    @Override
    public List<Node> visitWhereClause(XQueryParser.WhereClauseContext ctx) {
        // with given context, return the boolean result of cond
        return visit(ctx.cond());
    }

    @Override
    public List<Node> visitReturnClause(XQueryParser.ReturnClauseContext ctx) {
        // with given context, evaluate xq. should use the same context with where clause.
        return visit(ctx.xq());
    }

    @Override
    public List<Node> visitBraceCond(XQueryParser.BraceCondContext ctx) {
        return visit(ctx.cond());
    }

    @Override
    public List<Node> visitOrCond(XQueryParser.OrCondContext ctx) {
        Map<String,List<Node>> currentContext = this.contextMap;
        setContextMap(currentContext);
        boolean bL = visit(ctx.cond(0)) != null;
        setContextMap(currentContext);
        boolean bR = visit(ctx.cond(1)) != null;
        return bL || bR ? new LinkedList<>() : null;
    }

    @Override
    public List<Node> visitSatisfyCond(XQueryParser.SatisfyCondContext ctx) {
        Map<String, List<Node>> currentContext = this.contextMap;
        int varCnt = ctx.var().size();
        List<String> varsInSome = new ArrayList<>(varCnt);
        for(XQueryParser.VarContext vCtx : ctx.var()){
            varsInSome.add(vCtx.ID().getText());
        }
        List<XQueryParser.XqContext> xqInSome = ctx.xq();
        List<List<Node>> varsNodes = new ArrayList<>();
        for (int i = 0; i < varCnt; i++) {
            setContextMap(currentContext);
            String varName = varsInSome.get(i);
            List<Node> xqResult = visit(xqInSome.get(i));
            currentContext.put(varName, xqResult);
            varsNodes.add(xqResult);
        }
        setContextMap(currentContext);

        int per = 1;
        for (int i = 0; i < varCnt; i++) {
            per *= varsNodes.get(i).size();
        }
        if ( per <= 0) {
            // one var is empty, return false for sure.
            return null; // null -false
        }
        for (int i = 0; i < per; i++) {
            int[] indices = new int[varCnt];
            int fac = 1;
            for(int j = varCnt - 1;j >= 0;j --) {
                indices[j] = i / fac % varsNodes.get(j).size();
                fac*= varsNodes.get(j).size();
            }
            Map<String, List<Node>> permMap = new HashMap<>(currentContext);
            for(int p = 0;p < varCnt;p++) {
                List<Node> oneNodeList = new LinkedList<>();
                oneNodeList.add(varsNodes.get(p).get(indices[p]));
                permMap.put(varsInSome.get(p), oneNodeList);
            }
            this.setContextMap(permMap);
            List<Node> nullIfFalseList = visit(ctx.cond());
            if (nullIfFalseList != null){
                return nullIfFalseList;
            }
        }
        return null;
    }

    @Override
    public List<Node> visitEmptyCond(XQueryParser.EmptyCondContext ctx) {

        List<Node> res;

        // create a non-empty list for positive return
        Node oneNode = tmpDoc.createTextNode("random");
        LinkedList<Node> oneNodeList = new LinkedList<>();
        oneNodeList.add(oneNode);

        setContextMap(contextMap);
        res = visit(ctx.xq());
        ///TODO: BugFix, res.size() != 0 means NOT empty: should be TRUE [fixed]
        if (res.size() != 0) return null;  // false
        return oneNodeList;   // true
    }

    @Override
    public List<Node> visitAndCond(XQueryParser.AndCondContext ctx) {
        Map<String,List<Node>> currentContext = this.contextMap;
        setContextMap(currentContext);
        boolean bL = visit(ctx.cond(0)) != null; // not null -true /null -false
        setContextMap(currentContext);
        boolean bR = visit(ctx.cond(1)) != null; // not null -true
        return bL && bR ? new LinkedList<>() : null;
    }

    @Override
    public List<Node> visitIsCond(XQueryParser.IsCondContext ctx) {

        LinkedList<Node> res = new LinkedList<>();
        Map<String, List<Node>> currentCtxMap = contextMap;

        setContextMap(currentCtxMap);
        List<Node> l = visit(ctx.xq(0));
        setContextMap(currentCtxMap);
        List<Node> r = visit(ctx.xq(1));

        for (Node ln: l) {
            for (Node rn: r) {
                if (ln.isSameNode(rn)) {
                    res.add(ln); // only storing one node is enough
                    return res;  // true
                }
            }
        }
        return null; // false
    }

    @Override
    public List<Node> visitEqCond(XQueryParser.EqCondContext ctx) {
        LinkedList<Node> res = new LinkedList<>();
        Map<String, List<Node>> currentCtxMap = contextMap;

        setContextMap(currentCtxMap);
        List<Node> l = visit(ctx.xq(0));
        setContextMap(currentCtxMap);
        List<Node> r = visit(ctx.xq(1));

        for (Node ln: l) {
            for (Node rn: r) {
                if (ln.isEqualNode(rn)) {
                    res.add(ln); // only storing one node is enough
                    return res;  // true
                }
            }
        }
        return null; // false
    }

    @Override
    public List<Node> visitNotCond(XQueryParser.NotCondContext ctx) {
        boolean flag = visit(ctx.cond()) != null;
        return flag ? null : new LinkedList<>();

    }




    // Attention: this method will never be called.
    @Override
    public List<Node> visitStartTag(XQueryParser.StartTagContext ctx) {
        return super.visitStartTag(ctx);
    }

    // Attention: this method will never be called.
    @Override
    public List<Node> visitEndTag(XQueryParser.EndTagContext ctx) {
        return super.visitEndTag(ctx);
    }


    // Attention: this method will never be called.
    @Override
    public List<Node> visitVar(XQueryParser.VarContext ctx) {
        return super.visitVar(ctx);
    }








    // TODO: Read this! Attention: methods below should never be called! Do NOT change.

    @Override
    public List<Node> visitSingleAP(XQueryParser.SingleAPContext ctx) {
        return super.visitSingleAP(ctx);
    }

    @Override
    public List<Node> visitDoubleAP(XQueryParser.DoubleAPContext ctx) {
        return super.visitDoubleAP(ctx);
    }

    @Override
    public List<Node> visitDoc(XQueryParser.DocContext ctx) {
        return super.visitDoc(ctx);
    }

    @Override
    public List<Node> visitAttrRP(XQueryParser.AttrRPContext ctx) {
        return super.visitAttrRP(ctx);
    }

    @Override
    public List<Node> visitDoubleSlashRP(XQueryParser.DoubleSlashRPContext ctx) {
        return super.visitDoubleSlashRP(ctx);
    }

    @Override
    public List<Node> visitTextRP(XQueryParser.TextRPContext ctx) {
        return super.visitTextRP(ctx);
    }

    @Override
    public List<Node> visitParentRP(XQueryParser.ParentRPContext ctx) {
        return super.visitParentRP(ctx);
    }

    @Override
    public List<Node> visitSelfRP(XQueryParser.SelfRPContext ctx) {
        return super.visitSelfRP(ctx);
    }

    @Override
    public List<Node> visitFilterRP(XQueryParser.FilterRPContext ctx) {
        return super.visitFilterRP(ctx);
    }

    @Override
    public List<Node> visitCommaRP(XQueryParser.CommaRPContext ctx) {
        return super.visitCommaRP(ctx);
    }

    @Override
    public List<Node> visitChildrenRP(XQueryParser.ChildrenRPContext ctx) {
        return super.visitChildrenRP(ctx);
    }

    @Override
    public List<Node> visitTagRP(XQueryParser.TagRPContext ctx) {
        return super.visitTagRP(ctx);
    }

    @Override
    public List<Node> visitBracketRP(XQueryParser.BracketRPContext ctx) {
        return super.visitBracketRP(ctx);
    }

    @Override
    public List<Node> visitSingleSlashRP(XQueryParser.SingleSlashRPContext ctx) {
        return super.visitSingleSlashRP(ctx);
    }

    @Override
    public List<Node> visitEqFilter(XQueryParser.EqFilterContext ctx) {
        return super.visitEqFilter(ctx);
    }

    @Override
    public List<Node> visitNotFilter(XQueryParser.NotFilterContext ctx) {
        return super.visitNotFilter(ctx);
    }

    @Override
    public List<Node> visitAndFilter(XQueryParser.AndFilterContext ctx) {
        return super.visitAndFilter(ctx);
    }

    @Override
    public List<Node> visitBracketFilter(XQueryParser.BracketFilterContext ctx) {
        return super.visitBracketFilter(ctx);
    }

    @Override
    public List<Node> visitIsFilter(XQueryParser.IsFilterContext ctx) {
        return super.visitIsFilter(ctx);
    }

    @Override
    public List<Node> visitRpFilter(XQueryParser.RpFilterContext ctx) {
        return super.visitRpFilter(ctx);
    }

    @Override
    public List<Node> visitOrFilter(XQueryParser.OrFilterContext ctx) {
        return super.visitOrFilter(ctx);
    }

    @Override
    public List<Node> visitTagName(XQueryParser.TagNameContext ctx) {
        return super.visitTagName(ctx);
    }

    @Override
    public List<Node> visitAttrName(XQueryParser.AttrNameContext ctx) {
        return super.visitAttrName(ctx);
    }

    @Override
    public List<Node> visitFileName(XQueryParser.FileNameContext ctx) {
        return super.visitFileName(ctx);
    }
}
