package com.rxcay.ucsd.cse232b;
import com.rxcay.ucsd.cse232b.antlr4.*;
import org.w3c.dom.Node;

import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/29/22 3:39 PM
 * @description
 */
public class QEngineXPathVisitor extends XPathBaseVisitor<List<Node>> {
    @Override
    public List<Node> visitSingleAP(XPathParser.SingleAPContext ctx) {
        return super.visitSingleAP(ctx);
    }

    @Override
    public List<Node> visitDoubleAP(XPathParser.DoubleAPContext ctx) {
        return super.visitDoubleAP(ctx);
    }

    @Override
    public List<Node> visitDoc(XPathParser.DocContext ctx) {
        return super.visitDoc(ctx);
    }

    @Override
    public List<Node> visitAttrRP(XPathParser.AttrRPContext ctx) {
        return super.visitAttrRP(ctx);
    }

    @Override
    public List<Node> visitDoubleSlashRP(XPathParser.DoubleSlashRPContext ctx) {
        return super.visitDoubleSlashRP(ctx);
    }

    @Override
    public List<Node> visitTextRP(XPathParser.TextRPContext ctx) {
        return super.visitTextRP(ctx);
    }

    @Override
    public List<Node> visitParentRP(XPathParser.ParentRPContext ctx) {
        return super.visitParentRP(ctx);
    }

    @Override
    public List<Node> visitSelfRP(XPathParser.SelfRPContext ctx) {
        return super.visitSelfRP(ctx);
    }

    @Override
    public List<Node> visitFilterRP(XPathParser.FilterRPContext ctx) {
        return super.visitFilterRP(ctx);
    }

    @Override
    public List<Node> visitCommaRP(XPathParser.CommaRPContext ctx) {
        return super.visitCommaRP(ctx);
    }

    @Override
    public List<Node> visitChildrenRP(XPathParser.ChildrenRPContext ctx) {
        return super.visitChildrenRP(ctx);
    }

    @Override
    public List<Node> visitTagRP(XPathParser.TagRPContext ctx) {
        return super.visitTagRP(ctx);
    }

    @Override
    public List<Node> visitBracketRP(XPathParser.BracketRPContext ctx) {
        return super.visitBracketRP(ctx);
    }

    @Override
    public List<Node> visitSingleSlashRP(XPathParser.SingleSlashRPContext ctx) {
        return super.visitSingleSlashRP(ctx);
    }

    @Override
    public List<Node> visitEqFilter(XPathParser.EqFilterContext ctx) {
        return super.visitEqFilter(ctx);
    }

    @Override
    public List<Node> visitNotFilter(XPathParser.NotFilterContext ctx) {
        return super.visitNotFilter(ctx);
    }

    @Override
    public List<Node> visitAndFilter(XPathParser.AndFilterContext ctx) {
        return super.visitAndFilter(ctx);
    }

    @Override
    public List<Node> visitBracketFilter(XPathParser.BracketFilterContext ctx) {
        return super.visitBracketFilter(ctx);
    }

    @Override
    public List<Node> visitIsFilter(XPathParser.IsFilterContext ctx) {
        return super.visitIsFilter(ctx);
    }

    @Override
    public List<Node> visitRpFilter(XPathParser.RpFilterContext ctx) {
        return super.visitRpFilter(ctx);
    }

    @Override
    public List<Node> visitOrFilter(XPathParser.OrFilterContext ctx) {
        return super.visitOrFilter(ctx);
    }

    @Override
    public List<Node> visitTagName(XPathParser.TagNameContext ctx) {
        return super.visitTagName(ctx);
    }

    @Override
    public List<Node> visitAttrName(XPathParser.AttrNameContext ctx) {
        return super.visitAttrName(ctx);
    }

    @Override
    public List<Node> visitFileName(XPathParser.FileNameContext ctx) {
        return super.visitFileName(ctx);
    }
}
