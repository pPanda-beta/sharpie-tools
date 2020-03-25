package ppanda.sharpie.tools.interfacewrapper.processors.utils;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import java.util.stream.Collectors;

import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;

public class MethodUtils {
    public static MethodCallExpr delegatingCallExpr(MethodDeclaration method, String fieldNameOfUnderlyingIFace) {
        NodeList<Expression> arguments = method.getParameters().stream().map(NodeWithSimpleName::getNameAsExpression)
            .collect(Collectors.toCollection(NodeList::new));
        return new MethodCallExpr(new NameExpr(fieldNameOfUnderlyingIFace), method.getName(), arguments);
    }

    public static void setBodyAsStatement(MethodDeclaration method, MethodCallExpr callExpr) {
        method.setModifiers(PUBLIC);
        if (method.getType().isVoidType()) {
            method.setBody(new BlockStmt(new NodeList<>(new ExpressionStmt(callExpr))));
            return;
        }
        ReturnStmt returnStmt = new ReturnStmt(callExpr);
        method.setBody(new BlockStmt(new NodeList<>(returnStmt)));
    }

}
