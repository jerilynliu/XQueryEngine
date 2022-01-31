package com.rxcay.ucsd.cse232b;

import com.rxcay.ucsd.cse232b.antlr4.XPathBaseVisitor;
import com.rxcay.ucsd.cse232b.antlr4.XPathParser;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/29/22 3:39 PM
 * @description
 */
public class QEngineXPathVisitor extends XPathBaseVisitor<List<Node>> {

    private List<Node> paramNodes = new LinkedList<>();

    // Attention: param nodes are set in a value-based way. Any callee can modify or return it exclusively.
    private void setPNodes(List<Node> origin){
        paramNodes = new LinkedList<>(origin);
    }


    @Override
    public List<Node> visitDoc(XPathParser.DocContext ctx) {
        try {
            return XMLProcessor.checkFileNameAndGetNodes(ctx.fileName().getText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Node> visitSingleAP(XPathParser.SingleAPContext ctx) {
        // no setPNodes since doc nodes have not been loaded.
        List<Node> resDoc = visit(ctx.doc());
        setPNodes(resDoc);
        return visit(ctx.rp());

    }

    @Override
    public List<Node> visitDoubleAP(XPathParser.DoubleAPContext ctx) {
        return super.visitDoubleAP(ctx);
    }

    @Override
    public List<Node> visitTagRP(XPathParser.TagRPContext ctx) {
        return super.visitTagRP(ctx);
    }

    @Override
    public List<Node> visitChildrenRP(XPathParser.ChildrenRPContext ctx) {
        return super.visitChildrenRP(ctx);
    }

    @Override
    public List<Node> visitSelfRP(XPathParser.SelfRPContext ctx) {
        return super.visitSelfRP(ctx);
    }

    @Override
    public List<Node> visitParentRP(XPathParser.ParentRPContext ctx) {
        return super.visitParentRP(ctx);
    }

    @Override
    public List<Node> visitTextRP(XPathParser.TextRPContext ctx) {
        return super.visitTextRP(ctx);
    }

    @Override
    public List<Node> visitAttrRP(XPathParser.AttrRPContext ctx) {
        return super.visitAttrRP(ctx);
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
    public List<Node> visitDoubleSlashRP(XPathParser.DoubleSlashRPContext ctx) {
        return super.visitDoubleSlashRP(ctx);
    }

    @Override
    public List<Node> visitFilterRP(XPathParser.FilterRPContext ctx) {
        return super.visitFilterRP(ctx);
    }

    @Override
    public List<Node> visitCommaRP(XPathParser.CommaRPContext ctx) {
        List<Node> currentCtxPNodes = paramNodes;
        setPNodes(currentCtxPNodes);
        List<Node> res1 = visit(ctx.rp(0));
        setPNodes(currentCtxPNodes);
        List<Node> res2 = visit(ctx.rp(1));
        res1.addAll(res2);
        return res1;
    }

    @Override
    public List<Node> visitTagName(XPathParser.TagNameContext ctx) {
        return super.visitTagName(ctx);
    }

    @Override
    public List<Node> visitAttrName(XPathParser.AttrNameContext ctx) {
        return super.visitAttrName(ctx);
    }

    // never called. visit return at visitDoc.
    @Override
    public List<Node> visitFileName(XPathParser.FileNameContext ctx) {
        return super.visitFileName(ctx);
    }

    @Override
    public List<Node> visitEqFilter(XPathParser.EqFilterContext ctx) {
        List<Node> origin = paramNodes;
        return filterCollectVisitHelper(origin,
                node -> {
                    List<Node> oneNodeList = new LinkedList<>();
                    oneNodeList.add(node);
                    setPNodes(oneNodeList);
                    List<Node> res1 = visit(ctx.rp(0));
                    setPNodes(oneNodeList);
                    List<Node> res2 = visit(ctx.rp(1));
                    for (Node x : res1) {
                        for (Node y: res2) {
                            if (x.isEqualNode(y)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
        );
    }

    @Override
    public List<Node> visitNotFilter(XPathParser.NotFilterContext ctx) {
        List<Node> origin = paramNodes;
        setPNodes(origin);
        List<Node> filteredF = visit(ctx.f());
        HashSet<Node> s = new HashSet<>(filteredF);
        return filterCollectVisitHelper(origin, node -> !s.contains(node));
    }

    @Override
    public List<Node> visitAndFilter(XPathParser.AndFilterContext ctx) {
        List<Node> origin = paramNodes;
        setPNodes(origin);
        List<Node> filteredWithF1 = visit(ctx.f(0));
        setPNodes(filteredWithF1);
        return visit(ctx.f(1));
    }

    @Override
    public List<Node> visitBracketFilter(XPathParser.BracketFilterContext ctx) {
        return visit(ctx.f());
    }

    @Override
    public List<Node> visitIsFilter(XPathParser.IsFilterContext ctx) {
        List<Node> origin = paramNodes;
        return filterCollectVisitHelper(origin,
                node -> {
                    List<Node> oneNodeList = new LinkedList<>();
                    oneNodeList.add(node);
                    setPNodes(oneNodeList);
                    List<Node> res1 = visit(ctx.rp(0));
                    setPNodes(oneNodeList);
                    List<Node> res2 = visit(ctx.rp(1));
                    for (Node x : res1) {
                        for (Node y: res2) {
                            if (x.isSameNode(y)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
        );
    }

    private List<Node> filterCollectVisitHelper(List<Node> origin, Predicate<Node> rule) {
        return origin.stream()
                .filter(rule)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Node> visitRpFilter(XPathParser.RpFilterContext ctx) {
        List<Node> origin = paramNodes;
        return filterCollectVisitHelper(origin,
                node -> {
                    List<Node> oneNodeList = new LinkedList<>();
                    oneNodeList.add(node);
                    setPNodes(oneNodeList);
                    List<Node> res = visit(ctx.rp());
                    return res.size() > 0;
                }
        );
    }

    @Override
    public List<Node> visitOrFilter(XPathParser.OrFilterContext ctx) {
        List<Node> origin = paramNodes;
        setPNodes(origin);
        List<Node> rf1 = visit(ctx.f(0));
        setPNodes(origin);
        List<Node> rf2 = visit(ctx.f(1));
        HashSet<Node> s1 = new HashSet<>(rf1);
        HashSet<Node> s2 = new HashSet<>(rf2);
        return filterCollectVisitHelper(origin,
                node -> s1.contains(node) || s2.contains(node));
    }

}
