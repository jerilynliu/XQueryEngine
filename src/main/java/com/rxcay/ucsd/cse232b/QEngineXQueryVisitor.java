package com.rxcay.ucsd.cse232b;

import com.rxcay.ucsd.cse232b.antlr4.XQueryBaseVisitor;
import com.rxcay.ucsd.cse232b.antlr4.XQueryParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 2/20/22 12:28 PM
 * @description
 */
public class QEngineXQueryVisitor extends XQueryBaseVisitor<List<Node>>{
    private final Document tmpDoc;
    private Map<String,List<Node>> contextMap = new HashMap<>();

    void setContextMap(Map<String,List<Node>> c) {
        this.contextMap = new HashMap<>(c);
    }
    //TODO:need another getDescendents method here to parse #doubleSlashXQ. Get proper pNodes then call XPathEvaluator.


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
        return super.visitFLWR(ctx);
    }

    @Override
    public List<Node> visitSingleSlashXQ(XQueryParser.SingleSlashXQContext ctx) {
        return super.visitSingleSlashXQ(ctx);
    }

    @Override
    public List<Node> visitTagXQ(XQueryParser.TagXQContext ctx) {
        return super.visitTagXQ(ctx);
    }

    @Override
    public List<Node> visitApXQ(XQueryParser.ApXQContext ctx) {
        return super.visitApXQ(ctx);
    }

    @Override
    public List<Node> visitLetXQ(XQueryParser.LetXQContext ctx) {
        return super.visitLetXQ(ctx);
    }

    @Override
    public List<Node> visitCommaXQ(XQueryParser.CommaXQContext ctx) {
        return super.visitCommaXQ(ctx);
    }

    @Override
    public List<Node> visitVarXQ(XQueryParser.VarXQContext ctx) {
        return super.visitVarXQ(ctx);
    }

    @Override
    public List<Node> visitScXQ(XQueryParser.ScXQContext ctx) {
        return super.visitScXQ(ctx);
    }

    @Override
    public List<Node> visitBraceXQ(XQueryParser.BraceXQContext ctx) {
        return super.visitBraceXQ(ctx);
    }

    @Override
    public List<Node> visitDoubleSlashXQ(XQueryParser.DoubleSlashXQContext ctx) {
        return super.visitDoubleSlashXQ(ctx);
    }

    @Override
    public List<Node> visitForClause(XQueryParser.ForClauseContext ctx) {
        return super.visitForClause(ctx);
    }

    @Override
    public List<Node> visitLetClause(XQueryParser.LetClauseContext ctx) {
        return super.visitLetClause(ctx);
    }

    @Override
    public List<Node> visitWhereClause(XQueryParser.WhereClauseContext ctx) {
        return super.visitWhereClause(ctx);
    }

    @Override
    public List<Node> visitReturnClause(XQueryParser.ReturnClauseContext ctx) {
        return super.visitReturnClause(ctx);
    }

    @Override
    public List<Node> visitBraceCond(XQueryParser.BraceCondContext ctx) {
        return super.visitBraceCond(ctx);
    }

    @Override
    public List<Node> visitOrCond(XQueryParser.OrCondContext ctx) {
        return super.visitOrCond(ctx);
    }

    @Override
    public List<Node> visitSatisfyCond(XQueryParser.SatisfyCondContext ctx) {
        return super.visitSatisfyCond(ctx);
    }

    @Override
    public List<Node> visitEmptyCond(XQueryParser.EmptyCondContext ctx) {
        return super.visitEmptyCond(ctx);
    }

    @Override
    public List<Node> visitAndCond(XQueryParser.AndCondContext ctx) {
        return super.visitAndCond(ctx);
    }

    @Override
    public List<Node> visitIsCond(XQueryParser.IsCondContext ctx) {
        return super.visitIsCond(ctx);
    }

    @Override
    public List<Node> visitEqCond(XQueryParser.EqCondContext ctx) {
        return super.visitEqCond(ctx);
    }

    @Override
    public List<Node> visitNotCond(XQueryParser.NotCondContext ctx) {
        return super.visitNotCond(ctx);
    }

    @Override
    public List<Node> visitStartTag(XQueryParser.StartTagContext ctx) {
        return super.visitStartTag(ctx);
    }

    @Override
    public List<Node> visitEndTag(XQueryParser.EndTagContext ctx) {
        return super.visitEndTag(ctx);
    }

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
