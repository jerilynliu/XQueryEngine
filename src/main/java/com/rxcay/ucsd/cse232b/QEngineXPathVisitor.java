package com.rxcay.ucsd.cse232b;

import com.rxcay.ucsd.cse232b.antlr4.XPathBaseVisitor;
import com.rxcay.ucsd.cse232b.antlr4.XPathParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.LinkedHashSet;
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
    void setPNodes(List<Node> origin){
        paramNodes = new LinkedList<>(origin);
    }

    // helper function to get all strict descendents of a node
    public LinkedList<Node> getDescendents(Node node) {

        LinkedList<Node> res = new LinkedList<>(); // result

        NodeList childrenNodes = node.getChildNodes();
        for (int i = 0; i < childrenNodes.getLength(); i++) {
            Node curtChild = childrenNodes.item(i);
            res.add(curtChild); // add a child
            res.addAll(getDescendents(curtChild));
        }
        return res;
    }

    // helper function: set the paramNodes to be the descendents-and-self of the original paramNodes, and then perform whatever visit action intended.
    public List<Node> visitDoubleSlash(XPathParser.RpContext ctx) {

        List<Node> currentCtxPNodes = paramNodes;
        setPNodes(currentCtxPNodes);

        // store all the descendents of paramNodes into tmp
        LinkedList<Node> tmp = new LinkedList<>();
        for (Node node : paramNodes) {
            tmp.addAll(getDescendents(node));
        }

        // add all the descendents, so that paramNodes now becomes descendents-and-self of the original paramNodes
        for (Node node : tmp) {
            if (!paramNodes.contains(node)) {  // Edge Case => The query is doc("file")//A//A, and the DOM tree consists only A nodes.
                paramNodes.add(node);
            }
        }

        List<Node> currentCtxPNodes2 = paramNodes;
        setPNodes(currentCtxPNodes2);
        return visit(ctx);  // now the paramNodes (the to-be operational context nodes) are successfully updated, perform whatever visit action intended.
    }

    @Override
    public List<Node> visitDoc(XPathParser.DocContext ctx) {
        try {
            return XMLProcessor.checkFileNameAndGetNodes(ctx.fileName().FILENAME().getText());
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
        List<Node> resDoc = visit(ctx.doc()); // Only the document root node is in the list.
        setPNodes(resDoc);
        return visitDoubleSlash(ctx.rp());
    }

    @Override
    public List<Node> visitTagRP(XPathParser.TagRPContext ctx) {

        LinkedList<Node> res = new LinkedList<>();
        String targetTagName = ctx.tagName().ID().getText();
        for (Node node : paramNodes) {
            NodeList children = node.getChildNodes();
            // iterate the children to find the nodes with the right tag
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(targetTagName)){
                    res.add(child);
                }
            }
        }

        return res;
    }

    @Override
    public List<Node> visitChildrenRP(XPathParser.ChildrenRPContext ctx) {

        LinkedList<Node> res = new LinkedList<>();

        for (Node node : paramNodes) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                res.add(child);
            }
        }
        return res;

    }

    @Override
    public List<Node> visitSelfRP(XPathParser.SelfRPContext ctx) {
        return paramNodes;
    }

    @Override
    public List<Node> visitParentRP(XPathParser.ParentRPContext ctx) {

        LinkedList<Node> res = new LinkedList<>();

        for (Node node : paramNodes) {
            Node parentNode = node.getParentNode();
            if (parentNode != null && !res.contains(parentNode)) {
                res.add(parentNode);
            }
        }
        return res;
    }

    @Override
    public List<Node> visitTextRP(XPathParser.TextRPContext ctx) {

        LinkedList<Node> res = new LinkedList<>();

        for (Node node : paramNodes) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {  // get all TextNode
                    res.add(child);
                }
            }
        }
        return res;
    }

    // not sure about this one
    @Override
    public List<Node> visitAttrRP(XPathParser.AttrRPContext ctx) {

        LinkedList<Node> res = new LinkedList<>();

        for (Node node : paramNodes) {

            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            NamedNodeMap attributes = node.getAttributes(); // get all attributes of a node
            for (int i = 0; i < attributes.getLength(); i++) {
                res.add(attributes.item(i));
            }
        }

        setPNodes(res);
        return visit(ctx.attrName());

    }

    @Override
    public List<Node> visitBracketRP(XPathParser.BracketRPContext ctx) {

        List<Node> currentCtxPNodes = paramNodes;
        setPNodes(currentCtxPNodes);
        return visit(ctx.rp());

    }

    @Override
    public List<Node> visitSingleSlashRP(XPathParser.SingleSlashRPContext ctx) {

        List<Node> currentCtxPNodes = paramNodes;
        setPNodes(currentCtxPNodes);
        List<Node> res1 = visit(ctx.rp(0));
        setPNodes(res1);
        List<Node> res2 = visit(ctx.rp(1));

        // remove duplicates
        LinkedHashSet<Node> lhs = new LinkedHashSet<>(res2);
        return new LinkedList<>(lhs);

    }

    @Override
    public List<Node> visitDoubleSlashRP(XPathParser.DoubleSlashRPContext ctx) {

        List<Node> currentCtxPNodes = paramNodes;
        setPNodes(currentCtxPNodes);
        List<Node> res1 = visit(ctx.rp(0));
        setPNodes(res1);
        List<Node> res2 = visitDoubleSlash(ctx.rp(1));

        // remove duplicates
        LinkedHashSet<Node> lhs = new LinkedHashSet<>(res2);
        return new LinkedList<>(lhs);

    }

    @Override
    public List<Node> visitFilterRP(XPathParser.FilterRPContext ctx) {

        List<Node> currentCtxPNodes = paramNodes;
        setPNodes(currentCtxPNodes);
        List<Node> res = visit(ctx.rp());
        setPNodes(res);
        return visit(ctx.f());

    }

    @Override
    public List<Node> visitCommaRP(XPathParser.CommaRPContext ctx) {

        List<Node> currentCtxPNodes = paramNodes;
        setPNodes(currentCtxPNodes);
        List<Node> res1 = visit(ctx.rp(0));
        setPNodes(currentCtxPNodes);                 // reset paramNodes to original state
        List<Node> res2 = visit(ctx.rp(1));

        res1.addAll(res2);
        return res1;

    }

    // never called. visit return at tagRP
    @Override
    public List<Node> visitTagName(XPathParser.TagNameContext ctx) {
        return super.visitTagName(ctx);
    }

    // never called. visit return at attrRP/
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
